/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jims.his.service;


import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.error.ServerErrorType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.integration.endpoints.AuthzEndpoint;
import org.apache.oltu.oauth2.integration.utils.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * get access token
 * 
 */
@Path("/token")
public class TokenEndpoint {
	//SqlSession sqlSession = DbUtil.getSessionFactory().openSession(true);
	//AppMapper appDao = sqlSession.getMapper(AppMapper.class);
	//RefreshTokenMapper refreshTokenDao = sqlSession
	//		.getMapper(RefreshTokenMapper.class);
	//UserMapper dao = sqlSession.getMapper(UserMapper.class);
	//AuthorityMapper authorityDao = sqlSession.getMapper(AuthorityMapper.class);

	// 登录页面
	private static String loginPage;

	// 错误页面
	private static String errorPage;

	static {
		Properties p = new Properties();
		try {
			p.load(AuthzEndpoint.class.getClassLoader().getResourceAsStream(
					"config.properties"));
			loginPage = p.getProperty("loginPage");
			errorPage = p.getProperty("errorPage");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static final String INVALID_CLIENT_DESCRIPTION = "Client authentication failed (e.g., unknown client, no client authentication included, or unsupported authentication method).";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public Response authorize(@Context HttpServletRequest request)
			throws OAuthSystemException, URISyntaxException {

		OAuthTokenRequest oauthRequest = null;
		String scope = "";
		OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

		try {
			oauthRequest = new OAuthTokenRequest(request);

			/*
			 * 当前登录的用户，模拟一个从session中获取的登录用户
			 * 该方法未实现，待模块与养老平台整合时，应调用养老平台方法判断用户是否已登录
			 */
			String userId = "1";

			if ("".equals(userId) || userId == null) {
				// 用户没有登录的话就跳转到登录页面
				return Response.temporaryRedirect(new URI(loginPage)).build();
			}

			//App app = null;
			//if (oauthRequest.getClientId() != null && !"".equals(oauthRequest.getClientId())) {
			//	app = appDao.selectByPrimaryKey(oauthRequest.getClientId());
			//} else {
			//	return Response.temporaryRedirect(new URI(errorPage + "?error=" + ServerErrorType.CLIENT_ID_IS_NULL)).build();
			//}

			//// 校验clientid
			//if (app == null || !app.getApp_key().toString().equals(oauthRequest.getClientId())) {
			//	if(oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())){
			//		return Response.temporaryRedirect(new URI(errorPage + "?error=" + ServerErrorType.UNKOWN_CLIENT_ID)).build();
			//	}else{
	         //       OAuthResponse response =
	         //               OAuthASResponse.errorResponse(HttpServletResponse.SC_OK)
	         //                   .setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription(INVALID_CLIENT_DESCRIPTION)
	         //                   .buildJSONMessage();
	         //           return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
			//	}
			//}
            //
			//// 校验client_secret
			//if (!app.getSecret_key().equals(oauthRequest.getClientSecret())) {
			//	if(oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())){
			//		return Response.temporaryRedirect(new URI(errorPage + "?error=" + ServerErrorType.UNKOWN_CLIENT_SECRET)).build();
			//	}else{
	         //       OAuthResponse response =
	         //               OAuthASResponse.errorResponse(HttpServletResponse.SC_OK)
	         //                   .setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription(INVALID_CLIENT_DESCRIPTION)
	         //                   .buildJSONMessage();
	         //           return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
			//	}
			//}

			// 校验不同类型的授权方式
			if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
				String cacheCode = null;
				if (CacheManager.getCacheInfo(userId + "_code").getValue() != null) {
					cacheCode = CacheManager.getCacheInfo(userId + "_code")
							.getValue().toString();
				} else {
					// 用户没有登录的话就跳转到登录页面
					return Response.temporaryRedirect(new URI(loginPage)).build();
				}
				
				if (!cacheCode.equals(oauthRequest.getParam(OAuth.OAUTH_CODE))) {
					return Response.temporaryRedirect(new URI(errorPage+ "?error=" + ServerErrorType.INVALID_AUTHORIZATION_CODE)).build();
				}
				
	            if(CacheManager.getCacheInfo(userId+"_scope").getValue()!=null){
	            	scope = CacheManager.getCacheInfo(userId+"_scope").getValue().toString();
	            }
			} else if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.PASSWORD.toString())) {
				//User user = dao.getById(userId);
				//if (!user.getPassword().equals(oauthRequest.getPassword())|| !user.getName().equals(oauthRequest.getUsername())) {
                 //   OAuthResponse response = OAuthASResponse
                 //           .errorResponse(HttpServletResponse.SC_OK)
                 //           .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                 //           .setErrorDescription("Invalid username or password.")
                 //           .buildJSONMessage();
                 //       return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
				//}
			} else if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(
					GrantType.CLIENT_CREDENTIALS.toString())) {
				// 客户端id以及secret已验证，更多验证规则在这里添加，没有其他验证则程序直接发放令牌
//                OAuthResponse response = OAuthASResponse
//                        .errorResponse(HttpServletResponse.SC_OK)
//                        .setError(OAuthError.TokenResponse.INVALID_GRANT)
//                        .setErrorDescription("invalid client")
//                        .buildJSONMessage();
//                    return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
				
			}else if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(
					GrantType.REFRESH_TOKEN.toString())) {
				// 刷新令牌未实现
			}

			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();
			// 构建响应
			OAuthResponse response = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setRefreshToken(refreshToken)
					.setExpiresIn("3600")
					.buildJSONMessage();

			// 判断是否已经授权----待调整是放在authz部分还是token部分
			Map aQueryParam = new HashMap();
			aQueryParam.put("appKey", oauthRequest.getClientId());
			aQueryParam.put("userId", Integer.valueOf(userId));
			//if (authorityDao.findUnique(aQueryParam) == null) {
			//	Authority authority = new Authority();
			//	authority.setApp_key(oauthRequest.getClientId());
			//	authority.setUser_id(Integer.valueOf(userId));
			//	authorityDao.insert(authority);
			//}

