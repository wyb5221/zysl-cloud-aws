package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.aws.api.dto.KeyVersionDTO;
import com.zysl.cloud.aws.api.dto.TagDTO;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 修改文件tag入参对象
 */
@Setter
@Getter
@ApiModel(description = "修改文件tag请求对象")
public class SetFileTagRequest extends BaseReqeust {
    private static final long serialVersionUID = -7591314280634497629L;
    //服务器bucket名称
    @ApiModelProperty(value = "服务器bucket名称", name = "bucket",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucket;
    //文件集合
    @ApiModelProperty(value = "文件集合", name = "keyList",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
    private List<KeyVersionDTO> keyList;
    //标签集合
    @ApiModelProperty(value = "标签集合", name = "tageList",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
    private List<TagDTO> tageList;

    @Override
    public String toString() {
        return "UpdateFileTageRequest{" +
                "bucket='" + bucket + '\'' +
                ", keyList=" + keyList +
                ", tageList=" + tageList +
                '}';
    }
}
