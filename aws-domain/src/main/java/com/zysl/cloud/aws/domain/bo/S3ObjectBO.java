package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ObjectBO extends S3BaseBO implements Serializable {

	private static final long serialVersionUID = 930239064897318736L;

	//数据主体
	private byte[] bodys;


}
