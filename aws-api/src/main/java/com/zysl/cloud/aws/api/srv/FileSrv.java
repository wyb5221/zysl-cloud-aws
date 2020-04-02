package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.DownloadFileDTO;
import com.zysl.cloud.aws.api.dto.FileInfoDTO;
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

	/**
	 * base64进制String上传文件
	 * @param request
	 * @returnuploadFile
	 */
	@PostMapping("/uploadFile")
	BaseResponse<UploadFieDTO> uploadFile(@RequestBody UploadFileRequest request);

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
	 * 分享文件下载
	 * @param response
	 * @param downRequest
	 */
	@GetMapping("/shareDownloadFile")
	void shareDownloadFile(HttpServletResponse response, DownloadFileRequest request);

	/**
	 * 删除文件
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	BaseResponse<String> deleteFile(@RequestBody DelObjectRequest request);

	/**
	 * 获取文件信息
	 * @param bucketName
	 * @param fileName
	 */
	@GetMapping("/getFileInfo")
	BaseResponse<FileInfoDTO> getFileInfo(GetFileRequest request);

	/**
	 * 获取视频文件信息
	 * @param response
	 * @param bucketName
	 * @param fileId
	 */
	@GetMapping("/getVideo")
	void getVideo(HttpServletResponse response, GetVideoRequest request);

	/**
	 * 获取文件版本信息
	 * @param bucketName
	 * @param fileName
	 * @return
	 */
	@GetMapping("/getFileVersion")
	BasePaginationResponse<ObjectVersionDTO> getFileVersion(GetFileVerRequest request);

	/**
	 * 获取文件大小
	 * @param bucketName
	 * @param fileName
	 */
	@GetMapping("/getFileSize")
	BaseResponse<Long> getFileSize(GetFileRequest request);

    /**
     * 设置文件标签信息
     * @return
     */
    @PostMapping("/setTag")
    BaseResponse<String> setObjectTag(@RequestBody SetFileTagRequest request);

	/**
	 * 文件复制
	 * @param request
	 * @return
	 */
	@PostMapping("/copy")
	BaseResponse<String> copyFile(@RequestBody CopyObjectsRequest request);

	/**
	 * 文件移动
	 * @param request
	 * @return
	 */
	@PostMapping("/move")
	BaseResponse<String> moveFile(@RequestBody CopyObjectsRequest request);

	/**
	 * 文件分享
	 * @param request
	 * @return
	 */
	@PostMapping("/shareFile")
	BaseResponse<UploadFieDTO> shareFile(@RequestBody ShareFileRequest request);


	/**
	 * 设置数据操作权限
	 * @description
	 * @author miaomingming
	 * @date 16:52 2020/3/30
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/setDataAuth")
	BaseResponse<String> updateDataAuth(@RequestBody DataAuthRequest request);

	/**
	 * 文件重命名
	 * @param request
	 * @return
	 */
	@PostMapping("/rename")
	BaseResponse<UploadFieDTO> fileRename(@RequestBody ObjectRenameRequest request);

	/**
	 * 文件是否存在
	 * @description
	 * @author miaomingming
	 * @date 15:24 2020/4/1
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.Boolean>
	 **/
	@PostMapping("/exist")
	BaseResponse<Boolean> isExistFile(@RequestBody FileExistRequest request);

	
	/**
	 * 分片下载，通过请求头Range区分
	 * 最大范围后后端配置
	 * Range格式：
	 * 没有此字段或没有值：返回最大范围
	 * a-  ：  从 a开始的最大范围
	 * a   :
	 * @description
	 * @author miaomingming
	 * @date 9:24 2020/4/2
	 * @param request
	 * @param response
	 * @param downRequest
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.DownloadFileDTO>
	 **/
	@GetMapping("/multiDownload")
	BaseResponse<String> multiDownloadFile(HttpServletRequest request, HttpServletResponse response, MultiDownloadFileRequest downRequest);


	/**
	 * 创建断点续传
	 * @param request
	 * @return
	 */
	@PostMapping("/createMultipart")
	BaseResponse<String> createMultipart(@RequestBody CreateMultipartRequest request);

	/**
	 *断点续传
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadPart")
	BaseResponse<MultipartUploadRequest> uploadPart(HttpServletRequest request);

	/**
	 * 断点续传完成确认
	 * @param request
	 * @return
	 */
	@PostMapping("/complete")
	BaseResponse<UploadFieDTO> completeMultipart(@RequestBody CompleteMultipartRequest request);


}
