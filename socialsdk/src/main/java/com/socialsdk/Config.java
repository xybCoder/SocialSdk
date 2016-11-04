package com.socialsdk;

/**
 *
 * 全局配置信息
 *
 */

public class Config {

    public static boolean debugMode = true;//调试
    public final static String WX_APP_ID = "wx65b32431b8a0be08";//微信APPID
    public final static String WX_SECRET="3439517d98d33292951356fb9934353a";
    public final static String WX_SCOPE= "snsapi_userinfo";//微信授权域
    public final static String WEIBO_APP_KEY = "2514009253";//微博appkey
    public final static String WEIBO_REDIRECTR_URL = "https://api.weibo.com/oauth2/default.html";//微博回调地址
    public final static String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";//微博授权域
    public final static String QQ_APP_ID= "1105768566";
    public final static String QQ_SCOPE = "all";//QQ授权域

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Config.debugMode = debugMode;
    }

    public static String getUrlForWeChatToken() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + WX_APP_ID
                + "&secret="
                + WX_SECRET
                + "&code=%s&grant_type=authorization_code";
    }

    public static String getUrlForWeChatUserInfo() {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
    }

    public static String getUrlForWeChatRefreshToken() {
        return "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="
                + WX_APP_ID
                + "&grant_type=refresh_token&refresh_token=%s";
    }


}
