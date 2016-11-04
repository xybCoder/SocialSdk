package com.socialsdk.sso.weibo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.socialsdk.SSOUtil;
import com.socialsdk.Config;
import com.socialsdk.bean.SocialToken;
import com.socialsdk.sso.SSOProxy;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.net.RequestListener;

/**
 * 微博授权proxy
 */
public class WeiboSSOProxy {

    private static final boolean DEBUG = Config.debugMode;
    private static final String TAG = "WeiboSSOProxy";

    private static SsoHandler ssoHandler;
    private static AuthInfo authInfo;

    public static SsoHandler getSsoHandler(Context context) {
        if (ssoHandler == null) {
            ssoHandler = new SsoHandler((Activity) context, getAuthInfo(context, Config.WEIBO_APP_KEY, Config.WEIBO_REDIRECTR_URL, Config.WEIBO_SCOPE));
        }
        return ssoHandler;
    }

    private static AuthInfo getAuthInfo(Context context, String key, String redirectUrl, String scope) {
        if (authInfo == null || !key.equals(authInfo.getAppKey())) {
            authInfo = new AuthInfo(context, key, redirectUrl, scope);
        }
        return authInfo;
    }

    public static void login(Context context, WeiboAuthListener listener) {
        if (!SSOProxy.isTokenValid(context)) {
            getSsoHandler(context).authorize(listener);
        }
    }

    public static void logout(Context context, SocialToken token, RequestListener listener) {
        if (SSOProxy.isTokenValid(context)) {
            LogoutAPI logout = new LogoutAPI(context, Config.WEIBO_APP_KEY, parseToken(token));
            logout.logout(listener);
        }

        ssoHandler = null;
        authInfo = null;
    }

    public static void getUserInfo(Context context, SocialToken token, RequestListener listener) {
        if (DEBUG) {
            Log.i(TAG, "getUserInfo");
        }
        if (SSOProxy.isTokenValid(context)) {
            if (DEBUG) {
                Log.i(TAG, "getUserInfo#isTokenValid true");
            }
            UsersAPI usersAPI = new UsersAPI(context, Config.WEIBO_APP_KEY, parseToken(token));
            usersAPI.show(Long.parseLong(token.getOpenId()), listener);
        } else {
            if (DEBUG) {
                Log.i(TAG, "getUserInfo#isTokenValid false");
            }
        }
    }

    private static Oauth2AccessToken parseToken(SocialToken socialToken) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setUid(socialToken.getOpenId());
        token.setExpiresTime(socialToken.getExpiresTime());
        token.setToken(socialToken.getToken());
        return token;
    }
}
