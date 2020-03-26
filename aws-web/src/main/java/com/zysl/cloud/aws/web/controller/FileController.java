package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.KeyPageRequest;
import com.zysl.cloud.aws.api.req.KeyRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.api.srv.FileSrv;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.utils.common.BaseController;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.zysl.cloud.aws.web.validator.KeyRequestV;


@Slf4j
@CrossOrigin
@RestController
public class FileController extends BaseController implements FileSrv {

	@Autowired
	IS3BucketService bucketService;

	@Override
	public BaseResponse<String> test(KeyRequest request){
		return ServiceProvider.call(request,KeyRequestV.class,String.class,req->{
			return "test11";
		});
	}

	@Override
	public BasePaginationResponse<String> test2(KeyPageRequest request){
		return ServiceProvider.callList(request,KeyRequestV.class,String.class,(req,myPage)->{
//			myPage.setTotalRecords(xx);//其他方法查询列查询；或者mybatis分页插件
			return bucketService.getS3Buckets(request.getName());
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> shareFile(ShareFileRequest request) {
		return null;
	}
}
