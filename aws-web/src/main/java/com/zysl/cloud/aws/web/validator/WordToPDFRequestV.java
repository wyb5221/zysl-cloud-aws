package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class WordToPDFRequestV implements IValidator {

  @Length(min = 3,max=63)
  @NotBlank
  private String bucketName;


  @Length(min = 1,max=1024)
  @NotBlank
  private String fileName;

  @Length(max=32)
  private String versionId;

  @Length(min = 4,max=16)
  private String textMark;

  @Length(min = 4,max=16)
  private String userPwd;

  @Length(min = 4,max=16)
  private String ownerPwd;


  @Override
  public void customizedValidate(List<String> errors, Integer userCase) {
    if(!fileName.toLowerCase().endsWith("doc")
           && !fileName.toLowerCase().endsWith("docx")){
      errors.add("后缀只能是doc或者docx");
    }
  }
}
