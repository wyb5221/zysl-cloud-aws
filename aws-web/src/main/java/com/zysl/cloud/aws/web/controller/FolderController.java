package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.zysl.aws.web.config.BizConstants;
import com.zysl.aws.web.enums.DeleteStoreEnum;
import com.zysl.aws.web.enums.KeyTypeEnum;
import com.zysl.cloud.aws.api.dto.ObjectInfoDTO;
import com.zysl.cloud.aws.api.req.CreateFolderRequest;
import com.zysl.cloud.aws.api.req.DelObjectRequest;
import com.zysl.cloud.aws.api.req.QueryObjectsRequest;
import com.zysl.cloud.aws.api.srv.FolderSrv;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IFolderService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;
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
            setPathAndFileName(t, req.getFolderName());

            folderService.create(t);
            return RespCodeEnum.SUCCESS.getDesc();
        });
    }

    @Override
    public BaseResponse<String> deleteFile(DelObjectRequest request) {
        return ServiceProvider.call(request, DelObjectRequestV.class, String.class, req ->{
            //先查询文件夹下的对象信息
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getKey());
            S3ObjectBO s3ObjectBO = (S3ObjectBO)folderService.getDetailInfo(t);
            List<ObjectInfoBO> folderList = s3ObjectBO.getFolderList();
            //删除文件
            List<ObjectInfoBO> fileList = s3ObjectBO.getFileList();
//            if(delfile){
//
//
//            }
            return null;
        });
    }
    public boolean delfile(String bucket, List<ObjectInfoBO> fileList){
        //删除文件信息
        fileList.forEach(obj -> {
            S3ObjectBO file = new S3ObjectBO();
            file.setBucketName(bucket);
            setPathAndFileName(file, obj.getKey());
            file.setDeleteStore(DeleteStoreEnum.COVER.getCode());
            fileService.delete(file);
        });
        return true;
    }

    public boolean operFolder(String bucket, List<ObjectInfoBO> folderList){
        for (ObjectInfoBO object : folderList) {
            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(bucket);
            setPathAndFileName(t, object.getKey());
            S3ObjectBO s3ObjectBO = (S3ObjectBO)folderService.getDetailInfo(t);
            List<ObjectInfoBO> files = s3ObjectBO.getFolderList();
            //删除文件信息
            files.forEach(obj -> {
                S3ObjectBO file = new S3ObjectBO();
                file.setBucketName(bucket);
                setPathAndFileName(file, obj.getKey());
                file.setDeleteStore(DeleteStoreEnum.COVER.getCode());
                fileService.delete(file);
            });
            //文件夹
            List<ObjectInfoBO> folders = s3ObjectBO.getFolderList();
            if(!CollectionUtils.isEmpty(folderList)){
                operFolder(bucket, folders);
            }else{
                //删除文件夹
                S3ObjectBO del = new S3ObjectBO();
                del.setBucketName(bucket);
                setPathAndFileName(del,  object.getKey());
                folderService.delete(del);
            }
        }
        return true;
    }

    @Override
    public BasePaginationResponse<ObjectInfoDTO> getS3Objects(QueryObjectsRequest request) {
        return ServiceProvider.callList(request, QueryObjectsRequestV.class, ObjectInfoDTO.class, (req, page) ->{

            S3ObjectBO t = new S3ObjectBO();
            t.setBucketName(req.getBucketName());
            setPathAndFileName(t, req.getKey());

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
                objectList.stream().filter(obj -> isTageExist(req.getUserId(), req.getBucketName(), obj.getKey(),"")).collect(Collectors.toList());

                return BeanCopyUtil.copyList(objectList, ObjectInfoDTO.class);
            }else {
                return BeanCopyUtil.copyList(objectList, ObjectInfoDTO.class);
            }
        });
    }

    public boolean isTageExist(String userId, String bucket, String key, String versionId) {
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName(bucket);
        t.setVersionId(versionId);
        setPathAndFileName(t, key);
        S3ObjectBO object = (S3ObjectBO)fileService.getDetailInfo(t);
        List<TagsBO> tagList = object.getTagList();

        for (TagsBO tag : tagList) {
            //判断标签可以是否是owner
            if(BizConstants.TAG_OWNER.equals(tag.getKey()) &&
                    userId.equals(tag.getValue())){
                //在判断标签value
                return true;
            }
        }
        return false;
    }
}
