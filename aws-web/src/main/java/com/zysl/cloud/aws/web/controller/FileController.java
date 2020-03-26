package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.api.req.KeyPageRequest;
import com.zysl.cloud.aws.api.req.KeyRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.api.srv.FileSrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;
import com.zysl.cloud.aws.web.validator.CopyObjectsRequestV;
import com.zysl.cloud.aws.web.validator.KeyRequestV;
import com.zysl.cloud.aws.web.validator.ShareFileRequestV;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@CrossOrigin
@RestController
public class FileController extends BaseController implements FileSrv {

	@Autowired
	IS3BucketService bucketService;
	@Autowired
	IFileService fileService;

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
