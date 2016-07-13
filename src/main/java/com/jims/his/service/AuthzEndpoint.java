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
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.error.ServerErrorType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.integration.utils.Cache;
import org.apache.oltu.oauth2.integration.utils.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
 * client request authorization
 * 
 */
@Path("/authz")
public class AuthzEndpoint {

    //登录页面
    private static String loginPage;
    
    //错误页面
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

	@GET
	public Response authorize(@Context HttpServletRequest request)
			throws URISyntaxException, OAuthSystemException {

		OAuthAuthzRequest oauthRequest = null;

		OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(
				new MD5Generator());

		try {
			oauthRequest = new OAuthAuthzRequest(request); 
			
			/*
			 * 当前登录的用户，模拟一个从session中获取的登录用户
			 * 该方法未实现，待模块与养老平台整合时，应调用养老平台方法判断用户是否已登录
			 * 并获得对应用户的userId
			 */
			String userId = "1";

			if ("".equals(userId) || userId == null) {
				// 用户没有登录就跳转到登录页面
				return Response.temporaryRedirect(new URI(loginPage)).build();
			}

			//App app = null;
			//if(oauthRequest.getClientId()!=null && !"".equals(oauthRequest.getClientId())){
			//	app = appDao.selectByPrimaryKey(oauthRequest.getClientId());
			//}else{
			//	return Response.temporaryRedirect(new URI(errorPage+"?error="+ServerErrorType.CLIENT_ID_IS_NULL)).build();
			//}
			
			// 根据response_type创建response
			String responseType = oauthRequest
					.getParam(OAuth.OAUTH_RESPONSE_TYPE);

			OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse
					.authorizationResponse(request,
							HttpServletResponse.SC_FOUND);
			
			// 检查传入的客户端id是否正确
			//if (app == null) {
			//	return Response.temporaryRedirect(new URI(errorPage+"?error="+ServerErrorType.UNKOWN_CLIENT_ID)).build();
			//}

			String scope = oauthRequest.getParam(OAuth.OAUTH_SCOPE);
			
			// 授权请求类型
			if (responseType.equals(ResponseType.CODE.toString())) {
				String code = oauthIssuerImpl.authorizationCode();
				builder.setCode(code);
				CacheManager.putCache(userId+"_code", new Cache("code", code,
						216000000, false));
				CacheManager.putCache(userId+"_scope", new Cache("scope", scope,
						216000000, false));
			}
			if (responseType.equals(ResponseType.TOKEN.toString())) {
				// 校验client_secret
				//if (!app.getSecret_key().equals(oauthRequest.getClientSecret())) {
		         //       OAuthResponse response =
		         //               OAuthASResponse.errorResponse(HttpServletResponse.SC_OK)
		         //                   .setError(OAuthError.TokenResponse.INVALID_CLIENT).setErrorDescription(INVALID_CLIENT_DESCRIPTION)
		         //                   .buildJSONMessage();
		         //           return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
				//}
				//String accessToken = oauthIssuerImpl.accessToken();
				//builder.setAccessToken(accessToken);
				//builder.setExpiresIn(3600l);
                ////判断是否已经授权----待调整是放在authz部分还是token部分
                //Map<String,Object> aQueryParam = new HashMap<>();
                //aQueryParam.put("appKey",oauthRequest.getClientId());
                //aQueryParam.put("userId",Integer.valueOf(userId));
                //if(authorityDao.findUnique(aQueryParam)==null){
	            	//Authority authority = new Authority();
		         //   authority.setApp_key(oauthRequest.getClientId());
		         //   authority.setUser_id(Integer.valueOf(userId));
		         //   authorityDao.insert(authority);
                //}
				//// 存储token,已授权则更新令牌，未授权则新增令牌
				//Map<String,Object> rQueryParam = new HashMap<>();
				//rQueryParam.put("appKey", oauthRequest.getClientId());
				//rQueryParam.put("userId", Integer.valueOf(userId));
				//if (refreshTokenDao.findUnique(rQueryParam) != null) {
				//	Map<String,Object> map = new HashMap<>();
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
				//	rt.setCreate_time(getDate());
				//	rt.setAuthorization_time(getDate());
				//	rt.setExpire("3600");
				//	rt.setScope(scope);
				//	rt.setAuthorization_time(getDate());
				//	refreshTokenDao.insert(rt);
				//}
			}
			
			// 客户端跳转URI
			String redirectURI = oauthRequest
					.getParam(OAuth.OAUTH_REDIRECT_URI);

			final OAuthResponse response = builder.location(redirectURI).setParam("scope", scope)
					.buildQueryMessage();
			String test = response.getLocationUri();
			URI url = new URI(response.getLocationUri());

			return Response.status(response.getResponseStatus()).location(url)
					.build();

		} catch (OAuthProblemException e) {
			return Response.temporaryRedirect(new URI(errorPage+"?error="+ServerErrorType.BAD_RQUEST)).build();
		}
	}
	
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(System.currentTimeMillis());
	}
}
