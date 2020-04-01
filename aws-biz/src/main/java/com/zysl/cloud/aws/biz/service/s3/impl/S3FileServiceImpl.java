package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.utils.DataAuthUtils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("s3FileService")
public class S3FileServiceImpl implements IS3FileService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private DataAuthUtils dataAuthUtils;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		log.info("s3file.create.param:{}", JSON.toJSONString(t));
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

        List<TagBO> oldTagList = this.getTags(t);
		if(!CollectionUtils.isEmpty(t.getTagList())){
			//合并标签集合
			oldTagList = mergeTags(oldTagList, t.getTagList());
		}
		List<Tag> tagSet = Lists.newArrayList();
		oldTagList.forEach(obj -> {
			tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
		});
		//设置标签信息
		Tagging tagging = CollectionUtils.isEmpty(tagSet) ? null : Tagging.builder().tagSet(tagSet).build();

		PutObjectRequest request = null;
		if(null == tagging){
			request = PutObjectRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName()))
					.contentEncoding(t.getContentEncoding())
					.expires(t.getExpires() == null ? null : t.getExpires().toInstant())
					.build();
		}else{
			request = PutObjectRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName()))
					.contentEncoding(t.getContentEncoding())
					.tagging(tagging)
					.expires(t.getExpires() == null ? null : t.getExpires().toInstant())
					.build();
		}

		PutObjectResponse response = s3FactoryService.callS3MethodWithBody(request,RequestBody.fromBytes(t.getBodys()),s3Client, S3Method.PUT_OBJECT);
		log.info("s3file.create.response:{}", response);

		t.setVersionId(response.versionId());
		return t;
	}

	@Override
	public List<TagBO> mergeTags(List<TagBO> oldTagList, List<TagBO> tagList){
		List<TagBO> newTagList = Lists.newArrayList();

		//将新标签集合转成map
        Map<String, String> tagMap = tagList.stream().collect(Collectors.toMap(TagBO::getKey, TagBO::getValue));
        //遍历新增的标签和已有标签，标签key相同，则修改原有标签，标签key新增没有则保留

		if(CollectionUtils.isEmpty(oldTagList)){
			return tagList;
		}else{
			List<TagBO> removalList = oldTagList.stream().filter(obj -> StringUtils.isEmpty(tagMap.get(obj.getKey()))).collect(Collectors.toList());
			//将去重之后的集合和新添加的标签集合合并
			tagList.addAll(removalList);
			return tagList;
		}
	}

	@Override
	public List<TagBO> addTags(S3ObjectBO t, List<TagBO> tagList){
		List<TagBO> newTagList = Lists.newArrayList();

		//将新标签集合转成map
        Map<String, String> tagMap = tagList.stream().collect(Collectors.toMap(TagBO::getKey, TagBO::getValue));

        //查询对象原有标签
		List<TagBO> oldTagList = this.getTags(t);

		//遍历新增的标签和已有标签，标签key相同，则修改原有标签，标签key新增没有则保留
        List<TagBO> removalList = oldTagList.stream().filter(obj -> StringUtils.isEmpty(tagMap.get(obj.getKey()))).collect(Collectors.toList());
        //将去重之后的集合和新添加的标签集合合并
        tagList.addAll(removalList);

		return tagList;
	}

	@Override
	public void delete(S3ObjectBO t){
		log.info("s3file.delete.param:{}", JSON.toJSONString(t));

		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		DeleteObjectsRequest deleteObjectsRequest = null;
		if(DeleteStoreEnum.COVER.getCode().equals(t.getDeleteStore())){
			if(StringUtils.isEmpty(t.getVersionId())){
				//删除整个文件信息, 先查询文件的版本信息
				List<S3ObjectBO> objectList = getVersions(t);

				List<ObjectIdentifier> objects = new ArrayList<>();
				//查询文件的版本信息
				objectList.forEach(obj -> {
					ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
							.key(obj.getFileName())
							.versionId(obj.getVersionId()).build();
					objects.add(objectIdentifier);
				});
				//删除列表
				Delete delete = Delete.builder().objects(objects).build();
				//逻辑删除
				deleteObjectsRequest = DeleteObjectsRequest.builder()
						.bucket(t.getBucketName())
						.delete(delete)
						.build();
			}else{
				//删除文件指定版本信息
				ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
						.key(StringUtils.join(t.getPath() ,t.getFileName()))
						.versionId(t.getVersionId()).build();
				List<ObjectIdentifier> objects = new ArrayList<>();
				objects.add(objectIdentifier);
				Delete delete = Delete.builder().objects(objects).build();

				//逻辑删除
				deleteObjectsRequest = DeleteObjectsRequest.builder()
						.bucket(t.getBucketName())
						.delete(delete)
						.build();
			}
		}else{
			ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
					.key(StringUtils.join(t.getPath() ,t.getFileName())).build();
			List<ObjectIdentifier> objects = new ArrayList<>();
			objects.add(objectIdentifier);
			Delete delete = Delete.builder().objects(objects).build();

			//逻辑删除
			deleteObjectsRequest = DeleteObjectsRequest.builder()
					.bucket(t.getBucketName())
					.delete(delete)
					.build();
		}
		//文件删除
		DeleteObjectsResponse response = s3FactoryService.callS3Method(deleteObjectsRequest, s3, S3Method.DELETE_OBJECTS);
		log.info("--delete文件删除返回；{}--", response);
	}

	@Override
	public void modify(S3ObjectBO t){
		log.info("s3file.modify.param:{}", JSON.toJSONString(t));

		//目前修改文件标签信息
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//设置标签入参
		List<TagBO> tageList = t.getTagList();
		//文件tage设置参数
		PutObjectTaggingRequest.Builder request = PutObjectTaggingRequest.builder()
				.bucket(t.getBucketName())
				.key(StringUtils.join(t.getPath() ,t.getFileName()));

		List<Tag> tagSet = new ArrayList<>();
		if(!CollectionUtils.isEmpty(tageList)){
			tageList.forEach(obj -> {
				tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
			});
			Tagging tagging = Tagging.builder().tagSet(tagSet).build();
			//设置标签信息
			request.tagging(tagging);
		}
		//设置版本
		if(!StringUtils.isEmpty(t.getVersionId())){
			request.versionId(t.getVersionId());
		}

		PutObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(),s3,S3Method.PUT_OBJECT_TAGGING);
		log.info("s3file.modify.param:{}", response);

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public S3ObjectBO copy(S3ObjectBO src,S3ObjectBO dest){
		log.info("s3file.create.param.src:{}, dest:{}", JSON.toJSONString(src), JSON.toJSONString(dest));

		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(src.getBucketName());

		//获取标签内容
		List<Tag> tagSet = new ArrayList<>();
		Tag tag = Tag.builder().key(S3TagKeyEnum.FILE_NAME.getCode()).value(dest.getFileName()).build();
		tagSet.add(tag);

		if(!CollectionUtils.isEmpty(dest.getTagList())){
			dest.getTagList().forEach(obj -> {
				tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
			});
		}

		String copySourceUrl = null;
		try{
			copySourceUrl = java.net.URLEncoder.encode(src.getBucketName() + "/" + src.getPath() + src.getFileName(), "utf-8");
		}catch (Exception e){
			throw new AppLogicException(ErrCodeEnum.S3_COPY_SOURCE_ENCODE_ERROR.getCode());
		}

		//查询复制接口入参
		CopyObjectRequest request = null;
		if(CollectionUtils.isEmpty(tagSet)){
			request = CopyObjectRequest.builder()
					.copySource(copySourceUrl)
					.bucket(dest.getBucketName())
					.key(StringUtils.join(dest.getPath() ,dest.getFileName()))
					.build();
		}else{
			Tagging tagging = Tagging.builder().tagSet(tagSet).build();
			request = CopyObjectRequest.builder()
					.copySource(copySourceUrl)
					.bucket(dest.getBucketName())
					.key(StringUtils.join(dest.getPath() ,dest.getFileName()))
					.tagging(tagging)
					.taggingDirective(TaggingDirective.REPLACE)
					.build();
		}




		CopyObjectResponse response = s3FactoryService.callS3Method(request,s3,S3Method.COPY_OBJECT);
		log.info("s3file.copy.response:{}", response);
		dest.setVersionId(response.versionId());

		return dest;
	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){
		log.info("s3file.move.param.src:{},dest:{}",  JSON.toJSONString(src),  JSON.toJSONString(dest));
		//先复制文件
		this.copy(src, dest);
		//在删除文件
		this.delete(src);
	}

	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		log.info("s3file.getBaseInfo.param:{}", JSON.toJSONString(t));
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		HeadObjectRequest request = null;
		if(StringUtils.isEmpty(t.getVersionId())){
			request = HeadObjectRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName())).build();
		}else{
			request = HeadObjectRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName()))
					.versionId(t.getVersionId()).build();
		}

		HeadObjectResponse response = s3FactoryService.callS3Method(request, s3Client, S3Method.HEAD_OBJECT);
		log.info("s3file.copy.getBaseInfo:{}", response);

		t.setVersionId(response.versionId());
		t.setContentLength(response.contentLength());
		t.setExpires(DateUtils.from(response.expires()));
		t.setLastModified(DateUtils.from(response.lastModified()));
		t.setContentEncoding(response.contentEncoding());
		t.setContentLanguage(response.contentLanguage());
		t.setContentType(response.contentType());
		t.setContentMD5(response.sseCustomerKeyMD5());
		return t;
	}

	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		log.info("s3file.getDetailInfo.param:{}", JSON.toJSONString(t));

		//查询文件基础信息
		getBaseInfo(t);
		//查询文件标签
		List<TagBO> tagList = getTags(t);
		t.setTagList(tagList);

		return t;
	}

	@Override
	public S3ObjectBO getInfoAndBody(S3ObjectBO t){
		log.info("s3file.getInfoAndBody.param:{}", JSON.toJSONString(t));
		//查询文件基础信息
		getDetailInfo(t);

		//查询文件内容
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		//获取下载对象
		GetObjectRequest request = null;
		if(StringUtils.isEmpty(t.getVersionId())){
			request = GetObjectRequest.builder().
					bucket(t.getBucketName()).key(StringUtils.join(t.getPath() ,t.getFileName())).build();
		}else {
			request = GetObjectRequest.builder().
					bucket(t.getBucketName()).key(StringUtils.join(t.getPath() ,t.getFileName())).
					versionId(t.getVersionId()).build();
		}

		ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(request,
				ResponseTransformer.toBytes());
		GetObjectResponse objectResponse = objectAsBytes.response();

		Date date1 = Date.from(objectResponse.lastModified());
		Date date2 = DateUtils.createDate(bizConfig.DOWNLOAD_TIME);

		byte[] bytes = objectAsBytes.asByteArray();
		log.info("--asByteArray结束时间--:{}", System.currentTimeMillis());
		if(DateUtils.doCompareDate(date1, date2) < 0){
			//进行解码
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] fileContent = new byte[0];
			try {
				fileContent = decoder.decodeBuffer(new String(bytes));
			} catch (IOException e) {
				log.error("--download文件流转换异常：{}--", e);
			}
			t.setBodys(fileContent);
			return t;
		}else {
			t.setBodys(bytes);
			return t;
		}
	}

	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		log.info("s3file.getVersions.param:{}", JSON.toJSONString(t));
		//获取s3初始化对象
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		ListObjectVersionsRequest request = ListObjectVersionsRequest.builder().
				bucket(t.getBucketName()).
				prefix(StringUtils.join(t.getPath() ,t.getFileName())).
				build();

		ListObjectVersionsResponse response = s3FactoryService.callS3Method(request,s3Client, S3Method.LIST_OBJECT_VERSIONS);
		log.info("s3file.getVersions.response:{}", response);

		List<ObjectVersion> list = response.versions();
		List<S3ObjectBO> versionList = Lists.newArrayList();
		list.forEach(obj -> {
			S3ObjectBO s3Object = new S3ObjectBO();
			//版本信息
			s3Object.setVersionId(obj.versionId());
			s3Object.setContentLength(Long.valueOf(obj.size()));
			s3Object.setLastModified(Date.from(obj.lastModified()));
			//文件信息
			s3Object.setBucketName(response.name());
			s3Object.setFileName(response.prefix());
			versionList.add(s3Object);
		});
		return versionList;
	}

	@Override
	public void rename(S3ObjectBO t) {
		log.info("s3file.rename.param:{}", JSON.toJSONString(t));
		//先查询文件内容
		S3ObjectBO s3ObjectBO = this.getInfoAndBody(t);

		t.setBodys(s3ObjectBO.getBodys());
		//在重新上传文件
		this.create(t);

	}

	@Override
	public void checkDataOpAuth(S3ObjectBO s3ObjectBO,String opAuthTypes){
//		S3ObjectBO s3ObjectBO = new S3ObjectBO();
//		s3ObjectBO.setBucketName(bucket);
//		s3ObjectBO.setPath(path);
//		s3ObjectBO.setFileName(fileName);
//		s3ObjectBO.setVersionId(fileVersionId);

		log.info("checkDataOpAuth:param:{},opAuthTypes:{}", JSON.toJSONString(s3ObjectBO),opAuthTypes);
		String objAuths = null;
		// 是对象
		if (StringUtils.isNotBlank(s3ObjectBO.getFileName())) {
			//读取对象的标签--权限列表
			objAuths = getTagValue(getTags(s3ObjectBO),S3TagKeyEnum.USER_AUTH.getCode());
			if(dataAuthUtils.checkAuth(opAuthTypes,objAuths)){
				return;
			}
		}
		//逐级往上检查目录
		String curPath = s3ObjectBO.getPath();
		if(StringUtils.isNotBlank(curPath) && !curPath.endsWith("/")){
			curPath += "/";
		}
		S3ObjectBO bo = s3ObjectBO;
		bo.setBucketName(s3ObjectBO.getBucketName());
		while(StringUtils.isNotBlank(curPath)){
			bo.setPath(curPath);
			objAuths = getTagValue(getTags(s3ObjectBO),S3TagKeyEnum.USER_AUTH.getCode());
			if(dataAuthUtils.checkAuth(opAuthTypes,objAuths)){
				return;
			}
			//截取上层目录
			curPath = curPath.substring(0,curPath.length() - 1);
			if(curPath.lastIndexOf("/") > -1){
				curPath = curPath.substring(0,curPath.lastIndexOf("/") + 1);
			}
		}

		//遍历后还是无法匹配
		log.warn("check.data.op.auth.failed:s3ObjectBO：{},opAuthTypes:{}", JSON.toJSONString(s3ObjectBO),opAuthTypes);
		throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
	}

	@Override
	public List<TagBO> getTags(S3ObjectBO t) {
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		//查询文件的标签信息
		GetObjectTaggingRequest request = null;
		if(StringUtils.isEmpty(t.getVersionId())){
			request = GetObjectTaggingRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName()))
					.build();
		}else{
			request = GetObjectTaggingRequest.builder()
					.bucket(t.getBucketName())
					.key(StringUtils.join(t.getPath() ,t.getFileName()))
					.versionId(t.getVersionId())
					.build();
		}
		GetObjectTaggingResponse response = s3FactoryService.callS3Method(request, s3, S3Method.GET_OBJECT_TAGGING, false);
		log.info("s3file.getDetailInfo.response:{}", response);

		if(null != response){
			List<Tag> list = response.tagSet();
			List<TagBO> tagList = Lists.newArrayList();
			list.forEach(obj -> {
				TagBO tag = new TagBO();
				tag.setKey(obj.key());
				tag.setValue(obj.value());
				tagList.add(tag);
			});
			return tagList;
		}else{
			return Lists.newArrayList();
		}
	}

	@Override
	public String getTagValue(List<TagBO> tagList, String key) {
		for (TagBO tag :tagList) {
			if(key.equals(tag.getKey())){
				return tag.getValue();
			}
		}
		return null;
	}
}
