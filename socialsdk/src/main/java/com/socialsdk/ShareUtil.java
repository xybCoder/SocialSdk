package com.socialsdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.socialsdk.bean.ShareScene;
import com.socialsdk.share.ShareProxy;

/**
 * 分享工具类
 */

public class ShareUtil {


    /**
     * 一键分享，默认UI
     *
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void share(Context context, ShareScene scene) {
        if (!TextUtils.isEmpty(Config.WX_APP_ID)
                && !TextUtils.isEmpty(Config.WX_SECRET)
                && !TextUtils.isEmpty(Config.WEIBO_APP_KEY)
                && !TextUtils.isEmpty(Config.QQ_APP_ID)) {
            ShareProxy.share(context, scene);
        }else{
            LogUtil.e(" wechat(qq,weibo) appid is null");
        }
    }

    /**
     * 分享到微信
     *
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void shareToWeChat(Context context, ShareScene scene) {
        if(!TextUtils.isEmpty(Config.WX_APP_ID)) {
            ShareProxy.shareToWeChat(context, scene);
        }else{
            LogUtil.e("wechat appid is null");
        }
    }

    /**
     * 分享到微信朋友圈
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void shareToWeChatTimeline(Context context, ShareScene scene) {
        if(!TextUtils.isEmpty(Config.WX_APP_ID)) {
            ShareProxy.shareToWeChatTimeline(context, scene);
        }else{
            LogUtil.e("wechat appid is null");
        }
    }

    /**
     * 分享到微博
     *
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void shareToWeibo(Context context,  ShareScene scene) {
        if(!TextUtils.isEmpty(Config.WEIBO_APP_KEY)) {
            ShareProxy.shareToWeibo(context, "", scene);
        }else{
            LogUtil.e("weibo appid is null");
        }
    }

    /**
     * 分享到QQ
     *
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void shareToQQ(Context context,ShareScene scene) {
        if(!TextUtils.isEmpty(Config.QQ_APP_ID)) {
            ShareProxy.shareToQQ(context, scene);
        }else{
            LogUtil.e("qq appid is null");
        }
    }

    /**
     * 分享到QQ空间
     *
     * @param context context
     * @param scene   社会化分享数据
     */
    public static void shareToQZone(Context context,  ShareScene scene) {
        if(!TextUtils.isEmpty(Config.QQ_APP_ID)) {
            ShareProxy.shareToQZone(context, scene);
        }else{
            LogUtil.e("qq appid is null");
        }
    }
}
