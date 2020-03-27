package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.WordToPDFDTO;
import com.zysl.cloud.aws.api.req.WordToPDFRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/file")
public interface WordSrv {

	/**
	 * word转pdf，可设置水印、密码
	 * @description 
	 * @author miaomingming
	 * @date 9:41 2020/3/26 
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<WordToPDFDTO>
	 **/
	@PostMapping("/word2pdf")
	BaseResponse<WordToPDFDTO> changeWordToPdf(@RequestBody WordToPDFRequest request);
}
