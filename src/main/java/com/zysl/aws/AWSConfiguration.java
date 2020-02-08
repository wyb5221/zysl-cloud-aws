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
import java.time.Duration;

@Configuration
public class AWSConfiguration {

	@Value("${cloud.aws.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.endpoint}")
	private String endpoint;

	@Bean
	public S3Client amazonS3Client() throws Exception {

		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
		AttributeMap attributeMap = AttributeMap.builder()
				.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
				.put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))
				.put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))
				.put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))
				.put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))
				.build();

		S3Client s3Client = S3Client.builder().
				httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
				credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
				endpointOverride(URI.create(endpoint)).
				region(Region.US_EAST_1).
				build();

		return s3Client;
	}




}
