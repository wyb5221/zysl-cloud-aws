package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.zysl.aws.web.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.dto.*;
import com.zysl.cloud.aws.api.req.*;
import com.zysl.cloud.aws.api.srv.FileSrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.aws.web.validator.*;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


@Slf4j
@CrossOrigin
@RestController
public class FileController extends BaseController implements FileSrv {

	@Autowired
	IS3BucketService bucketService;
	@Autowired
	IS3FileService fileService;

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
	public BaseResponse<UploadFieDTO> uploadFile(UploadFileRequest request) {
		return ServiceProvider.call(request, UploadFileRequestV.class, UploadFieDTO.class, req -> {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			//进行解密
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] bytes = null;
			try {
				bytes = decoder.decodeBuffer(request.getData());
			} catch (IOException e) {
				log.info("---uploadFile流转换异常：{}--", e);
			}
			t.setBodys(bytes);

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.create(t);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(s3ObjectBO.getPath() + s3ObjectBO.getFileName());
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());
			return uploadFieDTO;
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> uploadFile(HttpServletRequest request) {
		return ServiceProvider.call(request, null, UploadFieDTO.class,req -> {

			S3ObjectBO s3Object = new S3ObjectBO();
			s3Object.setBucketName(request.getParameter("bucketName"));
			setPathAndFileName(s3Object,request.getParameter("fileId"));

			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
			byte[] bytes = null;
			try {
				bytes = multipartHttpServletRequest.getFile("file").getBytes();
			} catch (IOException e) {
				log.error("--uploadFile获取文件流异常--：{}", e);
				throw new AppLogicException("获取文件流异常");
			}
			s3Object.setBodys(bytes);

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.create(s3Object);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(s3ObjectBO.getPath() + s3ObjectBO.getFileName());
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());
			return uploadFieDTO;
		});
	}

	@Override
	public BaseResponse<DownloadFileDTO> downloadFile(HttpServletRequest request, HttpServletResponse response, DownloadFileRequest downRequest) {

		ServiceProvider.call(downRequest, DownloadFileRequestV.class, String.class, req ->{
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setVersionId(req.getVersionId());

			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
			List<TagsBO> tagList = s3ObjectBO.getTagList();
			boolean tagFlag = false;
			if(!StringUtils.isEmpty(req.getUserId())){
				//需要校验权限
				for (TagsBO tag : tagList) {
					//判断标签可以是否是owner
					if(BizConstants.TAG_OWNER.equals(tag.getKey()) &&
							req.getUserId().equals(tag.getValue())){
						//在判断标签value
						tagFlag = true;
					}
				}
			}
			if(!StringUtils.isEmpty(req.getUserId()) && !tagFlag){
				//userid不为空是，需要校验权限
				return null;
			}

			byte[] bytes = s3ObjectBO.getBodys();
			log.info("--下载接口返回的文件数据大小--", bytes.length);
			if(DownTypeEnum.COVER.getCode().equals(req.getType())){
				DownloadFileDTO downloadFileDTO = new DownloadFileDTO();

				BASE64Encoder encoder = new BASE64Encoder();
				//返回加密
				downloadFileDTO.setData(encoder.encode(bytes));
				return downloadFileDTO;
			}else {
				try {
					//1下载文件流
					OutputStream outputStream = response.getOutputStream();
					response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
					response.setCharacterEncoding("UTF-8");

					String userAgent = request.getHeader("User-Agent").toUpperCase();//获取浏览器名（IE/Chome/firefox）
					String fileId = t.getFileName();
					if (userAgent.contains("MSIE") ||
							(userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)) {
						fileId = URLEncoder.encode(t.getFileName(), "UTF-8");// IE浏览器
					}else{
						fileId = new String(t.getFileName().getBytes("UTF-8"), "ISO8859-1");// 谷歌
					}
					response.setHeader("Content-Disposition", "attachment;fileName="+fileId);

					outputStream.write(bytes);
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					log.info("--文件下载异常：--", e);
					throw new AppLogicException("文件流处理异常");
				}
				return null;
			}
		});
		return null;
	}

