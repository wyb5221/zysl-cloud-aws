package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.req.KeyPageRequest;
import com.zysl.cloud.aws.api.req.KeyRequest;
import com.zysl.cloud.aws.api.srv.S3FileServ;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.utils.common.BaseController;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.zysl.cloud.aws.web.validator.KeyRequestV;


@Slf4j
@CrossOrigin
@RestController
public class FileController extends BaseController implements S3FileServ {

	@Autowired
	IFileService fileService;

	@Override
	public BaseResponse<String> test(KeyRequest request){
		return ServiceProvider.call(request,KeyRequestV.class,String.class,req->{
			return fileService.test(req.getName());
		});
	}

	@Override
	public BasePaginationResponse<String> test2(KeyPageRequest request){
		return ServiceProvider.callList(request,KeyRequestV.class,String.class,(req,myPage)->{
//			myPage.setTotalRecords(xx);//其他方法查询列查询；或者mybatis分页插件
			return fileService.getBuckets(req.getName());
		});
	}
}
