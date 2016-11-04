package com.socialsdk.share.wechat;

import android.content.Context;

import com.socialsdk.Util;
import com.socialsdk.WXapi;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

/**
 * 微信分享Proxy
 */
public class WeChatShareProxy {

    private static IWXShareCallback mCallback;

    public static void shareToWeChat(final Context context, final String title, final String desc,
                                     final String url, final String thumbnail, final IWXShareCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeChatShareProxy.mCallback = callback;
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = desc;
                byte[] thumb = Util.getHtmlByteArray(thumbnail);
                if (null != thumb) {
                    msg.thumbData = Util.compressBitmap(thumb, 32);
                } else {
                    msg.thumbData = Util.compressBitmap(Util.getDefaultShareImage(context), 32);
                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = Util.buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                WXapi.getIWXAPIInstance(context).sendReq(req);
            }
        }).start();

    }

    public static void shareToWeChatTimeline(final Context context, final String title, final String url,
                                             final String thumbnail, final IWXShareCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeChatShareProxy.mCallback = callback;
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                byte[] thumb = Util.getHtmlByteArray(thumbnail);
                if (null != thumb) {
                    msg.thumbData = Util.compressBitmap(thumb, 32);
                } else {
                    msg.thumbData = Util.compressBitmap(Util.getDefaultShareImage(context), 32);
                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = Util.buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                WXapi.getIWXAPIInstance(context).sendReq(req);
            }
        }).start();

    }

    public static void shareComplete(SendMessageToWX.Resp resp) {
        if (null != mCallback) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    mCallback.onSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    mCallback.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                default:
                    mCallback.onFailure(new Exception("BaseResp.ErrCode.ERR_AUTH_DENIED"));
                    break;
            }
        }
    }
}
