package com.zysl.cloud.aws.biz.constant;

public interface S3Method {

	public static final String CREATE_BUCKETS = "createBucket";
	public static final String LIST_BUCKETS = "listBuckets";
	public static final String PUT_BUCKET_VERSIONING = "putBucketVersioning";
	public static final String PUT_OBJECT = "putObject";
	public static final String COPY_OBJECT = "copyObject";
	public static final String DELETE_OBJECTS = "deleteObjects";
	public static final String LIST_OBJECT_VERSIONS = "listObjectVersions";
	public static final String HEAD_OBJECT = "headObject";
	public static final String GET_OBJECT_TAGGING = "getObjectTagging";
	public static final String PUT_OBJECT_TAGGING = "putObjectTagging";
	public static final String LIST_OBJECTS = "listObjects";
	public static final String CREATE_MULTIPART_UPLOAD = "createMultipartUpload";
	public static final String UPLOAD_PART = "uploadPart";
	public static final String COMPLETE_MULTIPART_UPLOAD = "completeMultipartUpload";

}
