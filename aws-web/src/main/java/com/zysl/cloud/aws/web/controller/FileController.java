package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.*;
import com.zysl.cloud.aws.api.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.aws.api.req.*;
import com.zysl.cloud.aws.api.srv.FileSrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.utils.DataAuthUtils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.domain.bo.MultipartUploadBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.aws.web.validator.*;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.SpringContextUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import com.zysl.cloud.utils.validator.BeanValidator;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.Tag;
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
import java.util.UUID;


@Slf4j
@CrossOrigin
@RestController
public class FileController extends BaseController implements FileSrv {

	@Autowired
	IS3BucketService bucketService;
	@Autowired
	IS3FileService fileService;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private DataAuthUtils dataAuthUtils;


	@GetMapping("/curVer")
	public String getCurVersion(){
		return bizConfig.getCurVer();
	}

	@Override
	public BaseResponse<UploadFieDTO> uploadFile(UploadFileRequest request) {
		return ServiceProvider.call(request, UploadFileRequestV.class, UploadFieDTO.class, req -> {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			String fileId = req.getFileId();
			if(StringUtils.isEmpty(fileId)){
				fileId = UUID.randomUUID().toString().replaceAll("-","");
			}
			setPathAndFileName(t, fileId);


			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.WRITE.getCode());

			//进行解密
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] bytes = null;
			try {
				bytes = decoder.decodeBuffer(request.getData());
			} catch (IOException e) {
				log.info("---uploadFile流转换异常：{}--", e);
			}
			t.setBodys(bytes);
			//设置标签信息
			List<TagBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(req.getFileName())){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.FILE_NAME.getCode());
				tag.setValue(req.getFileName());
				tagList.add(tag);
			}
			t.setTagList(fileService.addTags(t, tagList));

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.create(t);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(StringUtils.join(s3ObjectBO.getPath(), s3ObjectBO.getFileName()));
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


			//数据权限校验
			fileService.checkDataOpAuth(s3Object, OPAuthTypeEnum.READ.getCode());

			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
			byte[] bytes = null;
			try {
					bytes = multipartHttpServletRequest.getFile("file").getBytes();
			} catch (IOException e) {
				log.error("--uploadFile获取文件流异常--：{}", e);
				throw new AppLogicException("获取文件流异常");
			}
			s3Object.setBodys(bytes);
			//设置标签信息
			List<TagBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(request.getParameter("fileName"))){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.FILE_NAME.getCode());
				tag.setValue(request.getParameter("fileName"));
				tagList.add(tag);
			}

			s3Object.setTagList(fileService.addTags(s3Object, tagList));

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.create(s3Object);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(StringUtils.join(s3ObjectBO.getPath(), s3ObjectBO.getFileName()));
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());
			return uploadFieDTO;
		});
	}

	@Override
	public BaseResponse<DownloadFileDTO> downloadFile(HttpServletRequest request, HttpServletResponse response, DownloadFileRequest downRequest) {
		return ServiceProvider.call(downRequest, DownloadFileRequestV.class, DownloadFileDTO.class, req ->{
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setVersionId(req.getVersionId());


			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());
			
			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
			checkOwner(req,s3ObjectBO);

			byte[] bytes = s3ObjectBO.getBodys();
			log.info("--下载接口返回的文件数据大小--", bytes.length);
			if(DownTypeEnum.COVER.getCode().equals(req.getType())){
				DownloadFileDTO downloadFileDTO = new DownloadFileDTO();

				BASE64Encoder encoder = new BASE64Encoder();
				//返回加密
				downloadFileDTO.setData(encoder.encode(bytes));
				return downloadFileDTO;
			}else {
				//获取标签中的文件名称
				String tagValue = fileService.getTagValue(s3ObjectBO.getTagList(), S3TagKeyEnum.FILE_NAME.getCode());
				String fileId = StringUtils.isEmpty(tagValue) ? t.getFileName() : tagValue;
				downloadFileByte(request,response,fileId,s3ObjectBO.getBodys());
				return null;
			}
		});
	}
	
	//临时数据校验，是否对象拥有者
	private void checkOwner(DownloadFileRequest req,S3ObjectBO s3ObjectBO){
		if(!StringUtils.isEmpty(req.getUserId())){
			//需要校验权限
			for (TagBO tag : s3ObjectBO.getTagList()) {
				//判断标签可以是否是owner
				if(S3TagKeyEnum.FILE_NAME.getCode().equals(tag.getKey()) &&
					req.getUserId().equals(tag.getValue())){
					return;
				}
			}
			log.warn("check.owner.error,file:{},userId:{}",s3ObjectBO.getBucketName()+s3ObjectBO.getFileName(),req.getUserId());
			throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
		}
	}
	
	private void downloadFileByte(HttpServletRequest request,HttpServletResponse response,String fileName,byte[] bytes){
		try {
			//1下载文件流
			OutputStream outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
			response.setCharacterEncoding("UTF-8");
			
			String userAgent = request.getHeader("User-Agent").toUpperCase();//获取浏览器名（IE/Chome/firefox）
			
			
			if (userAgent.contains("MSIE") ||
				(userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)) {
				fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
			}else{
				fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
			}
			response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
			
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			log.error("--文件下载异常：--", e);
			throw new AppLogicException(ErrCodeEnum.DOWNLOAD_FILE_ERROR.getCode());
		}
	}

	@Override
	public void shareDownloadFile(HttpServletResponse response, DownloadFileRequest request) {
		ServiceProvider.call(request, DownloadFileRequestV.class, null, req ->{
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setVersionId(req.getVersionId());

			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
			List<TagBO> tagList = s3ObjectBO.getTagList();
			List<TagBO> newTagList = Lists.newArrayList();
			for (TagBO tag : tagList) {

				//判断下载次数
				if(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode().equals(tag.getKey()) &&
						Integer.parseInt(tag.getValue()) < 1){
					//下载次数已下完
					log.info("--shareDownloadFile文件载次数已下完：--");
					return null;
				}
				//判断是否在有效期内
				if(S3TagKeyEnum.TAG_VALIDITY.getCode().equals(tag.getKey()) &&
						DateUtils.doCompareDate(new Date(), DateUtils.getStringToDate(tag.getValue())) > 0){
					//已过有效期
					log.info("--shareDownloadFile文件已过有效期：--");
					return null;
				}
				if(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode().equals(tag.getKey())){
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

			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.DELETE.getCode());

            fileService.delete(t);
            return RespCodeEnum.SUCCESS.getName();
        });
	}

    @Override
    public BaseResponse<FileInfoDTO> getFileInfo(GetFileRequest request) {
        return ServiceProvider.call(request, GetFileRequestV.class, FileInfoDTO.class, req ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            t.setVersionId(req.getVersionId());
            setPathAndFileName(t,req.getFileName());

            //数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());

            S3ObjectBO object = (S3ObjectBO)fileService.getDetailInfo(t);
            FileInfoDTO fileInfoDTO = new FileInfoDTO();
            fileInfoDTO.setContentLength(object.getContentLength());
            fileInfoDTO.setLastModified(object.getLastModified());
            fileInfoDTO.setVersionId(object.getVersionId());
            fileInfoDTO.setTageList(BeanCopyUtil.copyList(object.getTagList(), TagDTO.class));
			fileInfoDTO.setPath(object.getPath());
			//获取标签中的文件名称
			fileInfoDTO.setFileName(fileService.getTagValue(object.getTagList(), S3TagKeyEnum.FILE_NAME.getCode()));
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


			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());

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
	public BasePaginationResponse<ObjectVersionDTO> getFileVersion(GetFileVerRequest request) {
		return ServiceProvider.callList(request, GetFileRequestV.class, ObjectVersionDTO.class, (req, page) ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t,req.getFileName());


			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());

            List<S3ObjectBO> objectList = fileService.getVersions(t);
            List<ObjectVersionDTO> versionList = Lists.newArrayList();
            if(!CollectionUtils.isEmpty(objectList)){
				objectList.forEach(obj ->{
					ObjectVersionDTO version = new ObjectVersionDTO();
					version.setKey(obj.getFileName());
					version.setVersionId(obj.getVersionId());
//				version.setLastModified(obj.getLastModified());
					version.setSize(obj.getContentLength());
					versionList.add(version);
				});
			}

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

			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());

            S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getBaseInfo(t);

			Date date1 = s3ObjectBO.getLastModified();
			Date date2 = DateUtils.createDate(bizConfig.DOWNLOAD_TIME);
			if(DateUtils.doCompareDate(date1, date2) < 0){
				return s3ObjectBO.getContentLength() * 3/4;
			}else{
				return s3ObjectBO.getContentLength();
			}
		});
	}

    @Override
    public BaseResponse<String> setObjectTag(SetFileTagRequest request) {

        return ServiceProvider.call(request, SetFileTagRequestV.class, String.class, req ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucket());
            List<TagDTO> tageList = req.getTageList();
            List<TagBO> tags = BeanCopyUtil.copyList(tageList, TagBO.class);
            t.setTagList(tags);
            //同时修改多个文件的标签
            List<KeyVersionDTO> keyList = req.getKeyList();
            for (KeyVersionDTO obj : keyList) {
                t.setVersionId(obj.getVersionId());
                setPathAndFileName(t,obj.getKey());

                fileService.modify(t);
            }
            return RespCodeEnum.SUCCESS.getName();
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

			//设置目标文件标签
			List<TagBO> list = Lists.newArrayList();
			TagBO tagBO = new TagBO();
			tagBO.setKey(S3TagKeyEnum.FILE_NAME.getCode());
			tagBO.setValue(dest.getFileName());
			list.add(tagBO);
			dest.setTagList(fileService.addTags(src,list));

			//数据权限校验
			fileService.checkDataOpAuth(src, OPAuthTypeEnum.READ.getCode());
			//数据权限校验
			fileService.checkDataOpAuth(dest, OPAuthTypeEnum.WRITE.getCode());

			fileService.copy(src, dest);
			return RespCodeEnum.SUCCESS.getName();
		});
	}

	@Override
	public BaseResponse<String> moveFile(CopyObjectsRequest request) {
		return ServiceProvider.call(request, CopyObjectsRequestV.class, String.class, req -> {
			//复制源文件信息
			S3ObjectBO src = new S3ObjectBO();
			src.setBucketName(req.getSourceBucket());
			setPathAndFileName(src,req.getSourceKey());
			//复制后的目标文件信息
			S3ObjectBO dest = new S3ObjectBO();
			dest.setBucketName(req.getDestBucket());
			setPathAndFileName(dest,req.getDestKey());

			//设置目标文件标签
			dest.setTagList(fileService.addTags(src, Lists.newArrayList()));

			//数据权限校验
			fileService.checkDataOpAuth(src, OPAuthTypeEnum.READ.getCode());
			//数据权限校验
			fileService.checkDataOpAuth(dest, OPAuthTypeEnum.WRITE.getCode());

			fileService.move(src, dest);
			return RespCodeEnum.SUCCESS.getName();
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> shareFile(ShareFileRequest request) {
		return ServiceProvider.call(request, ShareFileRequestV.class, UploadFieDTO.class, req -> {

			//复制源文件信息
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t,req.getFileName());

			//获取文件内容
			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getInfoAndBody(t);

			S3ObjectBO shareObject = new S3ObjectBO();
			shareObject.setBucketName(bizConfig.shareFileBucket);
			setPathAndFileName(shareObject, req.getFileName());
			shareObject.setBodys(s3ObjectBO.getBodys());

			//获取标签信息
			List<TagBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(req.getMaxDownloadAmout()+"")){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode());
				tag.setValue(String.valueOf(req.getMaxDownloadAmout()));
				tagList.add(tag);
			}
			if(!StringUtils.isEmpty(req.getMaxHours()+"")){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.TAG_VALIDITY.getCode());
				String date = DateUtils.getDateToString(DateUtils.addDateHour(new Date(), req.getMaxHours()));
				tag.setValue(date);
				tagList.add(tag);
			}
			shareObject.setTagList(tagList);
			//重新上传文件
			S3ObjectBO shareBO = (S3ObjectBO) fileService.create(shareObject);
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(shareBO.getBucketName());
			uploadFieDTO.setFileName(shareBO.getPath() + shareBO.getFileName());
			uploadFieDTO.setVersionId(shareBO.getVersionId());

			return uploadFieDTO;
		});
	}


	@Override
	public BaseResponse<String> updateDataAuth(@RequestBody DataAuthRequest request){
		return ServiceProvider.call(request, DataAuthRequestV.class, String.class, req -> {
			S3ObjectBO src = new S3ObjectBO();
			src.setBucketName(req.getBucketName());
			setPathAndFileName(src,req.getFileName());
			src.setVersionId(req.getVersionId());

			//数据权限校验
			fileService.checkDataOpAuth(src, OPAuthTypeEnum.MODIFY.getCode());

			List<TagBO> list = new ArrayList<>();
			TagBO tagBO = new TagBO();
			tagBO.setKey(S3TagKeyEnum.USER_AUTH.getCode());
			tagBO.setValue(dataAuthUtils.contactAuths(req.getUserAuths(),req.getGroupAuths(),req.getEveryOneAuths()));
			list.add(tagBO);


			if(StringUtils.isNotBlank(req.getFileName())){
				src.setTagList(fileService.addTags(src,list));
				fileService.modify(src);
			}else{
				src.setTagList(list);
				bucketService.putBucketTag(src);
			}
			return RespCodeEnum.SUCCESS.getName();
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> fileRename(ObjectRenameRequest request) {
		return ServiceProvider.call(request, ObjectRenameRequestV.class, UploadFieDTO.class, req -> {

			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getSourcekey());
			t.setTagFilename(req.getDestKey());

			//设置标签
			List<TagBO> tagList = Lists.newArrayList();
			TagBO tag = new TagBO();
			tag.setKey(S3TagKeyEnum.FILE_NAME.getCode());
			tag.setValue(req.getDestKey());
			tagList.add(tag);
			t.setTagList(tagList);
			//调用重命名接口
			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.rename(t);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(StringUtils.join(s3ObjectBO.getPath(), s3ObjectBO.getFileName()));
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());
			uploadFieDTO.setTagFileName(s3ObjectBO.getTagFilename());
			return uploadFieDTO;
		});
	}


	@Override
	public BaseResponse<Boolean> isExistFile(@RequestBody FileExistRequest request){
		return ServiceProvider.call(request, FileExistRequestV.class, Boolean.class, req -> {
			List<String> buckets = req.getBucketNames();
			if(CollectionUtils.isEmpty(buckets)){
				buckets = bizConfig.getAnnouncementBuckets();
			}

			if(!CollectionUtils.isEmpty(buckets)){
				S3ObjectBO bo = new S3ObjectBO();
				bo.setVersionId(req.getVersionId());
				setPathAndFileName(bo, req.getFileName());
				for(String bucket:buckets){
					bo.setBucketName(bucket);
					try{
						fileService.getBaseInfo(bo);
						return Boolean.TRUE;
					}catch (AppLogicException e){
						log.warn("NoSuchKeyException:{}:{}",bucket,req.getFileName());
					}

				}
			}

			return Boolean.FALSE;
		});
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> multiDownloadFile(HttpServletRequest request, HttpServletResponse response, MultiDownloadFileRequest downRequest){
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		
		List<String> validate = new ArrayList<>();
		try{
			MultiDownloadFileRequestV validator = BeanCopyUtil.copy(downRequest, MultiDownloadFileRequestV.class);
			BeanValidator beanValidator = SpringContextUtil.getBean("beanValidator", BeanValidator.class);
			validate = beanValidator.validate(validator, BeanValidator.CASE_DEFAULT);
			
			if(!CollectionUtils.isEmpty(validate)){
				baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
				baseResponse.setMsg(RespCodeEnum.ILLEGAL_PARAMETER.getName());
				baseResponse.setValidations(validate);
				return baseResponse;
			}
			
			//从头信息取Range:bytes=0-1000
			String range = request.getHeader("Range");
			
			//对Range数值做校验
			Long[] byteLength = checkRange(range);
			
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(downRequest.getBucketName());
			setPathAndFileName(t, downRequest.getFileId());
			t.setVersionId(downRequest.getVersionId());
			t.setRange(StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]));
			
			//数据权限校验
			fileService.checkDataOpAuth(t, OPAuthTypeEnum.READ.getCode());
			
			S3ObjectBO s3ObjectBO = (S3ObjectBO) fileService.getInfoAndBody(t);
			
			//设置响应头：Content-Range: bytes 0-2000/4932
			byteLength[1] = byteLength[1] > s3ObjectBO.getContentLength()-1 ? s3ObjectBO.getContentLength()-1 : byteLength[1];
			String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1],"/",s3ObjectBO.getContentLength());
			response.setHeader("Content-Range",rspRange);
			
			//获取标签中的文件名称
			String tagValue = fileService.getTagValue(s3ObjectBO.getTagList(), S3TagKeyEnum.FILE_NAME.getCode());
			String fileId = StringUtils.isEmpty(tagValue) ? t.getFileName() : tagValue;
			
			//下载数据
			downloadFileByte(request, response, fileId, s3ObjectBO.getBodys());

			return null;
		}catch (AppLogicException e){
			log.error("multiDownloadFile.AppLogicException:",e);
			baseResponse.setMsg(e.getMessage());
			return baseResponse;
		}catch (Exception e){
			log.error("multiDownloadFile.Exception:",e);
			baseResponse.setMsg(e.getMessage());
			return baseResponse;
		}
    }
    
    /**
     * 校验range
	 * 	支持以下3种格式
	 * 	bytes=500-999` 表示第500-999字节范围的内容。
	 * 	bytes=-500` 表示最后500字节的内容。
	 * 	bytes=500-` 表示从第500字节开始到文件结束部分的内容。
     * @description
     * @author miaomingming
     * @date 18:07 2020/4/2
     * @param range
     * @return java.lang.String
     **/
    private Long[] checkRange(String range){
		Long[] byteLength = new Long[2];
		byteLength[0] = 0L;
		byteLength[1] = BizConstants.MULTI_DOWN_FILE_MAX_SIZE-1;
		if(StringUtils.isBlank(range)){
			return byteLength;
		}
		long start = 0,end = 0;
		try{
			String[] ranges = range.substring(6).split("-");
			if(ranges.length != 2){
				log.error("multi.download.range.format.error:{}",range);
				throw new AppLogicException(ErrCodeEnum.MULTI_DOWNLOAD_FILE_FORMAT_RANGE_ERROR.getCode());
			}
			if(StringUtils.isNotBlank(ranges[0])){
				start = Long.parseLong(ranges[0]);
			}
			if(StringUtils.isNotBlank(ranges[0])){
				end = Long.parseLong(ranges[1]);
			}
			if(end - start >= BizConstants.MULTI_DOWN_FILE_MAX_SIZE - 1){
				end = start + BizConstants.MULTI_DOWN_FILE_MAX_SIZE - 1;
			}
			
			byteLength[0] = start;
			byteLength[1] = end;
			return byteLength;
		}catch (Exception e){
			log.error("multi.download.range.format.error:{},",range,e);
			throw new AppLogicException(ErrCodeEnum.MULTI_DOWNLOAD_FILE_FORMAT_RANGE_ERROR.getCode());
		}
	}

	@Override
	public BaseResponse<String> createMultipart(CreateMultipartRequest request) {
		return ServiceProvider.call(request, CreateMultipartRequestV.class, String.class , req -> {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			//设置标签信息
			List<TagBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(req.getFileName())){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.FILE_NAME.getCode());
				tag.setValue(req.getFileName());
				tagList.add(tag);
			}
			t.setTagList(fileService.addTags(t, tagList));

			return fileService.createMultipartUpload(t);
		});
	}

	@Override
	public BaseResponse<MultipartUploadRequest> uploadPart(HttpServletRequest request) {
		return ServiceProvider.call(request, null, MultipartUploadRequest.class, req -> {

			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(request.getParameter("bucketName"));
			setPathAndFileName(t, request.getParameter("fileId"));
			t.setUploadId(request.getParameter("uploadId"));
			t.setPartNumber(
					StringUtils.isEmpty(request.getParameter("partNumber")) ? 1 : Integer.parseInt(request.getParameter("partNumber")));
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;

			byte[] bytes = null;
			try {
				bytes = multipartHttpServletRequest.getFile("file").getBytes();
			} catch (IOException e) {
				log.error("--uploadFile获取文件流异常--：{}", e);
				throw new AppLogicException("获取文件流异常");
			}
			t.setBodys(bytes);

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.uploadPart(t);
			MultipartUploadRequest response = new MultipartUploadRequest();
			response.setPartNumber(s3ObjectBO.getPartNumber());
			response.setETag(s3ObjectBO.getETag());

			return response;
		});
	}

	@Override
	public BaseResponse<UploadFieDTO> completeMultipart(CompleteMultipartRequest request) {
		return ServiceProvider.call(request, CompleteMultipartRequestV.class, UploadFieDTO.class, req -> {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setUploadId(req.getUploadId());
			t.setETagList(BeanCopyUtil.copyList(req.getETagList(), MultipartUploadBO.class));

			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.completeMultipartUpload(t);

			//设置返回参数
			UploadFieDTO uploadFieDTO = new UploadFieDTO();
			uploadFieDTO.setFolderName(s3ObjectBO.getBucketName());
			uploadFieDTO.setFileName(StringUtils.join(s3ObjectBO.getPath(), s3ObjectBO.getFileName()));
			uploadFieDTO.setVersionId(s3ObjectBO.getVersionId());
			return uploadFieDTO;

		});
	}
	
	
	@Override
	public BaseResponse<String> abortMultipartUpload(@RequestBody AbortMultipartRequest request){
		return ServiceProvider.call(request, CompleteMultipartRequestV.class, String.class, req -> {
			S3ObjectBO t = new S3ObjectBO();
			t.setBucketName(req.getBucketName());
			setPathAndFileName(t, req.getFileId());
			t.setUploadId(req.getUploadId());
			
			fileService.abortMultipartUpload(t);
			
			return RespCodeEnum.SUCCESS.getName();
			
		});
	}
}
