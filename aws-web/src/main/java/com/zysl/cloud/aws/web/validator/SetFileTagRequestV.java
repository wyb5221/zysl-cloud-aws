package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.dto.KeyVersionDTO;
import com.zysl.cloud.aws.api.dto.TageDTO;
import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 修改文件tage入参对象
 */
@Setter
@Getter
public class SetFileTagRequestV implements IValidator {
    //服务器bucket名称
    @NotBlank
    private String bucket;
    //文件集合
    private List<KeyVersionDTO> keyList;
    //标签集合
    private List<TageDTO> tageList;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {
        if(CollectionUtils.isEmpty(keyList)){
            errors.add("文件名不能为空");
        }
        if(CollectionUtils.isEmpty(tageList)){
            errors.add("标签信息不能为空");
        }
    }
}
