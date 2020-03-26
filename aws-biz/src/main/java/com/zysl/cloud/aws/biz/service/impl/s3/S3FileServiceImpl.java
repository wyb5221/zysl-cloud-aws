package com.zysl.cloud.aws.biz.service.impl.s3;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IS3FactoryService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("s3FileService")
public class S3FileServiceImpl implements IFileService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		log.info("s3file.create.param:{}", JSON.toJSONString(t));
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		PutObjectRequest request = PutObjectRequest.builder()
									   .bucket(t.getBucketName())
									   .key(t.getPath() + t.getFileName())
									   .contentEncoding(t.getContentEncoding())
									   .expires(t.getExpires() == null ? null : t.getExpires().toInstant())
									   .build();

		PutObjectResponse response = s3FactoryService.callS3MethodWithBody(request,RequestBody.fromBytes(t.getBodys()),s3Client, S3Method.PUT_OBJECT);

		t.setVersionId(response.versionId());
		return t;
	}
	@Override
	public void delete(S3ObjectBO t){

	}

	@Override
	public void modify(S3ObjectBO t){

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public void copy(S3ObjectBO src,S3ObjectBO dest){
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByServerNo(src.getBucketName());

		//获取标签内容
		List<Tag> tagSet = new ArrayList<>();
		if(!CollectionUtils.isEmpty(dest.getTagList())){
			dest.getTagList().forEach(obj -> {
				tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
			});
		}
		Tagging tagging = Tagging.builder().tagSet(tagSet).build();

		//查询复制接口入参
		CopyObjectRequest request = CopyObjectRequest.builder()
				.copySource("")
				.bucket(dest.getBucketName()).key(dest.getFileName())
				.tagging(tagging)
				.build();

		ListBucketsResponse response = s3FactoryService.callS3Method(request,s3,S3Method.COPY_OBJECT);
	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){

	}
	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		log.info("s3file.getBaseInfo.param:{}", JSON.toJSONString(t));
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		GetObjectRequest request = GetObjectRequest.builder()
									   .bucket(t.getBucketName())
									   .key(t.getPath() + t.getFileName())
									   .versionId(t.getVersionId() == null ? null : t.getVersionId())
									   .build();

		GetObjectResponse response = s3FactoryService.callS3Method(request,s3Client, S3Method.PUT_OBJECT);

		t.setVersionId(response.versionId());
		t.setContentLength(response.contentLength());
		t.setExpires(DateUtils.from(response.expires()));
		t.setLastModified(DateUtils.from(response.lastModified()));
		return t;
	}
	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		return null;
	}

	@Override
	public S3ObjectBO getInfoAndBody(S3ObjectBO t){
		return null;
	}

	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		return null;
	}
}
