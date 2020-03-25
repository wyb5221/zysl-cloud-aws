package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.req.KeyPageRequest;
import com.zysl.cloud.aws.api.req.KeyRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件服务接口
 * @description
 * @author miaomingming
 * @date 21:48 2020/3/22
 * @param
 * @return
 **/

@RequestMapping("/test")
public interface S3FileServ {

	@GetMapping("/test")
	BaseResponse<String> test(KeyRequest request);


	@GetMapping("/test2")
	BasePaginationResponse<String> test2(KeyPageRequest request);

}
