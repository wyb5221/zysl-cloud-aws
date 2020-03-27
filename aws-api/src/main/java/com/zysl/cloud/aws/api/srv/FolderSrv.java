package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.ObjectInfoDTO;
import com.zysl.cloud.aws.api.req.CreateFolderRequest;
import com.zysl.cloud.aws.api.req.DelObjectRequest;
import com.zysl.cloud.aws.api.req.QueryObjectsRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 目录处理类
 */
//@RequestMapping("/aws/folder")
public interface FolderSrv {

    /**
     * 创建目标
     * @param request
     * @return
     */
    @PostMapping("/add")
    BaseResponse<String> createFolder(@RequestBody CreateFolderRequest request);

    /**
     * 删除目录
     * @param bucketName
     * @param key
     * @return
     */
    @PostMapping("/delete")
    BaseResponse<String> deleteFile(@RequestBody DelObjectRequest request);

    /**
     * 查询目录对象列表
     * @return
     */
    @PostMapping("/objects")
    BasePaginationResponse<ObjectInfoDTO> getS3Objects(@RequestBody QueryObjectsRequest request);

}
