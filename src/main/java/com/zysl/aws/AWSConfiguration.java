//package com.zysl.aws;
//
//import com.zysl.aws.utils.MyConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
//import software.amazon.awssdk.http.SdkHttpConfigurationOption;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.utils.AttributeMap;
//
//import java.net.URI;
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//public class AWSConfiguration {
//
//	/*@Value("${cloud.aws.accessKey}")
//	private String accessKey;
//	@Value("${cloud.aws.secretKey}")
//	private String secretKey;
//	@Value("${cloud.aws.endpoint}")
//	private String endpoint;*/
//
//
//	public static Map<String, Object> amazonS3Client(MyConfig myConfig) throws Exception {
//		Map<String, Object> awsMap = new HashMap<>();
//
//		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
//		AttributeMap attributeMap = AttributeMap.builder()
//				.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
//				.put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))
//				.put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))
//				.put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))
//				.put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))
//				.build();
//
//		//获取配置信息
//		List<Map<String, Object>> listProps = myConfig.getListProps();
//		for (int i = 0; i < listProps.size(); i++) {
//			Map<String, Object> map = listProps.get(i);
//			//服务器别名
//			String h_name = map.get("hostName").toString();
//			//s3服务器连接登陆用户、密码、地址
//			String accessKey = map.get("accessKey").toString();
//			String secretKey = map.get("secretKey").toString();
//			String endpoint = map.get("endpoint").toString();
//
//			//初始化s3连接
//			AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
//			S3Client s3Client = S3Client.builder().
//					httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
//					credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
//					endpointOverride(URI.create(endpoint)).
//					region(Region.US_EAST_1).
//					build();
//			awsMap.put(h_name, s3Client);
//		}
//
//		return awsMap;
//	}
//
//	/*@Bean
//	public S3Client amazonS3Client() throws Exception {
//
//		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
//		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
//		AttributeMap attributeMap = AttributeMap.builder()
//				.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
//				.put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))
//				.put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))
//				.put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))
//				.put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))
//				.build();
//
//		S3Client s3Client = S3Client.builder().
//				httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
//				credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
//				endpointOverride(URI.create(endpoint)).
//				region(Region.US_EAST_1).
//				build();
//
//		return s3Client;
//	}
//	*/
//}