	@Override
	public void shareDownloadFile(HttpServletResponse response, DownloadFileRequest request) {
		ServiceProvider.call(request, DownloadFileRequestV.class, null, req ->{
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setVersionId(req.getVersionId());

			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
			List<TagsBO> tagList = s3ObjectBO.getTagList();
			List<TagsBO> newTagList = Lists.newArrayList();
			for (TagsBO tag : tagList) {

				//判断下载次数
				if(BizConstants.TAG_DOWNLOAD_AMOUT.equals(tag.getKey()) &&
						Integer.parseInt(tag.getValue()) < 1){
					//下载次数已下完
					log.info("--shareDownloadFile文件载次数已下完：--");
					return null;
				}
				//判断是否在有效期内
				if(BizConstants.TAG_VALIDITY.equals(tag.getKey()) &&
						DateUtils.doCompareDate(new Date(), DateUtils.getStringToDate(tag.getValue())) > 0){
					//已过有效期
					log.info("--shareDownloadFile文件已过有效期：--");
					return null;
				}
				if(BizConstants.TAG_DOWNLOAD_AMOUT.equals(tag.getKey())){
					int amout = Integer.parseInt(tag.getValue()) - 1;
					tag.setValue(String.valueOf(amout));
					newTagList.add(tag);
				}else{
					newTagList.add(tag);
				}
			}

			//权限校验完成，可以下载
			byte[] bytes = s3ObjectBO.getBodys();
			try {
				//1下载文件流
				OutputStream outputStream = response.getOutputStream();
				response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
				response.setHeader("Content-Disposition", "attachment;fileName="+request.getFileId());
				response.setCharacterEncoding("UTF-8");

				outputStream.write(bytes);
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				log.info("--文件下载异常：--", e);
				throw new AppLogicException("文件流处理异常");
			}
			//在重新设置文件标签
			t.setTagList(newTagList);
			fileService.modify(t);
			return null;
		});
	}

	@Override
	public BaseResponse<String> deleteFile(DelObjectRequest request) {
		return ServiceProvider.call(request, DelObjectRequestV.class, String.class, req -> {
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            t.setVersionId(req.getVersionId());
            setPathAndFileName(t,req.getKey());
            fileService.delete(t);
            return RespCodeEnum.SUCCESS.getDesc();
        });
	}

