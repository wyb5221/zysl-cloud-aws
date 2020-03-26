package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.aws.api.dto.KeyVersionDTO;
import com.zysl.cloud.aws.api.dto.TageDTO;
import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 修改文件tage入参对象
 */
@Setter
@Getter
public class SetFileTagRequest extends BaseReqeust {
    private static final long serialVersionUID = -7591314280634497629L;
    //服务器bucket名称
    private String bucket;
    //文件集合
    private List<KeyVersionDTO> keyList;
    //标签集合
    private List<TageDTO> tageList;

    @Override
    public String toString() {
        return "UpdateFileTageRequest{" +
                "bucket='" + bucket + '\'' +
                ", keyList=" + keyList +
                ", tageList=" + tageList +
                '}';
    }
}
