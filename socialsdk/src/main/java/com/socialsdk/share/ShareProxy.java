package com.socialsdk.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.socialsdk.Config;
import com.socialsdk.event.ShareEvent;
import com.socialsdk.bean.ShareScene;
import com.socialsdk.share.qq.QQShareProxy;
import com.socialsdk.share.wechat.IWXShareCallback;
import com.socialsdk.share.wechat.WeChatShareProxy;
import com.socialsdk.share.weibo.AccessTokenKeeper;
import com.socialsdk.share.weibo.WeiboShareProxy;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;

/**
 * 社会化分享代理
 * Created by zhanghailong-ms on 2015/11/23.
 */
public class ShareProxy {

    private static final String TAG = "ShareProxy";
    private static boolean DEBUG = Config.debugMode;
    private static ShareScene scene;

    /**
     * 默认一键分享
     * @param context
     * @param scene
     */
    public static void share(Context context, ShareScene scene) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("scene", scene);
        intent.putExtras(bundle);
        intent.setClass(context, ShareActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(com.socialsdk.R.anim.snack_in, 0);
    }

    /**
     * 分享到微信
     *
     * @param context context
     * @param scene   场景
     */
    public static void shareToWeChat(final Context context, final ShareScene scene) {
        if (DEBUG) {
            Log.i(TAG, "ShareProxy#shareToWeChat");
        }
        ShareProxy.scene = scene;
        WeChatShareProxy.shareToWeChat(context, scene.getTitle(), scene.getDesc(), scene.getUrl(),
                scene.getThumbnail(), wechatShareCallback);
    }

    /**
     * 分享到微信朋友圈
     *
     * @param context context
     * @param scene   场景
     */
    public static void shareToWeChatTimeline(Context context, final ShareScene scene) {
        if (DEBUG) {
            Log.i(TAG, "ShareProxy#shareToWeChatTimeline");
        }
        ShareProxy.scene = scene;
        WeChatShareProxy.shareToWeChatTimeline(context, scene.getTitle(), scene.getUrl(),
                scene.getThumbnail(), wechatShareCallback);
    }

    private static IWXShareCallback wechatShareCallback = new IWXShareCallback() {
        @Override
        public void onSuccess() {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#wechatShareCallback onSuccess");
            }
            EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_SUCCESS, scene.getType(), scene.getId()));
        }

        @Override
        public void onCancel() {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#wechatShareCallback onCancel");
            }
            EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_CANCEL, scene.getType()));
        }

        @Override
        public void onFailure(Exception e) {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#wechatShareCallback onFailure");
            }
            EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_FAILURE, scene.getType(), e));
        }
    };

    /**
     * 分享到微博
     *
     * @param context     context
     * @param redirectUrl 回调地址
     * @param scene       场景
     */
    public static void shareToWeibo(final Context context,String redirectUrl, final ShareScene scene) {
        if (DEBUG) {
            Log.i(TAG, "ShareProxy#shareToWeibo");
        }
        WeiboShareProxy.shareTo(context, redirectUrl, scene.getTitle(), scene.getDesc(),
                scene.getThumbnail(), scene.getUrl(), new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        if (DEBUG) {
                            Log.i(TAG, "ShareProxy#shareToWeibo onComplete");
                        }
                        Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
                        if (token.isSessionValid()) {
                            AccessTokenKeeper.writeAccessToken(context, token);
                        }
                        EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_SUCCESS, scene.getType(), scene.getId()));
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        if (DEBUG) {
                            Log.i(TAG, "ShareProxy#shareToWeibo onWeiboException " + e.toString());
                        }
                      EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_FAILURE, scene.getType(), e));
                    }

                    @Override
                    public void onCancel() {
                        if (DEBUG) {
                            Log.i(TAG, "ShareProxy#shareToWeibo onCancel");
                        }
                     EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_CANCEL, scene.getType()));
                    }
                });

    }


    private static IUiListener qShareListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#qShareListener onComplete");
            }
            if (scene == null) {
                EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_SUCCESS, 0, -1));
            } else {
                EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_SUCCESS, scene.getType(), scene.getId()));
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#qShareListener onError :" + uiError.errorCode + " "
                        + uiError.errorMessage + " " + uiError.errorDetail);
            }
            EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_FAILURE, scene.getType(), new Exception(uiError.errorCode + " "
                    + uiError.errorMessage + " " + uiError.errorDetail)));
        }

        @Override
        public void onCancel() {
            if (DEBUG) {
                Log.i(TAG, "ShareProxy#qShareListener onCancel");
            }
            EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_CANCEL, scene.getType()));
        }
    };

    /**
     * 分享到QQ
     *
     * @param context context
     * @param scene   场景
     */
    public static void shareToQQ(Context context, ShareScene scene) {
        ShareProxy.scene = scene;
        QQShareProxy.shareToQQ(context, scene.getTitle(), scene.getDesc(), scene.getUrl(),
                scene.getThumbnail(), scene.getAppName(), qShareListener);
    }


    /**
     * 分享到QQ空间
     *
     * @param context context
     * @param scene   场景
     */
    public static void shareToQZone(Context context, ShareScene scene) {
        ShareProxy.scene = scene;
        QQShareProxy.shareToQZone(context, scene.getTitle(), scene.getDesc(), scene.getUrl(),
                scene.getThumbnail(), qShareListener);
    }

    /**
     * 分享到QQ，QQ空间结果回调
     *
     * @param requestCode request
     * @param resultCode  result
     * @param data        data
     */
    public static void shareToQCallback(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qShareListener);
    }

    /**
     * 分享到微博结果回调
     *
     * @param intent   intent
     * @param response response
     */
    public static void shareToWeiboCallback(Intent intent, IWeiboHandler.Response response) {
        WeiboShareProxy.getInstance().handleWeiboResponse(intent, response);
    }
}
