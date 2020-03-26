package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ObjectBO extends BaseFileBO implements Serializable {

	private static final long serialVersionUID = 930239064897318736L;

	private String bucketName;

	private Date lastModified;

	//元素 List
	//标签 List
	//子目录 List
	//子文件 List
}
