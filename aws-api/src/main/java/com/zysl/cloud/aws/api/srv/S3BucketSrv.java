package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.req.CreateBucketRequest;
import com.zysl.cloud.aws.api.req.GetBucketsRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/aws/bucket")
public interface S3BucketSrv {

	@GetMapping("/createBucket")
	BaseResponse<String> createBucket(CreateBucketRequest request);


	/**
	 * 设置文件夹的版本控制权限
	 * @param bucketName
	 * @param status
	 * @return
	 */
	@PostMapping("/setVersion")
	BaseResponse<String> updateFileVersion(@RequestBody SetFileVersionRequest request);

	/**
	 * 查询所有bucket列表
	 * @param bucketName
	 * @param serviceNo
	 * @return
	 */
	@GetMapping("/getBuckets")
	BasePaginationResponse<String> getBuckets(GetBucketsRequest request);
}
