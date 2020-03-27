package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IFolderService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("s3FolderService")
public class S3FolderServiceImpl implements IS3FolderService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IFileService fileService;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		log.info("s3file.create.param:{}", JSON.toJSONString(t));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		PutObjectRequest request = PutObjectRequest.builder().bucket(t.getBucketName()).
				key(t.getPath() + "/").build();

		PutObjectResponse response = s3FactoryService.callS3Method(request, s3, S3Method.PUT_OBJECT);
		log.info("s3file.create.response:{}", response);
		t.setVersionId(response.versionId());

		return t;
	}

	@Override
	public void delete(S3ObjectBO t){
		log.info("s3file.delete.param:{}", JSON.toJSONString(t));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//获取删除对象
		List<ObjectIdentifier> objects = new ArrayList<>();
		ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
				.key(t.getPath()+"/").build();
		objects.add(objectIdentifier);
		Delete delete = Delete.builder().objects(objects).build();
		DeleteObjectsRequest request = DeleteObjectsRequest.builder()
				.bucket(t.getBucketName())
				.delete(delete)
				.build();

		DeleteObjectsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.DELETE_OBJECTS);
		log.info("s3file.delete.response:{}", response);

	}

	@Override
	public void modify(S3ObjectBO t){

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public void copy(S3ObjectBO src,S3ObjectBO dest){
		log.info("s3file.move.param.src:{}，dest:{}", JSON.toJSONString(src), JSON.toJSONString(dest));
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(src.getBucketName());

		//查询复制接口入参
		CopyObjectRequest request = CopyObjectRequest.builder()
				.copySource(src.getBucketName() + "/" + src.getPath() + src.getFileName())
				.bucket(dest.getBucketName()).key(dest.getPath() + dest.getFileName())
				.build();

		CopyObjectResponse response = s3FactoryService.callS3Method(request,s3,S3Method.COPY_OBJECT);
		log.info("s3file.copy.response:{}", response);
	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){


	}
	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		return null;
	}

	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		log.info("s3file.getDetailInfo.param:{}", JSON.toJSONString(t));
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
					.prefix(t.getPath()+"/")
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
		t.setFolderList(folderList);
		t.setFileList(fileList);

		//查询目录的标签信息
		S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getDetailInfo(t);
		t.setTagList(s3ObjectBO.getTagList());
		return t;
	}


	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		log.info("s3file.getVersions.param:{}", JSON.toJSONString(t));
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//查询文件夹的版本信息，需要在key后面加/
		ListObjectVersionsRequest request = ListObjectVersionsRequest.builder().
				bucket(t.getBucketName()).
				prefix(t.getPath() + t.getFileName() + "/").
				build();

		ListObjectVersionsResponse response = s3FactoryService.callS3Method(request,s3, S3Method.LIST_OBJECT_VERSIONS);
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
}
