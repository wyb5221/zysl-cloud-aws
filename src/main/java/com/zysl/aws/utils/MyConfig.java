package com.zysl.aws.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("myprops")
@Setter
@Getter
public class MyConfig {

    private List<Map<String,Object>> listProps = new ArrayList<>();


}
