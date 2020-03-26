package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.WordToPDFDTO;
import com.zysl.cloud.aws.api.req.WordToPDFRequest;
import com.zysl.cloud.aws.api.srv.WordSrv;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.biz.service.IWordService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.utils.BizUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class WordController extends BaseController implements WordSrv {

	@Autowired
	private IWordService wordService;
	@Autowired
	private IPDFService pdfService;
	@Resource(name="s3FileService")
	private IFileService fileService;


	@Override
	public BaseResponse<WordToPDFDTO> changeWordToPdf(@RequestBody WordToPDFRequest request){
		log.info("===changeWordToPdf.param:{}===",request);
		BaseResponse<WordToPDFDTO> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(false);
		//step 1.文件后缀校验
		if(StringUtils.isBlank(request.getBucketName()) || StringUtils.isBlank(request.getFileName())){
			log.info("===文件夹或文件名为空:{}===",request);
			baseResponse.setMsg("文件夹或文件名为空.");
			return baseResponse;
		}
		if(!request.getFileName().toLowerCase().endsWith("doc")
			   && !request.getFileName().toLowerCase().endsWith("docx")){
			log.info("===不是word文件:{}===",request.getFileName());
			baseResponse.setMsg("不是word文件.");
			return baseResponse;
		}
		//step 2.读取源文件--
		//调用s3接口下载文件内容
		S3ObjectBO queryBO = new S3ObjectBO();
		queryBO.setBucketName(request.getBucketName());
		queryBO.setFileName(request.getFileName());
		queryBO.setVersionId(request.getVersionId());
		S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getInfoAndBody(queryBO);

		if(s3ObjectBO == null || s3ObjectBO.getBodys() == null || s3ObjectBO.getBodys().length == 0){
			log.info("===未查询到数据文件:{}===",request.getFileName());
			baseResponse.setMsg("未查询到数据文件.");
			return baseResponse;
		}

		byte[] inBuff = s3ObjectBO.getBodys();
		if(null == inBuff){
			log.info("===文件不存在:{}===",request.getFileName());
			baseResponse.setMsg("文件不存在.");
			return baseResponse;
		}
//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] inBuff = null;
//        try {
//            inBuff = decoder.decodeBuffer(fileStr);
//        } catch (IOException e) {
//            log.error("--changeWordToPdf文件下载异常：--{}", e);
//        }

		//step 3.word转pdf、加水印 300,300
		String fileName = BizUtil.getTmpFileNameWithoutSuffix(request.getFileName());
		byte[] outBuff = wordService.changeWordToPDF(fileName, inBuff,false, request.getTextMark());
		log.info("===changeToPDF===outBuff,length:{}", outBuff != null ? outBuff.length : 0);
		if(outBuff == null || outBuff.length == 0){
			log.info("===changeToPDF===pdfFileData is null .fileName:{}",request.getFileName());
			baseResponse.setMsg("word转换的pdf大小为0..");
			return baseResponse;
		}

		//step 4.实现加密
		if(!StringUtils.isBlank(request.getUserPwd()) && !StringUtils.isBlank(request.getOwnerPwd())){
			byte[] addPwdOutBuff = pdfService.addPwd(outBuff,request.getUserPwd(),request.getOwnerPwd());
			if(addPwdOutBuff == null || addPwdOutBuff.length == 0){
				log.info("===addPwd===file add pwd err.fileName:{}",request.getFileName());
				baseResponse.setMsg("word转换的pdf加密后大小为0..");
				return baseResponse;
			}
		}

		//step 5.上传到temp-001
//        BASE64Encoder encoder = new BASE64Encoder();
//        String str = encoder.encode(outBuff);
		S3ObjectBO addRequestBO = new S3ObjectBO();
		addRequestBO.setBucketName(request.getBucketName());
		addRequestBO.setFileName(fileName + "text.pdf");
		addRequestBO.setVersionId(request.getVersionId());
		addRequestBO.setBodys(outBuff);

		S3ObjectBO addFileRst = (S3ObjectBO)fileService.create(addRequestBO);

		WordToPDFDTO dto = new WordToPDFDTO();
		if(null != addFileRst){
			//文件上传成功
			dto.setVersionId(addFileRst.getVersionId());
		}
		//step 7.设置返回参数
		dto.setBucketName(request.getBucketName());
		dto.setFileName(fileName + "text.pdf");
		baseResponse.setModel(dto);
		baseResponse.setSuccess(true);
		return baseResponse;
	}
}
