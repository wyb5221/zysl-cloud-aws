package com.zysl.cloud.aws.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwk.Jwk;
import io.jsonwebtoken.*;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;

@Service
public class TokenTest {

    @Autowired
    private RestTemplate restTemplate;

    public boolean appleAuth(String jwt) {

        //  向苹果后台获取公钥参数
        String appleResp = null;
        try {
            appleResp = restTemplate.getForObject("https://appleid.apple.com/auth/keys", String.class);

        }catch (Exception e){

        }
        JSONObject appleRespJson = JSONObject.parseObject(appleResp);
        String keys = appleRespJson.getString("keys");
        JSONArray keysArr = JSONObject.parseArray(keys);

        JSONObject jsonObject = JSONObject.parseObject(keysArr.getString(0));

        //  通过jar生成publicKey
        PublicKey publicKey = null;
        try {
            Jwk jwa = Jwk.fromValues(jsonObject);
            publicKey = jwa.getPublicKey();

        }catch (Exception e){

        }
        return verify(publicKey, jwt);
    }


    private boolean verify(PublicKey key, String jwt) {
        //  分割前台传过来的identifyToken（jwt格式的token）用base64解码使用
        String aud = "";
        String sub = "";
        try {
            String claim = new String(Base64.decodeBase64(jwt.split("\\.")[1]));
            //logger.info("checkIdentifyToken-claim:{}", claim);
            aud = JSONObject.parseObject(claim).get("aud").toString();
            sub = JSONObject.parseObject(claim).get("sub").toString();
            //  appleUserId从token中解码取出后赋值
        } catch (Exception e) {
//            throw new PicaException("checkIdentifyToken-token decode fail Exception", "token decode fail");
        }

        JwtParser jwtParser = Jwts.parser().setSigningKey(key);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);
        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                JSONObject claimBody = JSONObject.parseObject(JSON.toJSONString(claim.getBody()), JSONObject.class);
                return true;
            }
            return false;
        } catch (ExpiredJwtException e) {
//            throw new PicaException("apple token expired Exception", "apple token expired");
        } catch (Exception e) {
//            throw new PicaException("apple token illegal Exception", "apple token illegal");
        }
        return false;
    }


}
