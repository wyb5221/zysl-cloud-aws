package com.zysl.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.utils.AttributeMap;

import java.net.URI;

@Configuration
public class AWSConfiguration {

	@Value("${cloud.aws.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.endpoint}")
	private String endpoint;

//	@Bean
//	public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
//        MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
//		return minioClient;
//	}

//	@Bean
//	public BasicAWSCredentials basicAWSCredentials() {
//		return new BasicAWSCredentials(accessKey, secretKey);
//	}

	@Bean
	public S3Client amazonS3Client() throws Exception {

        //先调用下忽略https证书的再请求才可以
//        URL realUrl = new URL(endpoint);
//        if("https".equalsIgnoreCase(realUrl.getProtocol())){
//            SslUtils.ignoreSsl();
//        }

//		ClientConfiguration clientConfig = new ClientConfiguration();
//		clientConfig.setProtocol(Protocol.HTTPS);
//
//		AttributeMap attributeMap =
//
//		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//		AmazonS3 conn= new AmazonS3Client(credentials, clientConfig);
//		conn.setEndpoint(endpoint);

		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
		AttributeMap attributeMap = AttributeMap.builder().put(
				SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true).build();

		S3Client s3Client = S3Client.builder().
				httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
				credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
				endpointOverride(URI.create(endpoint)).
				region(Region.US_EAST_1).
//				region(Region.US_WEST_2).
				build();

		return s3Client;
	}




}
