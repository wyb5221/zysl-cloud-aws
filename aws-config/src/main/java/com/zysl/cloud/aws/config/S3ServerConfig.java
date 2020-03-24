package com.zysl.cloud.aws.config;

import com.zysl.cloud.aws.prop.S3ServerProp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@Component
@ConfigurationProperties(prefix = "aws.s3")
public class S3ServerConfig {

	private List<S3ServerProp> servers;
}
