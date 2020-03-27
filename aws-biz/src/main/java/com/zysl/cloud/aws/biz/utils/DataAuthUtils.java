package com.zysl.cloud.aws.biz.utils;

import com.zysl.cloud.aws.api.dto.OPAuthDTO;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.WebUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class DataAuthUtils {

	private final String DATA_AUTH_TOKEN = "auth-token";

	@Autowired
	private WebUtil webUtil;


	/**
	 * 查询数据验证带上类的token--后续要改成jwt解析
	 * @description
	 * @author miaomingming
	 * @date 9:51 2020/3/27
	 * @param
	 * @return java.lang.String
	 **/
	public String getUserAuth(){
		return  webUtil.getHeaderValue(DATA_AUTH_TOKEN);
	}

	/**
	 * 权限校验
	 * @description
	 * @author miaomingming
	 * @date 9:53 2020/3/27
	 * @param opTypes com.zysl.cloud.aws.api.enums.OPAuthTypeEnum
	 * @param objectAuth 用户id:权限列表;用户id:权限列表;#角色ID:权限列表;角色ID:权限列表;#所有人的权限列表
	 * @return java.lang.Boolean
	 **/
	public boolean checkAuth(String opTypes,String objectAuth){
		//用户id#角色ID;角色ID
		String tokenAuth = getUserAuth();

		//没传则不校验
		if(StringUtils.isNotBlank(tokenAuth)){
			return Boolean.TRUE;
		}
		// 没设置则异常
		if (StringUtils.isNotBlank(objectAuth)) {
			return Boolean.FALSE;
		}
		//未去重列表，但不影响功能
		List<String> authList = parseAuths(tokenAuth,objectAuth);

		if(authList.contains(OPAuthTypeEnum.ALL.getCode())){
			return Boolean.TRUE;
		}

		for(char key:opTypes.toCharArray()){
			if(!authList.contains(String.valueOf(key))){
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * 权限解析
	 * @description
	 * @author miaomingming
	 * @date 10:15 2020/3/27
	 * @param tokenAuth 用户id#角色ID;角色ID
	 * @param objectAuth 用户id:权限列表;用户id:权限列表;#角色ID:权限列表;角色ID:权限列表;#所有人的权限列表
	 * @return java.util.List<java.lang.String>
	 **/
	private List<String> parseAuths(String tokenAuth,String objectAuth){
		List<String> authList = new ArrayList<>();

		//格式校验:一个#
		if(tokenAuth.indexOf("#") != tokenAuth.lastIndexOf("#")){
			log.warn("tokenAuth.format.error:{}",tokenAuth);
			return authList;
		}
		//格式校验:二个#
		if(objectAuth.length() - objectAuth.replaceAll("#","").length() != 2){
			log.warn("objectAuth.format.error:{}",objectAuth);
			return authList;
		}

		//用户ID校验
		String userId = tokenAuth.substring(0,tokenAuth.indexOf("#"));
		String userIdAuths = objectAuth.substring(0,objectAuth.indexOf("#"));
		addAuthToList(authList,userId,userIdAuths);

		//角色ID校验
		String roleIds = tokenAuth.substring(tokenAuth.indexOf("#")+1);
		String roleIdAuths = objectAuth.substring(objectAuth.indexOf("#")+1,objectAuth.lastIndexOf("#"));
		addAuthToList(authList,roleIds,roleIdAuths);

		//所有人权限校验
		String otherAuth = objectAuth.substring(objectAuth.lastIndexOf("#")+1);
		addAuthToList(authList,otherAuth);

		return authList;
	}

	/**
	 * 对比提取权限
	 * @description
	 * @author miaomingming
	 * @date 10:23 2020/3/27
	 * @param authList
	 * @param ob  x1;x2;...
	 * @param obAuths  x1:kk;x2:kk;...
	 * @return void
	 **/
	private void addAuthToList(List<String> authList,String ob,String obAuths){
		if(StringUtils.isBlank(obAuths)){
			return;
		}
		//对象-角色列表
		String[] obAuthList = obAuths.split(";");
		//对象list
		List<String> obList = new ArrayList<>();
		if(StringUtils.isNotBlank(ob)){
			obList = Arrays.asList(ob.split(";"));
		}

		for(String obAuth : obAuthList){
			if(StringUtils.isBlank(obAuth)){
				continue;
			}
			String[] obAuthItem = obAuth.split(":");
			if(obList.contains(obAuthItem[0])){
				addAuthToList(authList,obAuthItem[1]);
			}
		}

	}

	/**
	 * 将权限列表添加到map
	 * @description
	 * @author miaomingming
	 * @date 11:06 2020/3/27
	 * @param authList
	 * @param charList
	 * @return void
	 **/
	private void addAuthToList(List<String> authList,String charList){
		if(StringUtils.isNotBlank(charList)){
			for(char key:charList.toCharArray()){
				authList.add(String.valueOf(key));
			}
		}
	}

	/**
	 * 
	 * @description 
	 * @author miaomingming
	 * @date 10:38 2020/3/27 
	 * @param userAuths
	 * @param roleAuths
	 * @param others
	 * @return java.lang.String
	 **/
	public String contactAuths(List<OPAuthDTO> userAuths,List<OPAuthDTO> roleAuths,String others){
		StringBuffer sb = new StringBuffer(256);
		if(!CollectionUtils.isEmpty(userAuths)){
			userAuths.forEach(o->{
				if(StringUtils.isNotBlank(o.getValues())){
					sb.append(o.getKey()).append(":")
						.append(o.getValues())
						.append(";");
				}
			});
		}
		sb.append("#");

		if(!CollectionUtils.isEmpty(roleAuths)){
			roleAuths.forEach(o->{
				if(StringUtils.isNotBlank(o.getValues())){
					sb.append(o.getKey()).append(":")
						.append(o.getValues())
						.append(";");
				}
			});
		}
		sb.append("#");

		if(StringUtils.isNotBlank(others)){
			sb.append(others);
		}

		return sb.toString();
	}




	public static void main(String[] args){
		DataAuthUtils test = new DataAuthUtils();
		String tokenAuth = "1;#2;3;";
		String objectAuth = "1:rm;2:d;#4:k#t";


		List<String> list = test.parseAuths(tokenAuth,objectAuth);

		list.forEach((key)->System.out.println("key=" + key ));

	}
}
