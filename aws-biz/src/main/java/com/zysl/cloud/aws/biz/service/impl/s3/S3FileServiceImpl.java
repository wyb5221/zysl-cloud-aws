package com.zysl.cloud.aws.biz.service.impl.s3;

import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.UploadFieBO;
import org.springframework.stereotype.Service;

@Service("s3FileService")
public class S3FileServiceImpl implements IFileService<S3ObjectBO> {

	@Override
	public void create(S3ObjectBO t){

	}
	@Override
	public void delete(S3ObjectBO t){

	}

	@Override
	public UploadFieBO shareFile(ShareFileRequest request) {
		return null;
	}

	@Override
	public boolean copyFile(CopyObjectsRequest request) {
		return false;
	}

	@Override
	public void modify(S3ObjectBO t){

	}
	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		return null;
	}
	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		return null;
	}

	@Override
	public S3ObjectBO getInfoAndBody(S3ObjectBO t){
		return null;
	}
}
