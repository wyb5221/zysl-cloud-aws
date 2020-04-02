package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ObjectBO extends BaseFileBO implements Serializable {

	private static final long serialVersionUID = 930239064897318736L;

	private String bucketName;

	private Date lastModified;

	//是否物理删除，1是0否，默认0
	private Integer deleteStore;
	//权限校验参数
	private String userId;
	//断点续传id
	private String uploadId;
	//续传次数
	private Integer partNumber;
	//断点续传标记
	private String eTag;
	private List<MultipartUploadBO> eTagList;

	//元素 List
	//标签 List
	List<TagBO> tagList;
	//子目录 List
	List<ObjectInfoBO> folderList;
	//子文件 List
	List<ObjectInfoBO> fileList;

}
