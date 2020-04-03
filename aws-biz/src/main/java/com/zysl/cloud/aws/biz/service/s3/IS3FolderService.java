package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFolderService;

public interface IS3FolderService<T> extends IFolderService<T> {

    /**
     * 查询最新对象的版本号
     * @param t
     * @return
     */
    String getLastVersion(T t);

}
