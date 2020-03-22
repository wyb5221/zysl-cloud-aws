package com.zysl.aws.web.controller;

import com.zysl.aws.api.req.KeyRequest;
import com.zysl.aws.api.srv.FileServ;
import com.zysl.aws.biz.service.IFileService;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.zysl.aws.web.validator.KeyRequestV;

@CrossOrigin
@RestController
@Slf4j
public class FileController implements FileServ {

	@Autowired
	IFileService fileService;

	@Override
	public BaseResponse<String> test(KeyRequest request){
		return ServiceProvider.call(request,KeyRequestV.class,String.class,(req)->{
			return fileService.test(request.getName());
		});
	}
}
