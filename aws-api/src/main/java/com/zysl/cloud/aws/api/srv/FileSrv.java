package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.api.req.KeyPageRequest;
import com.zysl.cloud.aws.api.req.KeyRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文件服务接口
 * @description
 * @author miaomingming
 * @date 21:48 2020/3/22
 * @param
 * @return
 **/

@RequestMapping("/aws/file")
public interface FileSrv {

	@GetMapping("/test")
	BaseResponse<String> test(KeyRequest request);


	@GetMapping("/test2")
	BasePaginationResponse<String> test2(KeyPageRequest request);

	/**
	 * 文件复制
	 * @param request
	 * @return
	 */
	@PostMapping("/copy")
	BaseResponse<String> copyFile(@RequestBody CopyObjectsRequest request);

	/**
	 * 文件分享
	 * @param request
	 * @return
	 */
	@PostMapping("/shareFile")
	BaseResponse<UploadFieDTO> shareFile(@RequestBody ShareFileRequest request);


}
