package com.socialsdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.socialsdk.sso.SSOProxy;

/**
 * 授权登陆工具类
 */

public class SSOUtil {

    /**
     * 一键登录，显示SDK默认登录界面
     *
     * @param context context
     */
    public static void oauth(Context context) {
        if (!TextUtils.isEmpty(Config.WX_APP_ID)
                && !TextUtils.isEmpty(Config.WX_SECRET)
                && !TextUtils.isEmpty(Config.WEIBO_APP_KEY)
                && !TextUtils.isEmpty(Config.QQ_APP_ID)) {
            SSOProxy.login(context);
        } else {
            LogUtil.e(" wechat(qq,weibo) appid is null");
        }
    }

    /**
     * 一键解除微信 qq 微博 授权
     *
     * @param context context
     */
    public static void revoke(Context context) {
        revokeWeibo(context);
        revokeQQ(context);
        revokeWeChat(context);
    }

    /**
     * 授权微信
     *
     * @param context context
     */
    public static void oauthWeChat(Context context) {
        if (!TextUtils.isEmpty(Config.WX_APP_ID) && !TextUtils.isEmpty(Config.WX_SECRET)) {
            SSOProxy.loginWeChat(context);
        } else {
            LogUtil.e(" wechat appid or secret is null");
        }
    }

    /**
     * 移除微信授权
     *
     * @param context context
     */
    public static void revokeWeChat(Context context) {
        SSOProxy.logoutWeChat(context);
    }

    /**
     * 微博授权
     *
     * @param context context
     */
    public static void oauthWeibo(Context context) {
        if (!TextUtils.isEmpty(Config.WEIBO_APP_KEY)) {
            SSOProxy.loginWeibo(context);
        } else {
            LogUtil.e("weibo appid is null");
        }
    }

    /**
     * 移除微博授权
     *
     * @param context context
     */
    public static void revokeWeibo(Context context) {
        SSOProxy.logoutWeibo(context);
    }

    /**
     * QQ授权
     *
     * @param context context
     */
    public static void oauthQQ(Context context) {
        if (!TextUtils.isEmpty(Config.QQ_APP_ID)) {
            SSOProxy.loginQQ(context);
        }else{
            LogUtil.e("qq appid is null");
        }
    }

    /**
     * 移除QQ授权
     *
     * @param context context
     */
    public static void revokeQQ(Context context) {
        SSOProxy.logoutQQ(context);
    }


}