    @Override
    public BaseResponse<FileInfoDTO> getFileInfo(GetFileRequest request) {
        return ServiceProvider.call(request, GetFileRequestV.class, FileInfoDTO.class, req ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            t.setVersionId(req.getVersionId());
            setPathAndFileName(t,req.getFileName());

            S3ObjectBO object = (S3ObjectBO)fileService.getDetailInfo(t);
            FileInfoDTO fileInfoDTO = new FileInfoDTO();
            fileInfoDTO.setContentLength(object.getContentLength());
            fileInfoDTO.setLastModified(object.getLastModified());
            fileInfoDTO.setVersionId(object.getVersionId());
            fileInfoDTO.setTageList(BeanCopyUtil.copyList(object.getTagList(), TageDTO.class));
            return fileInfoDTO;
        });
    }

    @Override
    public void getVideo(HttpServletResponse response, GetVideoRequest request) {
        ServiceProvider.call(request, GetVideoRequestV.class, String.class, req -> {
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getFileId());
            t.setVersionId(req.getVersionId());

            S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
            byte[] bytes = s3ObjectBO.getBodys();
            response.reset();
            //设置头部类型
            response.setContentType("video/mp4;charset=UTF-8");
            ServletOutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(bytes);
                out.flush();
            } catch (IOException e) {
                log.error("--文件流转换异常：--", e);
            }finally {
                try {
                    out.close();
                } catch (IOException e) {

                }
                out = null;
            }
            return null;
        });
    }

    @Override
	public BasePaginationResponse<ObjectVersionDTO> getFileVersion(GetFileRequest request) {
		return ServiceProvider.callList(request, GetFileRequestV.class, ObjectVersionDTO.class, (req, page) ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t,req.getFileName());

            List<S3ObjectBO> objectList = fileService.getVersions(t);
            List<ObjectVersionDTO> versionList = Lists.newArrayList();
            objectList.forEach(obj ->{
                ObjectVersionDTO version = new ObjectVersionDTO();
                version.setKey(obj.getFileName());
                version.setVersionId(obj.getVersionId());
//				version.setLastModified(obj.getLastModified());
                version.setSize(obj.getContentLength());
                versionList.add(version);
            });
            return versionList;
        });
	}

	@Override
	public BaseResponse<Long> getFileSize(GetFileRequest request) {
		return ServiceProvider.call(request, GetFileRequestV.class, Long.class, req ->{
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			t.setVersionId(req.getVersionId());
			setPathAndFileName(t,req.getFileName());
            S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getBaseInfo(t);
			return s3ObjectBO.getContentLength();
		});
	}

    @Override
    public BaseResponse<String> setObjectTag(SetFileTagRequest request) {

        return ServiceProvider.call(request, SetFileTagRequestV.class, String.class, req ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucket());
            List<TageDTO> tageList = req.getTageList();
            List<TagsBO> tags = BeanCopyUtil.copyList(tageList, TagsBO.class);
            t.setTagList(tags);
            //同时修改多个文件的标签
            List<KeyVersionDTO> keyList = req.getKeyList();
            for (KeyVersionDTO obj : keyList) {
                t.setVersionId(obj.getVersionId());
                setPathAndFileName(t,obj.getKey());

                fileService.modify(t);
            }
            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

    @Override
	public BaseResponse<String> copyFile(CopyObjectsRequest request) {
		return ServiceProvider.call(request, CopyObjectsRequestV.class, String.class, req -> {
			//复制源文件信息
			S3ObjectBO src = new S3ObjectBO();
			src.setBucketName(req.getSourceBucket());
			setPathAndFileName(src,req.getSourceKey());
			//复制后的目标文件信息
			S3ObjectBO dest = new S3ObjectBO();
			dest.setBucketName(req.getDestBucket());
			setPathAndFileName(dest,req.getDestKey());

			fileService.copy(src, dest);
			return RespCodeEnum.SUCCESS.getDesc();
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> shareFile(ShareFileRequest request) {
		return ServiceProvider.call(request, ShareFileRequestV.class, UploadFieDTO.class, req -> {

			//复制源文件信息
			S3ObjectBO src = new S3ObjectBO();
			src.setBucketName(req.getBucketName());
			setPathAndFileName(src,req.getFileName());
			//复制后的目标文件信息
			S3ObjectBO dest = new S3ObjectBO();
			dest.setBucketName(req.getBucketName());
			setPathAndFileName(dest,BizConstants.SHARE_DEFAULT_FOLDER  + "/" + req.getFileName());
			//获取标签信息
			List<TagsBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(req.getMaxDownloadAmout()+"")){
				TagsBO tag = new TagsBO();
				tag.setKey(BizConstants.TAG_DOWNLOAD_AMOUT);
				tag.setValue(String.valueOf(req.getMaxDownloadAmout()));
				tagList.add(tag);
			}
			if(!StringUtils.isEmpty(req.getMaxHours()+"")){
				TagsBO tag = new TagsBO();
				tag.setKey(BizConstants.TAG_VALIDITY);
				tag.setValue(String.valueOf(req.getMaxHours()));
				tagList.add(tag);
			}
			dest.setTagList(tagList);

			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.copy(src, dest);
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(s3ObjectBO.getPath() + s3ObjectBO.getFileName());
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());

			return uploadFieDTO;
		});
	}
}
