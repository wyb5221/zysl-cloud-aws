package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("s3FolderService")
public class S3FolderServiceImpl implements IS3FolderService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3FileService fileService;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		log.info("s3folder.create.param:{}", JSON.toJSONString(t));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName(),Boolean.TRUE);

		//先查询标签信息
		List<Tag> tagSet = Lists.newArrayList();
		t.getTagList().forEach(obj -> {
			tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
		});
		//设置标签信息
		Tagging tagging = CollectionUtils.isEmpty(tagSet) ? null : Tagging.builder().tagSet(tagSet).build();

		PutObjectRequest request = null;
		if(null == tagging){
			request = PutObjectRequest.builder().bucket(t.getBucketName()).
					key(t.getPath()).build();
		}else{
			request = PutObjectRequest.builder().bucket(t.getBucketName()).
					key(t.getPath()).tagging(tagging).build();
		}

		RequestBody requestBody = RequestBody.empty();
		PutObjectResponse response = s3FactoryService.callS3MethodWithBody
				(request, requestBody, s3, S3Method.PUT_OBJECT);
		log.info("s3folder.create.response:{}", response);

		t.setVersionId(response.versionId());

		return t;
	}

	//调用s3接口删除目录
	public void getS3DelFile(S3ObjectBO t, S3Client s3){
		//获取删除对象
		List<ObjectIdentifier> objects = new ArrayList<>();
		ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
				.key(t.getPath()).build();
		objects.add(objectIdentifier);
		Delete delete = Delete.builder().objects(objects).build();
		DeleteObjectsRequest request = DeleteObjectsRequest.builder()
				.bucket(t.getBucketName())
				.delete(delete)
				.build();

		DeleteObjectsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.DELETE_OBJECTS);
		log.info("s3folder.delete.response:{}", response);
	}

	@Override
	public void delete(S3ObjectBO t){
		log.info("s3folder.delete.param:{}", JSON.toJSONString(t));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		/**
		 * 先查询目录下的文件信息
		 */
		S3ObjectBO s3ObjectBO = getDetailInfo(t);
		List<ObjectInfoBO> folderList = s3ObjectBO.getFolderList();
		//删除文件
		List<ObjectInfoBO> fileList = s3ObjectBO.getFileList();
		if(delfile(t.getBucketName(), fileList) && operFolder(t.getBucketName(), folderList, s3)){
			getS3DelFile(t, s3);
		}
	}

	//删除文件
	public boolean delfile(String bucket, List<ObjectInfoBO> fileList){
		//删除文件信息
		fileList.forEach(obj -> {
			S3ObjectBO file = new S3ObjectBO();
			file.setBucketName(bucket);
			setPathAndFileName(file, obj.getKey());
			file.setDeleteStore(DeleteStoreEnum.COVER.getCode());
			fileService.delete(file);
		});
		return true;
	}
	//删除目录
	public boolean operFolder(String bucket, List<ObjectInfoBO> folderList, S3Client s3){
		for (ObjectInfoBO object : folderList) {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(bucket);
			setPathAndFileName(t, object.getKey());
			S3ObjectBO s3ObjectBO = getDetailInfo(t);
			List<ObjectInfoBO> files = s3ObjectBO.getFileList();
			//删除文件信息
			files.forEach(obj -> {
				S3ObjectBO file = new S3ObjectBO();
				file.setBucketName(bucket);
				setPathAndFileName(file, obj.getKey());
				file.setDeleteStore(DeleteStoreEnum.COVER.getCode());
				fileService.delete(file);
			});
			//文件夹
			List<ObjectInfoBO> folders = s3ObjectBO.getFolderList();
			if(!CollectionUtils.isEmpty(folders)){
				operFolder(bucket, folders, s3);
			}else{
				//删除文件夹
				S3ObjectBO del = new S3ObjectBO();
				del.setBucketName(bucket);
				setPathAndFileName(del,  object.getKey());
				getS3DelFile(del, s3);
			}
		}
		return true;
	}

	@Override
	public void modify(S3ObjectBO t){

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	/*
	//查询复制接口入参
		CopyObjectRequest request = CopyObjectRequest.builder()
				.copySource(src.getBucketName() + "/" + src.getPath() + src.getFileName())
				.bucket(dest.getBucketName()).key(dest.getPath() + dest.getFileName())
				.build();

		CopyObjectResponse response = s3FactoryService.callS3Method(request,s3,S3Method.COPY_OBJECT);
		log.info("s3folder.copy.response:{}", response);
	 */
	@Override
	public boolean copy(S3ObjectBO src,S3ObjectBO dest){
		log.info("s3folder.move.param.src:{}，dest:{}", JSON.toJSONString(src), JSON.toJSONString(dest));

		/**
		 * 同时复制目录下的所有对象
		 */
		/**
		 * 判断两个bucket是否在同一台服务器，
		 * 不在一台服务器则下载上传，在，则复制
		 */
		if(s3FactoryService.judgeBucket(src.getBucketName(), dest.getBucketName())){
			log.info("s3folder.copy.judgeBucket.返回true,两个bucket在同一台服务器");

			//获取s3初始化对象
			S3Client s3 = s3FactoryService.getS3ClientByBucket(src.getBucketName(),Boolean.TRUE);

			//先复制根目录
			fileService.copy(src, dest);

			//在查询根目录下的对象信息
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(src.getBucketName());
			t.setPath(src.getPath());
			t.setFileName(src.getFileName());
			S3ObjectBO detailInfo = getDetailInfo(t);

			return copyObject(detailInfo, src, dest);
		}else{
			log.info("s3folder.copy.judgeBucket.返回true,两个bucket在同一台服务器");

			//上传根目录
			String destKey = replaceString(src.getPath(), dest.getPath());
			S3ObjectBO dest1 = new S3ObjectBO();
			dest1.setBucketName(dest.getBucketName());
			setPathAndFileName(dest1, destKey);
			//设置标签
			dest1.setTagList(fileService.addTags(src, Lists.newArrayList()));
			this.create(dest1);

			//在查询根目录下的对象信息
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(src.getBucketName());
			t.setPath(src.getPath());
			t.setFileName(src.getFileName());
			S3ObjectBO detailInfo = getDetailInfo(t);
			return uploadObject(detailInfo, src, dest);
		}
	}

	public boolean copyObject(S3ObjectBO detailInfo, S3ObjectBO src,S3ObjectBO dest){
		//子文件
		List<ObjectInfoBO> fileList = detailInfo.getFileList();
		//文件直接复制
		fileList.forEach(file ->{
			String key = file.getKey();
			String destKey = replaceString(key, dest.getPath());
					//key.replace(src.getPath(), dest.getPath());

			S3ObjectBO src1 = new S3ObjectBO();
			src1.setBucketName(src.getBucketName());
			setPathAndFileName(src1, file.getKey());
			S3ObjectBO dest1 = new S3ObjectBO();
			dest1.setBucketName(dest.getBucketName());
			setPathAndFileName(dest1, destKey);
			fileService.copy(src1, dest1);
		});

		//子目录
		List<ObjectInfoBO> folderList = detailInfo.getFolderList();
		folderList.forEach(folder -> {
			String key = folder.getKey();
			String destKey = replaceString(key, dest.getPath());
					//key.replace(src.getPath(), dest.getPath());
			//先复制
			S3ObjectBO src1 = new S3ObjectBO();
			src1.setBucketName(src.getBucketName());
			setPathAndFileName(src1, folder.getKey());
			S3ObjectBO dest1 = new S3ObjectBO();
			dest1.setBucketName(dest.getBucketName());
			setPathAndFileName(dest1, destKey);
			fileService.copy(src1, dest1);

			//在查询
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(src.getBucketName());
			setPathAndFileName(t, folder.getKey());
			S3ObjectBO objects = getDetailInfo(t);

			copyObject(objects, src, dest);
		});
		return true;
	}

	public boolean uploadObject(S3ObjectBO detailInfo, S3ObjectBO src,S3ObjectBO dest){
		//子文件
		List<ObjectInfoBO> fileList = detailInfo.getFileList();
		//文件直接上传
		fileList.forEach(file ->{
			String key = file.getKey();
			String destKey = replaceString(key, dest.getPath());
			//key.replace(src.getPath(), dest.getPath());

			S3ObjectBO src1 = new S3ObjectBO();
			src1.setBucketName(src.getBucketName());
			setPathAndFileName(src1, file.getKey());
			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getInfoAndBody(src1);

			S3ObjectBO dest1 = new S3ObjectBO();
			dest1.setBucketName(dest.getBucketName());
			setPathAndFileName(dest1, destKey);
			dest1.setBodys(s3ObjectBO.getBodys());
			//设置标签信息
			dest1.setTagList(fileService.mergeTags(s3ObjectBO.getTagList(), Lists.newArrayList()));

			fileService.create(dest1);
		});

		//子目录
		List<ObjectInfoBO> folderList = detailInfo.getFolderList();
		folderList.forEach(folder -> {
			String key = folder.getKey();
			String destKey = replaceString(key, dest.getPath());
			//key.replace(src.getPath(), dest.getPath());
			//先复制
			S3ObjectBO src1 = new S3ObjectBO();
			src1.setBucketName(src.getBucketName());
			setPathAndFileName(src1, folder.getKey());


			S3ObjectBO dest1 = new S3ObjectBO();
			dest1.setBucketName(dest.getBucketName());
			setPathAndFileName(dest1, destKey);
			//设置标签
			dest1.setTagList(fileService.addTags(src1, Lists.newArrayList()));
			this.create(dest1);
//			fileService.create(dest1);

			//在查询
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(src.getBucketName());
			setPathAndFileName(t, folder.getKey());
			S3ObjectBO objects = getDetailInfo(t);

			uploadObject(objects, src, dest);
		});
		return true;
	}

	public String replaceString(String source, String target){
        String str = source.split("/")[0];
        String str1 = target + source.substring(str.length() + 1);
        return str1;
	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){
		//先复制
		copy(src, dest);
		//在删除
		S3ObjectBO t = new S3ObjectBO();
		t.setBucketName(src.getBucketName());
		t.setPath(src.getPath());
		t.setFileName(src.getFileName());
		delete(t);
	}
	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		return null;
	}

	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		log.info("s3folder.getDetailInfo.param:{}", JSON.toJSONString(t));
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//获取查询对象列表入参
		ListObjectsRequest request = null;
		if(StringUtils.isEmpty(t.getPath())){
			request = ListObjectsRequest.builder()
					.bucket(t.getBucketName())
					.delimiter("/")
					.build();
		}else{
			request = ListObjectsRequest.builder()
					.bucket(t.getBucketName())
					.prefix(t.getPath())
					.delimiter("/")
					.build();
		}
		//查询目录下的对象信息
		ListObjectsResponse response = s3FactoryService.callS3Method(request,s3, S3Method.LIST_OBJECTS);
		//目录列表
		List<CommonPrefix> prefixes = response.commonPrefixes();
		List<ObjectInfoBO> folderList = Lists.newArrayList();
		prefixes.forEach(obj -> {
			ObjectInfoBO object = new ObjectInfoBO();
			object.setKey(obj.prefix());
			folderList.add(object);
		});
		//文件列表
		List<S3Object> objectList = response.contents();
		List<ObjectInfoBO> fileList = Lists.newArrayList();
		objectList.forEach(obj -> {
			ObjectInfoBO object = new ObjectInfoBO();
			object.setKey(obj.key());
			object.setFileSize(obj.size());
			object.setUploadTime(obj.lastModified());
			fileList.add(object);
		});

		List<ObjectInfoBO> files = fileList.stream().filter(obj ->
				!obj.getKey().equals(StringUtils.join(t.getPath() ,t.getFileName()))
		).collect(Collectors.toList());

		t.setFolderList(folderList);
		t.setFileList(files);

		//查询目录的标签信息
		List<TagBO> tagList = fileService.getTags(t);
		t.setTagList(tagList);
		return t;
	}


	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		log.info("s3folder.getVersions.param:{}", JSON.toJSONString(t));
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//查询文件夹的版本信息，需要在key后面加/
		ListObjectVersionsRequest request = ListObjectVersionsRequest.builder().
				bucket(t.getBucketName()).
				prefix(StringUtils.join(t.getPath() ,t.getFileName())).
				build();

		ListObjectVersionsResponse response = s3FactoryService.callS3Method(request,s3, S3Method.LIST_OBJECT_VERSIONS);
		log.info("s3folder.getVersions.response:{}", response);

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
		log.info("s3folder.rename.param:{}", JSON.toJSONString(t));

		//重新上传目录，同时修改标签
		this.create(t);

	}


	public void setPathAndFileName(S3ObjectBO s3ObjectBO,String s3Key){
		if(StringUtils.isBlank(s3Key)){
			return;
		}
		if(s3ObjectBO == null){
			s3ObjectBO = new S3ObjectBO();
		}
		if(s3Key.startsWith("/")){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith("/")){
			s3ObjectBO.setPath(s3Key);
			s3ObjectBO.setFileName("");
		}else{
			s3ObjectBO.setPath(s3Key.substring(0,s3Key.lastIndexOf("/")+1));
			s3ObjectBO.setFileName(s3Key.substring(s3Key.lastIndexOf("/")+1));
		}
	}

}
