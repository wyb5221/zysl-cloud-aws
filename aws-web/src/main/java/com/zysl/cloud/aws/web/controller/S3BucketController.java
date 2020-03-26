package com.zysl.cloud.aws.web.controller;


import com.zysl.cloud.aws.api.req.CreateBucketRequest;
import com.zysl.cloud.aws.api.req.GetBucketsRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.api.srv.S3BucketSrv;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.aws.web.validator.CreateBucketRequestV;
import com.zysl.cloud.aws.web.validator.GetBucketsRequestV;
import com.zysl.cloud.aws.web.validator.SetFileVersionRequestV;
import com.zysl.cloud.utils.common.BaseController;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class S3BucketController extends BaseController implements S3BucketSrv {
	@Autowired
	private IS3BucketService s3BucketService;


	@Override
	public BaseResponse<String> createBucket(CreateBucketRequest request){
		return ServiceProvider.call(request, CreateBucketRequestV.class, String.class,req->{
			s3BucketService.createBucket(req.getBucketName(),req.getServerNo());
			return request.getBucketName();
		});
	}

//	@Override
//	BasePaginationResponse<FileInfoDTO> getFilesByBucket(@RequestBody BucketFileRequest request){
//		return ServiceProvider.callList(request, CreateBucketRequestV.class, FileInfoDTO.class,req->{
//			s3BucketService.createBucket(req.getBucketName(),req.getServerNo());
//			return request.getBucketName();
//		});
//
//	}

	@Override
	public BaseResponse<String> updateFileVersion(@RequestBody SetFileVersionRequest request){
		return ServiceProvider.call(request, SetFileVersionRequestV.class, String.class,req->{
			s3BucketService.setBucketVersion(request);
			return RespCodeEnum.SUCCESS.getDesc();
		});
	}

	@Override
	public BasePaginationResponse<String> getBuckets(GetBucketsRequest request){
		return ServiceProvider.callList(request, GetBucketsRequestV.class, String.class,(req,myPage)->{
			return s3BucketService.getS3Buckets(request.getServerNo());
		});
	}
}
