package com.zysl.cloud.aws.biz.utils;

import com.zysl.cloud.aws.api.dto.DataAuthDTO;
import com.zysl.cloud.aws.api.dto.OPAuthDTO;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.WebUtil;
import com.zysl.cloud.utils.common.AppLogicException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class DataAuthUtils {

	private final String DATA_AUTH_TOKEN = "auth-token";

	//分类间隔符
	private final String CLASS_SEPARATOR = ":";
	//数据组间隔符
	private final String ITEM_SEPARATOR = "_";
	//key-value间隔符
	private final String KV_SEPARATOR = "=";

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
	 * @param objectAuth 用户id=权限列表_用户id=权限列表_/角色ID=权限列表_角色ID=权限列表_/所有人的权限列表
	 * @return java.lang.Boolean
	 **/
	public boolean checkAuth(String opTypes,String objectAuth){
		//用户id/角色ID;角色ID
		String tokenAuth = getUserAuth();

		//没传则不校验
		if(StringUtils.isBlank(tokenAuth)){
			return Boolean.TRUE;
		}
		// 没设置则异常
		if (StringUtils.isBlank(objectAuth)) {
			return Boolean.FALSE;
		}
		//未去重列表，但不影响功能
		List<String> authList = getRealAuths(tokenAuth,objectAuth);

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
	 * 根据提交参数，解析实际权限
	 * @description
	 * @author miaomingming
	 * @date 10:15 2020/3/27
	 * @param tokenAuth 用户id/角色ID_角色ID
	 * @param objectAuth 用户id=权限列表_用户id=权限列表_/角色ID=权限列表_角色ID=权限列表_/所有人的权限列表
	 * @return java.util.List<java.lang.String>
	 **/
	private List<String> getRealAuths(String tokenAuth,String objectAuth){
		List<String> authList = new ArrayList<>();


		try{
			DataAuthDTO dataAuthDTO = parseDataAuths(objectAuth);

			//用户ID校验
			String userId = tokenAuth.substring(0,tokenAuth.indexOf(CLASS_SEPARATOR));
			addAuthToList(authList,userId,dataAuthDTO.getUserAuths());

			//角色ID校验
			String roleIds = tokenAuth.substring(tokenAuth.indexOf(CLASS_SEPARATOR)+1);
			addAuthToList(authList,roleIds,dataAuthDTO.getGroupAuths());

			//所有人权限校验
			addAuthToList(authList,dataAuthDTO.getEveryOneAuths());
		}catch (StringIndexOutOfBoundsException e){
			log.error("parseAuths.format.error:tokenAuth:{},objectAuth:{}",tokenAuth,objectAuth);
			throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_DATA_FORMAT_ERROR.getCode());
		}catch (Exception e){
			log.error("parseAuths.format.error:tokenAuth:{},objectAuth:{}::",tokenAuth,objectAuth,e);
			throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_ERROR.getCode());
		}


		return authList;
	}

	/**
	 * 对比提取权限
	 * @description
	 * @author miaomingming
	 * @date 10:23 2020/3/27
	 * @param authList
	 * @param ob  x1;x2;...
	 * @param dataAuthDTOS  x1:kk;x2:kk;...
	 * @return void
	 **/
	private void addAuthToList(List<String> authList,String ob,List<OPAuthDTO> dataAuthDTOS){
		if(CollectionUtils.isEmpty(dataAuthDTOS)){
			return;
		}
		//对象list
		List<String> obList = new ArrayList<>();
		if(StringUtils.isNotBlank(ob)){
			obList = Arrays.asList(ob.split(ITEM_SEPARATOR));
		}

		for(OPAuthDTO dto : dataAuthDTOS){
			if(obList.contains(dto.getKey())){
				addAuthToList(authList,dto.getValue());
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
	 * 权限数据合并成字符串，在用户提交写入对象属性时调用
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
				if(StringUtils.isNotBlank(o.getValue())){
					sb.append(o.getKey()).append(KV_SEPARATOR)
						.append(o.getValue())
						.append(ITEM_SEPARATOR);
				}
			});
		}
		sb.append(CLASS_SEPARATOR);

		if(!CollectionUtils.isEmpty(roleAuths)){
			roleAuths.forEach(o->{
				if(StringUtils.isNotBlank(o.getValue())){
					sb.append(o.getKey()).append(KV_SEPARATOR)
						.append(o.getValue())
						.append(ITEM_SEPARATOR);
				}
			});
		}
		sb.append(CLASS_SEPARATOR);

		if(StringUtils.isNotBlank(others)){
			sb.append(others);
		}

		return sb.toString();
	}

	/**
	 * 从对象属性读取后解析用
	 * 解析权限设置字符串，格式：  用户=权限列表_...:组=权限列表_...:所有人权限列表
	 * @description
	 * @author miaomingming
	 * @date 16:44 2020/3/31
	 * @param objectAuth
	 * @return com.zysl.cloud.aws.api.dto.DataAuthDTO
	 **/
	public DataAuthDTO parseDataAuths(String objectAuth){
		DataAuthDTO dataAuthDTO = new DataAuthDTO();
		try{
			//用户ID校验
			String userIdAuths = objectAuth.substring(0,objectAuth.indexOf(CLASS_SEPARATOR));
			dataAuthDTO.setUserAuths(parseAuthDTO(userIdAuths));

			//角色ID校验
			String roleIdAuths = objectAuth.substring(objectAuth.indexOf(CLASS_SEPARATOR)+1,objectAuth.lastIndexOf(CLASS_SEPARATOR));
			dataAuthDTO.setGroupAuths(parseAuthDTO(roleIdAuths));

			//所有人权限校验
			String otherAuth = objectAuth.substring(objectAuth.lastIndexOf(CLASS_SEPARATOR)+1);
			dataAuthDTO.setEveryOneAuths(otherAuth);
		}catch (StringIndexOutOfBoundsException e){
			log.error("parseAuths.format.error:objectAuth:{}",objectAuth);
			throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_DATA_FORMAT_ERROR.getCode());
		}catch (Exception e){
			log.error("parseAuths.format.error:objectAuth:{}::",objectAuth,e);
			throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_ERROR.getCode());
		}
		return dataAuthDTO;
	}

	/**
	 * 解析权限设置字符串，格式：  用户=权限列表_用户=权限列表_用户=权限列表_
	 * @description
	 * @author miaomingming
	 * @date 16:36 2020/3/31
	 * @param obAuths
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.OPAuthDTO>
	 **/
	private List<OPAuthDTO> parseAuthDTO(String obAuths){
		//对象-角色列表
		String[] obAuthList = obAuths.split(ITEM_SEPARATOR);
		//对象list
		List<OPAuthDTO> obList = new ArrayList<>();

		for(String obAuth : obAuthList){
			if(StringUtils.isBlank(obAuth)){
				continue;
			}
			String[] obAuthItem = obAuth.split(KV_SEPARATOR);
			obList.add(new OPAuthDTO(obAuthItem[0],obAuthItem[1]));
		}
		return obList;
	}




	public static void main(String[] args){
		DataAuthUtils test = new DataAuthUtils();
		String tokenAuth = "002:g02";
		String objectAuth = "001=r_:g01=rm_:d";


		List<String> list = test.getRealAuths(tokenAuth,objectAuth);

		list.forEach((key)->System.out.println("key=" + key ));

	}
}
