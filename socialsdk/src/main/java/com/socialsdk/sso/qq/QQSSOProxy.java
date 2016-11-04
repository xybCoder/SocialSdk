package com.socialsdk.sso.qq;

import android.app.Activity;
import android.content.Context;

import com.socialsdk.Config;
import com.socialsdk.bean.SocialToken;
import com.socialsdk.sso.SSOProxy;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * QQ授权proxy
 */
public class QQSSOProxy {

    private static Tencent tencent;

    public static Tencent getTencentInstance(Context context) {
        if (tencent == null) {
            tencent = Tencent.createInstance(Config.QQ_APP_ID, context);
        }
        return tencent;
    }

    public static void login(Context context,IUiListener listener) {
        Tencent tencent = getTencentInstance(context);
        if (!SSOProxy.isTokenValid(context)) {
            tencent.login((Activity) context, Config.QQ_SCOPE, listener);
        }
    }

    public static void logout(Context context ){
        Tencent tencent = getTencentInstance(context);
        if (SSOProxy.isTokenValid(context)) {
            tencent.logout(context);
        }
        QQSSOProxy.tencent = null;
    }

    public static void getUserInfo(Context context, String appId, SocialToken token, IUiListener listener) {
        getTencentInstance(context);
        if (SSOProxy.isTokenValid(context)) {
            UserInfo info = new UserInfo(context, parseToken(appId, token));
            info.getUserInfo(listener);
        }
    }

    private static QQToken parseToken(String appId, SocialToken socialToken) {
        QQToken token = new QQToken(appId);
        //3600是随意定义的，不影响token的使用
        token.setAccessToken(socialToken.getToken(), "3600");
        token.setOpenId(socialToken.getOpenId());
        return token;
    }
}
