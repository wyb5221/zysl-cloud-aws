package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.config.S3ServerConfig;
import com.zysl.cloud.aws.prop.S3ServerProp;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.AttributeMap;

@Slf4j
@Service
public class S3FactoryServiceImpl implements IS3FactoryService {

	//s3服务器连接信息 key为SERVER_NO
	private Map<String, S3Client> S3_SERVER_CLIENT_MAP = new ConcurrentHashMap<>();

	//s3服务器 key为SERVER_NO
	private Map<String, S3ServerProp> S3_SERVER_MAP = new ConcurrentHashMap<>();

	//s3服务器的bucket--serverNo
	private Map<String, String> S3_BUCKET_SERVER_MAP = new ConcurrentHashMap<>();

	@Autowired
	S3ServerConfig s3ServerConfig;


	@Override
	public String getServerNo(String bucketName){
		if(!this.S3_BUCKET_SERVER_MAP.containsKey(bucketName)){
			log.error("not.exist.bucketName:{}",bucketName);
			throw new AppLogicException(ErrCodeEnum.S3_BUCKET_NOT_EXIST.getCode());
		}
		return this.S3_BUCKET_SERVER_MAP.get(bucketName);
	}


	@Override
	public S3Client getS3ClientByServerNo(String serverNo){
		if(!this.S3_SERVER_CLIENT_MAP.containsKey(serverNo)){
			log.error("not.exist.serverNo:{}",serverNo);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_NO_NOT_EXIST.getCode());
		}
		return this.S3_SERVER_CLIENT_MAP.get(serverNo);
	}


	@Override
	public S3Client getS3ClientByBucket(String bucketName){
		return getS3ClientByBucket(bucketName,Boolean.FALSE);
	}

	@Override
	public S3Client getS3ClientByBucket(String bucketName,Boolean isWrite) throws AppLogicException{
		String serverNo = getServerNo(bucketName);
		if(isWrite != null && isWrite){
			S3ServerProp prop = this.S3_SERVER_MAP.get(serverNo);
			if(prop != null && prop.getNoSpace() != null && prop.getNoSpace()){
				log.error("s3.no.space.serverNo:{}",serverNo);
				throw new AppLogicException(ErrCodeEnum.S3_NO_SPACE_WARN.getCode());
			}
		}

		return getS3ClientByServerNo(serverNo);
	}

	@Override
	public Boolean isExistBucket(String bucketName){
		return this.S3_BUCKET_SERVER_MAP.containsKey(bucketName);
	}

	@Override
	public void addBucket(String bucketName,String serverNo){
		this.S3_BUCKET_SERVER_MAP.put(bucketName,serverNo);
	}

	@Override
	public Map<String, String> getBucketServerNoMap(){
		return this.S3_BUCKET_SERVER_MAP;
	}


	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName)
		throws AppLogicException{
		return callS3MethodWithBody(r,null,s3Client,methodName);
	}

	@Override
	public <T extends S3Response,R extends S3Request>T callS3MethodWithBody(R r, RequestBody requestBody,S3Client s3Client,String methodName) throws AppLogicException{
		log.info("=callS3Method:service_name:{},methodName:{},param:{}=",S3Client.SERVICE_NAME,methodName, JSON.toJSONString(r));
		T response;
		try{
			if(requestBody == null){
				Method method = S3Client.class.getMethod(methodName, r.getClass());
				response = (T)method.invoke(s3Client,r);
			}else{
				Method method = S3Client.class.getMethod(methodName, r.getClass(),RequestBody.class);
				response = (T)method.invoke(s3Client,r,requestBody);
			}

			if(response == null || response.sdkHttpResponse() == null ){
				log.error("callS3Method.invoke({})->no.response",methodName);
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_RESPONSE.getCode());
			}else if(response.sdkHttpResponse().statusCode() != RespCodeEnum.SUCCESS.getCode().intValue()){
				log.error("callS3Method.invoke({})->response.status.error:{}",methodName,response.sdkHttpResponse().statusCode());
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_RESPONSE_STATUS_ERROR.getCode());
			}else{
				log.info("callS3Method.invoke({}).success:{}",methodName, JSON.toJSONString(response));
			}
		}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			log.error("callS3Method.invoke({}).error:",methodName,e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_INVOKE_ERROR.getCode());
		}catch (NoSuchMethodException e){
			log.error("callS3Method.invoke({})->noSuchMethod:",methodName);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH.getCode());
		}catch (Exception e){
			log.error("callS3Method.error({}):",methodName,e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
		}
		return response;
	}




	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName,Boolean throwLogicException){
		log.info("=callS3Method:service_name:{},methodName:{},param:{}=",S3Client.SERVICE_NAME,methodName, JSON.toJSONString(r));
		T response = null;
		try{
			response = callS3MethodWithBody(r,null,s3Client,methodName);
		}catch (NoSuchKeyException e){
			log.error("callS3Method.invoke({})->NoSuchKeyException:",methodName);
			if(throwLogicException){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
		}catch (AwsServiceException e){
			log.error("callS3Method.invoke({})->AwsServiceException:",methodName);
			if(throwLogicException){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_AWS_SERVICE_EXCEPTION.getCode());
			}
		}catch (Exception e) {//SdkClientException等
			log.error("callS3Method.error({}):", methodName, e);
			if (throwLogicException) {
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
			}
		}
		return response;
	}




	@PostConstruct
	private void awsS3Init(){
		amazonS3ClientInit();
		//遍历服务器，将bucket-serverNo查询保存到map
		amazonS3BucketInit();
	}

	/**
	 * 客户端连接护初始化
	 * @description
	 * @author miaomingming
	 * @date 17:55 2020/3/23
	 * @param []
	 * @return void
	 **/
	private void amazonS3ClientInit(){
		log.info("=amazonS3ClientInit.start=");
		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
		AttributeMap attributeMap = AttributeMap.builder()
										.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
										.put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))//写入超时
										.put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))//读取超时
										.put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))//连接超时
										.put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))//连接最大空闲超时
										.build();

		List<S3ServerProp> s3ServerProps = s3ServerConfig.getServers();
		if(!CollectionUtils.isEmpty(s3ServerProps)){
			for (S3ServerProp props : s3ServerProps) {

				this.S3_SERVER_MAP.put(props.getServerNo(),props);

				//初始化s3连接
				AwsBasicCredentials awsCreds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
				S3Client s3Client = S3Client
									.builder()
									.httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap))
									.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
									.endpointOverride(URI.create(props.getEndpoint()))
									.region(Region.US_EAST_1)
									.build();
				this.S3_SERVER_CLIENT_MAP.put(props.getServerNo(),s3Client);

				log.info("=amazonS3ClientInit.success:serverNo:{}-->{}=",props.getServerNo(),props.getEndpoint());
			}
		}else{
			log.info("=amazonS3ClientInit.warn:no server found.=");
		}

		log.info("=amazonS3ClientInit.end=");
	}

	/**
	 * bucket与serverNo对应关系查询初始化
	 * @description
	 * @author miaomingming
	 * @date 21:39 2020/3/23
	 * @param []
	 * @return void
	 **/
	private void amazonS3BucketInit(){
		log.info("=amazonS3BucketInit.start=");

		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();

		if(!this.S3_SERVER_CLIENT_MAP.isEmpty()){
			for(String serverNo:this.S3_SERVER_CLIENT_MAP.keySet()){
				S3Client s3Client = this.S3_SERVER_CLIENT_MAP.get(serverNo);
				ListBucketsResponse response = s3Client.listBuckets(listBucketsRequest);

				if(response != null && !CollectionUtils.isEmpty(response.buckets())){
					response.buckets().forEach(bucket -> {
						this.S3_BUCKET_SERVER_MAP.put(bucket.name(),serverNo);
						log.info("=amazonS3BucketInit.found.bucket:{}=", bucket.name());
					});
				}
			}
		}else{
			log.info("=amazonS3BucketInit.warn:no server found.=");
		}

		log.info("=amazonS3BucketInit.end=");
	}


}
