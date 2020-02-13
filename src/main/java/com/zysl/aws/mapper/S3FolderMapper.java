package com.zysl.aws.mapper;

import com.zysl.aws.model.db.S3Folder;
import java.util.List;

public interface S3FolderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Wed Feb 12 18:15:26 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Wed Feb 12 18:15:26 CST 2020
     */
    int insert(S3Folder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Wed Feb 12 18:15:26 CST 2020
     */
    S3Folder selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Wed Feb 12 18:15:26 CST 2020
     */
    List<S3Folder> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Wed Feb 12 18:15:26 CST 2020
     */
    int updateByPrimaryKey(S3Folder record);
}