package com.zysl.cloud.aws.web;

import com.zysl.cloud.aws.biz.service.impl.TokenTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenServiceTest {

    @Autowired
    private TokenTest tokenTest;

    @Test
    public void test(){
        String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJjbl9uYW1lIjpudWxsLCJ1c2VyX2lkIjpudWxsLCJ1c2VyX25hbWUiOm51bGwsInNjb3BlIjpudWxsLCJleHAiOjE1ODMxNzIyMzYsImNsaWVudF9pZCI6IjEwMDEiLCJqdGkiOiJBMEIxQzJEM0U0RjVHNkg3SThKOUtBTEJNQ05ET0VQRlEwUjFTMlQzVTRWNVc2WDdZOFo5IiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iXX0.e6vJPF_Bx5G7M8zQ4qLM0YwWwNvaRjD6CKzjYkEctorBA7zJvT9OXO9y6M5DxCJjAzoFaM-t7iG2sbYQtIQkNA";
        tokenTest.appleAuth(jwt);
    }
}
