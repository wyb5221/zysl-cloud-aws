package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.ObjectInfoDTO;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.api.enums.KeyTypeEnum;
import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.api.req.CreateFolderRequest;
import com.zysl.cloud.aws.api.req.DelObjectRequest;
import com.zysl.cloud.aws.api.req.QueryObjectsRequest;
import com.zysl.cloud.aws.api.srv.FolderSrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IFolderService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;
import com.zysl.cloud.aws.web.validator.CopyObjectsRequestV;
import com.zysl.cloud.aws.web.validator.CreateFolderRequestV;
import com.zysl.cloud.aws.web.validator.DelObjectRequestV;
import com.zysl.cloud.aws.web.validator.QueryObjectsRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RestController
public class FolderController extends BaseController implements FolderSrv {

    @Autowired
    private IS3FolderService folderService;
    @Autowired
    private IS3FileService fileService;

    @Override
    public BaseResponse<String> createFolder(CreateFolderRequest request) {
        return ServiceProvider.call(request, CreateFolderRequestV.class, String.class, req ->{
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getFolderName() + "/");

            folderService.create(t);
            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

    @Override
    public BaseResponse<String> deleteFolder(DelObjectRequest request) {
        return ServiceProvider.call(request, DelObjectRequestV.class, String.class, req ->{
            //先查询文件夹下的对象信息
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getKey() + "/");

            folderService.delete(t);

            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

    @Override
    public BasePaginationResponse<ObjectInfoDTO> getS3Objects(QueryObjectsRequest request) {
        return ServiceProvider.callList(request, QueryObjectsRequestV.class, ObjectInfoDTO.class, (req, page) ->{

            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getKey() + "/");

            S3ObjectBO s3ObjectBO = (S3ObjectBO)folderService.getDetailInfo(t);

            List<ObjectInfoBO> objectList = Lists.newArrayList();
            //查询类型，0默认全部，1仅目录2仅文件
            if(KeyTypeEnum.FOLDER.getCode().equals(req.getKeyType())){
                //只返回目录
                objectList = s3ObjectBO.getFolderList();
            }else if(KeyTypeEnum.FILE.getCode().equals(req.getKeyType())){
                //只返回文件
                objectList = s3ObjectBO.getFileList();
            }else{
                //返回文件和文件夹
                objectList.addAll(s3ObjectBO.getFolderList());
                objectList.addAll(s3ObjectBO.getFileList());
            }

            //在判断标签权限
            if(!StringUtils.isEmpty(req.getUserId())){
                //userid不为空是，需要校验权限
                List<ObjectInfoBO> objecs = objectList.stream().filter(obj -> isTageExist(req.getUserId(), req.getBucketName(), obj.getKey(),"")).collect(Collectors.toList());

                return BeanCopyUtil.copyList(objecs, ObjectInfoDTO.class);
            }else {
                return BeanCopyUtil.copyList(objectList, ObjectInfoDTO.class);
            }
        });
    }

    //判断是否有权限
    public boolean isTageExist(String userId, String bucket, String key, String versionId) {
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName(bucket);
        t.setVersionId(versionId);
        setPathAndFileName(t, key);
        List<TagsBO> tagList = fileService.getTag(t);

        for (TagsBO tag : tagList) {
            //判断标签可以是否是owner
            if(S3TagKeyEnum.FILE_NAME.getCode().equals(tag.getKey()) &&
                    userId.equals(tag.getValue())){
                //在判断标签value
                return true;
            }
        }
        return false;
    }

    @Override
    public BaseResponse<String> copyFolder(CopyObjectsRequest request) {
        return ServiceProvider.call(request, CopyObjectsRequestV.class, String.class, req -> {
            S3ObjectBO src = new S3ObjectBO();
            src.setBucketName(req.getSourceBucket());
            setPathAndFileName(src, req.getSourceKey() + "/");
            S3ObjectBO dest = new S3ObjectBO();
            dest.setBucketName(req.getDestBucket());
            setPathAndFileName(dest, req.getDestKey() + "/");

            folderService.copy(src, dest);
            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

    @Override
    public BaseResponse<String> moveFolder(CopyObjectsRequest request) {
        return ServiceProvider.call(request, CopyObjectsRequestV.class, String.class, req ->{
            S3ObjectBO src = new S3ObjectBO();
            src.setBucketName(req.getSourceBucket());
            setPathAndFileName(src, req.getSourceKey() + "/");
            S3ObjectBO dest = new S3ObjectBO();
            dest.setBucketName(req.getDestBucket());
            setPathAndFileName(dest, req.getDestKey() + "/");
            //先复制
            boolean copyFlag = folderService.copy(src, dest);

            if(copyFlag){
                S3ObjectBO t = new S3ObjectBO();
                t.setBucketName(req.getSourceBucket());
                setPathAndFileName(t, req.getSourceKey() + "/");
                t.setDeleteStore(DeleteStoreEnum.COVER.getCode());
                folderService.delete(t);
            }

            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

}
