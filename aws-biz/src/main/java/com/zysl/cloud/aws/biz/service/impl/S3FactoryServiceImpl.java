package com.zysl.cloud.aws.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.aws.biz.service.IS3FactoryService;
import com.zysl.cloud.aws.config.S3ServerConfig;
import com.zysl.cloud.aws.prop.S3ServerProp;
import com.zysl.cloud.utils.common.AppLogicException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.AttributeMap;
import springfox.documentation.spring.web.json.Json;

@Slf4j
@Service
public class S3FactoryServiceImpl implements IS3FactoryService {

	@Autowired
	S3ServerConfig s3ServerConfig;
	@Autowired
	IS3BucketService s3BucketService;


	@Override
	public String getServerNo(String bucketName){
		return BizConstants.S3_BUCKET_SERVER_MAP.get(bucketName);
	}


	@Override
	public S3Client getS3ClientByServerNo(String serverNo){
		return BizConstants.S3_SERVER_CLIENT_MAP.get(serverNo);
	}


	@Override
	public S3Client getS3ClientByBucket(String bucketName){
		return getS3ClientByServerNo(getServerNo(bucketName));
	}


	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName)
		throws AppLogicException{
		log.info("=putObjectTagging.param:{}=", JSON.toJSONString(r));
		T response;
		try{
			Method method = S3Client.class.getMethod(methodName, r.getClass());
			response = (T)method.invoke(s3Client,r);
			if(response == null){
				log.error("callS3Method.invoke({})->no.response",methodName);
				throw new AppLogicException("callS3Method.no.ressponse:" + methodName);
			}else{
				log.info("callS3Method.invoke({}).success:{}",methodName, JSON.toJSONString(response));
			}
		}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			log.error("callS3Method.invoke({}).error:",methodName,e);
			throw new AppLogicException("callS3Method.exception:" + methodName);
		}catch (NoSuchMethodException e){
			log.error("callS3Method.invoke({})->noSuchMethod:",methodName);
			throw new AppLogicException("callS3Method.noSuchMethod:" + methodName);
		}catch (Exception e){
			log.error("callS3Method.error({}):",methodName,e);
			throw new AppLogicException("callS3Method.error:" + methodName);
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
				//初始化s3连接
				AwsBasicCredentials awsCreds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
				S3Client s3Client = S3Client
									.builder()
									.httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap))
									.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
									.endpointOverride(URI.create(props.getEndpoint()))
									.region(Region.US_EAST_1)
									.build();
				BizConstants.S3_SERVER_CLIENT_MAP.put(props.getServerNo(),s3Client);

				log.info("=amazonS3ClientInit.success:{}=",props.getEndpoint());
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
		if(!BizConstants.S3_SERVER_CLIENT_MAP.isEmpty()){
			for(String serverNo:BizConstants.S3_SERVER_CLIENT_MAP.keySet()){
				List<Bucket> list = s3BucketService.getBucketList(BizConstants.S3_SERVER_CLIENT_MAP.get(serverNo));
				if(!CollectionUtils.isEmpty(list)){
					list.forEach(bucket -> {
						BizConstants.S3_BUCKET_SERVER_MAP.put(bucket.name(),serverNo);
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
