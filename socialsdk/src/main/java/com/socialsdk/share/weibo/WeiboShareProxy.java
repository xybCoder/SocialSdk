package com.socialsdk.share.weibo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.socialsdk.Config;
import com.socialsdk.Util;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by zhanghailong-ms on 2015/11/24.
 */
public class WeiboShareProxy {
    private static final String TAG = "WeiboShareProxy";
    private static IWeiboShareAPI api;

    public static IWeiboShareAPI getInstance(Context context) {
        LogUtil.enableLog();
        if (null == api) {
            api = WeiboShareSDK.createWeiboAPI(context, Config.WEIBO_APP_KEY);
            api.registerApp();
        }

        return api;
    }

    public static IWeiboShareAPI getInstance() {
        return api;
    }

    private static void shareTo(final Context context, final String redirectUrl, final String scop, final String title, final String desc,
                                final String imageUrl, final String shareUrl, final WeiboAuthListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeiboMultiMessage msg = new WeiboMultiMessage();
                TextObject text = new TextObject();
                text.text = desc;
                msg.textObject = text;
                WebpageObject web = new WebpageObject();
                web.description = desc;
                byte[] thumb = Util.getHtmlByteArray(imageUrl);
                if (null != thumb) {
                    web.thumbData = Util.compressBitmap(thumb, 32);
                } else {
                    web.thumbData = Util.compressBitmap(Util.getDefaultShareImage(context), 32);
                }
                web.actionUrl = shareUrl;
                web.identify = imageUrl;
                web.title = title;
                msg.mediaObject = web;

                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = msg;

                AuthInfo authInfo = new AuthInfo(context, Config.WEIBO_APP_KEY, redirectUrl, scop);
                Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
                String token = "";
                if (accessToken != null) {
                    token = accessToken.getToken();
                }
                getInstance(context).sendRequest((Activity) context, request, authInfo, token, listener);
            }
        }).start();

    }

    public static void shareTo(final Context context, final String redirectUrl, final String title, final String desc,
                               final String imageUrl, final String shareUrl, final WeiboAuthListener listener) {
        if (TextUtils.isEmpty(redirectUrl)) {
            shareTo(context, title, desc, imageUrl, shareUrl, listener);
        } else {
            shareTo(context, redirectUrl, "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write", title, desc, imageUrl, shareUrl, listener);
        }

    }

    private static void shareTo(final Context context, final String title, final String desc,
                                final String imageUrl, final String shareUrl, final WeiboAuthListener listener) {
        shareTo(context, "http://www.sina.com", "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write", title, desc, imageUrl, shareUrl, listener);

    }
}
