package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeyPageRequest extends BasePaginationRequest {


	String name;
}
