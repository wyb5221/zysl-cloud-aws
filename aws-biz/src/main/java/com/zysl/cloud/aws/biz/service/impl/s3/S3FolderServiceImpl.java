package com.zysl.cloud.aws.biz.service.impl.s3;

import com.zysl.cloud.aws.biz.service.IFolderService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import java.util.List;

public class S3FolderServiceImpl implements IFolderService<S3ObjectBO> {
	@Override
	public S3ObjectBO create(S3ObjectBO t){
		return null;
	}
	@Override
	public void delete(S3ObjectBO t){

	}

	@Override
	public void modify(S3ObjectBO t){

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public void copy(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){

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
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		return null;
	}
}
