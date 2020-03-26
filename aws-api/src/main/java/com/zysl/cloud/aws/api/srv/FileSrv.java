package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.DownloadFileDTO;
import com.zysl.cloud.aws.api.dto.ObjectVersionDTO;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.*;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * 文件流上传
	 * @param request
	 * @returnuploadFile
	 */
	@PostMapping("/uploadFileInfo")
	BaseResponse<UploadFieDTO> uploadFile(HttpServletRequest request);

	/**
	 * 下载文件
	 * @param bucketName
	 * @param fileId
	 * @return
	 */
	@GetMapping("/downloadFile")
	BaseResponse<DownloadFileDTO> downloadFile(HttpServletRequest request, HttpServletResponse response, DownloadFileRequest downRequest);

	/**
	 * 删除文件
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	BaseResponse<String> deleteFile(@RequestBody DelObjectRequest request);

	/**
	 * 获取文件版本信息
	 * @param bucketName
	 * @param fileName
	 * @return
	 */
	@GetMapping("/getFileVersion")
	BasePaginationResponse<ObjectVersionDTO> getFileVersion(GetFileRequest request);

	/**
	 * 获取文件大小
	 * @param bucketName
	 * @param fileName
	 */
	@GetMapping("/getFileSize")
	BaseResponse<Long> getFileSize(GetFileRequest request);

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
