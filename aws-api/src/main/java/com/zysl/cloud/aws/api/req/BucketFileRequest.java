package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(description = "查询bucket下文件列表请求对象")
public class BucketFileRequest extends BasePaginationRequest {

}