//			String scope = "";
//            if(CacheManager.getCacheInfo(userId+"_scope").getValue()!=null){
//            	scope = CacheManager.getCacheInfo(userId+"_scope").getValue().toString();
//            }
			
			// 存储token,已授权则更新令牌，未授权则新增令牌
			Map rQueryParam = new HashMap();
			rQueryParam.put("appKey", oauthRequest.getClientId());
			rQueryParam.put("userId", Integer.valueOf(userId));
			//if (refreshTokenDao.findUnique(rQueryParam) != null) {
			//	Map map = new HashMap();
			//	map.put("accessToken", accessToken);
			//	map.put("appKey", oauthRequest.getClientId());
			//	map.put("userId", Integer.valueOf(userId));
			//	map.put("createTime", getDate());
			//	map.put("scope", scope);
			//	map.put("authorizationTime", getDate());
			//	refreshTokenDao.updateAccessToken(map);
			//} else {
			//	RefreshToken rt = new RefreshToken();
			//	rt.setApp_key(oauthRequest.getClientId());
			//	rt.setUser_id(Integer.valueOf(userId));
			//	rt.setAccess_token(accessToken);
			//	rt.setRefresh_token(refreshToken);
			//	rt.setCreate_time(getDate());
			//	rt.setAuthorization_time(getDate());
			//	rt.setExpire("3600");
			//	rt.setScope(scope);
			//	rt.setAuthorization_time(getDate());
			//	refreshTokenDao.insert(rt);
			//}

			return Response.status(response.getResponseStatus())
					.entity(response.getBody()).build();

		} catch (OAuthProblemException e) {
			System.out.println(e.getDescription());
			return Response.temporaryRedirect(new URI(errorPage + "?error="+ ServerErrorType.BAD_RQUEST)).build();
		}
	}

	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(System.currentTimeMillis());
	}
}