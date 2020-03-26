package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Getter
public class ShareFileRequestV implements IValidator {
  //文件夹名称
  @NotBlank
  private String bucketName;
  //文件名
  @NotBlank
  private String fileName;

  @Override
  public void customizedValidate(List<String> errors, Integer userCase) {

  }
}
