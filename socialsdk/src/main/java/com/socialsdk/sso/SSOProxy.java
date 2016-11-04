package com.socialsdk.sso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.socialsdk.SSOUtil;
import com.socialsdk.Config;
import com.socialsdk.R;
import com.socialsdk.bean.SocialToken;
import com.socialsdk.bean.SocialUser;
import com.socialsdk.event.SSOEvent;
import com.socialsdk.sso.qq.QQSSOProxy;
import com.socialsdk.sso.wechat.IWXCallback;
import com.socialsdk.sso.wechat.WeChatSSOProxy;
import com.socialsdk.sso.weibo.User;
import com.socialsdk.sso.weibo.WeiboSSOProxy;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 社交授权proxy
 */
public class SSOProxy {

    private static final String TAG = "SSOProxy";
    private static boolean DEBUG = Config.debugMode;

    private static SocialUser user;

    public static void setUser(Context context, SocialUser user) {
        SSOProxy.user = user;
        SocialUserKeeper.writeSocialUser(context, user);
    }

    public static SocialUser getUser(Context context) {
        if (user == null || TextUtils.isEmpty(user.getToken().getOpenId())) {
            user = SocialUserKeeper.readSocialUser(context);
        }
        return user;
    }

    public static void removeUser(Context context) {
        SocialUserKeeper.clear(context);
        user = null;
    }

    /**
     * 判断token是否过期
     *
     * @param context context
     * @return 是否过期
     */
    public static boolean isTokenValid(Context context) {
        return getUser(context).isTokenValid();
    }

    /**
     * 登录微博
     *
     * @param context context
     */
    public static void loginWeibo(final Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.loginWeibo");
        }
        WeiboSSOProxy.login(context, new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeibo#login onComplete");
                }
                final String token = bundle.getString("access_token");
                final String expiresIn = bundle.getString("expires_in", "0");
                final String code = bundle.getString("code");
                final String openId = bundle.getString("uid");
                final SocialToken socialToken = new SocialToken(openId, token, "", Long.valueOf(expiresIn));
                if (DEBUG) {
                    Log.i(TAG, "social token info: code=" + code + ", token=" + socialToken.toString());
                }
                getUser(context).setToken(socialToken);
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_TOKEN, SSOEvent.PLATFORM_WEIBO, socialToken));
                WeiboSSOProxy.getUserInfo(context, socialToken, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        if (DEBUG) {
                            Log.i(TAG, "SSOProxy.loginWeibo#getUserInfo onComplete, \n\r" + s);
                        }
                        User user = User.parse(s);
                        if (user == null) {
                            EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_WEIBO, new Exception("Sina user parse error.")));
                            return;
                        }
                        int gender = SocialUser.GENDER_UNKNOWN;
                        if ("f".equals(user.gender)) {
                            gender = SocialUser.GENDER_FEMALE;
                        } else if ("m".equals(user.gender)) {
                            gender = SocialUser.GENDER_MALE;
                        }
                        SocialUser socialUser = new SocialUser(SocialUser.TYPE_WEIBO,
                                user.name, user.profile_image_url, gender, user.description, socialToken);
                        if (DEBUG) {
                            Log.i(TAG, socialUser.toString());
                        }
                        setUser(context, socialUser);
                        EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_USER, SSOEvent.PLATFORM_WEIBO, socialUser));
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        if (DEBUG) {
                            Log.i(TAG, "SSOProxy.loginWeibo#getUserInfo onWeiboException, e=" + e.toString());
                        }
                        EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_WEIBO, e));
                    }
                });
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeibo#login onWeiboException, e=" + e.toString());
                }
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_WEIBO, e));
            }

            @Override
            public void onCancel() {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeibo#login onCancel");
                }
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_CANCEL, SSOEvent.PLATFORM_WEIBO));
            }
        });
    }

    /**
     * 微博授权取消
     *
     * @param context context
     */
    public static void logoutWeibo(final Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.logoutWeibo");
        }

        WeiboSSOProxy.logout(context, getUser(context).getToken(), new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.logoutWeibo#onComplete, s=" + s);
                }
                removeUser(context);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.logoutWeibo#onWeiboException, e=" + e.toString());
                }
            }
        });
    }

    /**
     * 微博登录状态回调接口
     *
     * @param context     context
     * @param requestCode request
     * @param resultCode  result
     * @param data        data
     */
    public static void loginWeiboCallback(Context context, int requestCode, int resultCode, Intent data) {
        if (WeiboSSOProxy.getSsoHandler(context) != null) {
            WeiboSSOProxy.getSsoHandler(context).authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 登录微信
     *
     * @param context context
     */
    public static void loginWeChat(final Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.loginWeChat");
        }
        WeChatSSOProxy.login(context, new IWXCallback() {
            @Override
            public void onGetCodeSuccess(String code) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeChat onGetCodeSuccess, code=" + code);
                }
                WeChatSSOProxy.getToken(code, Config.getUrlForWeChatToken());
            }

            @Override
            public void onGetTokenSuccess(SocialToken token) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeChat onGetCodeSuccess, token=" + token.toString());
                }
                getUser(context).setToken(token);
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_TOKEN, SSOEvent.PLATFORM_WECHAT, token));
                WeChatSSOProxy.getUserInfo(context, Config.getUrlForWeChatUserInfo(), token);
            }

            @Override
            public void onGetUserInfoSuccess(SocialUser user) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeChat onGetUserSuccess, user=" + user.toString());
                }
                setUser(context, user);
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_USER, SSOEvent.PLATFORM_WECHAT, user));
            }

            @Override
            public void onFailure(Exception e) {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeChat onFailure");
                }
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_WECHAT, e));
            }

            @Override
            public void onCancel() {
                if (DEBUG) {
                    Log.i(TAG, "SSOProxy.loginWeChat onCancel");
                }
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_CANCEL, SSOEvent.PLATFORM_WECHAT));
            }
        });
    }


    /**
     * 登出微信
     *
     * @param context context
     */
    public static void logoutWeChat(Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.logoutWeChat");
        }
        removeUser(context);
    }

    private static Context context;
    private static IUiListener qqLoginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            if (DEBUG)
                Log.i(TAG, "SSOProxy.loginQQ onComplete, info=" + o.toString());
            try {
                JSONObject info = new JSONObject(o.toString());
                final String openId = info.getString("openid");
                final String token = info.getString("access_token");
                final long expiresIn = info.getLong("expires_in");
                final SocialToken socialToken = new SocialToken(openId, token, "", expiresIn);
                getUser(context).setToken(socialToken);
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_TOKEN, SSOEvent.PLATFORM_QQ, socialToken));
                QQSSOProxy.getUserInfo(context, Config.QQ_APP_ID, socialToken, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        if (DEBUG)
                            Log.i(TAG, "SSOProxy.loginQQ#getToken onComplete info=" + o.toString());
                        try {
                            JSONObject user = new JSONObject(o.toString());
                            String name = user.getString("nickname");
                            String iconUrl = user.getString("figureurl_qq_2").replace("\\", "");
                            int gender = SocialUser.GENDER_UNKNOWN;
                            if ("女".equals(user.getString("gender")))
                                gender = SocialUser.GENDER_FEMALE;
                            else if ("男".equals(user.getString("gender")))
                                gender = SocialUser.GENDER_MALE;
                            SocialUser socialUser = new SocialUser(SocialUser.TYPE_QQ,
                                    name, iconUrl, gender, socialToken);
                            if (DEBUG)
                                Log.i(TAG, "SSOProxy.loginQQ#getToken onComplete user=" + socialUser.toString());
                            setUser(context, socialUser);
                            EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_GET_USER, SSOEvent.PLATFORM_QQ, socialUser));
                        } catch (JSONException e) {
                            EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_QQ, e));
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        if (DEBUG)
                            Log.i(TAG, "SSOProxy.loginQQ#getToken onError errorCode=" + uiError.errorCode
                                    + ", errorMsg=" + uiError.errorMessage + ", errorDetail=" + uiError.errorDetail);
                        EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_QQ,
                                new Exception(uiError.errorCode + "#" + uiError.errorMessage + "#" + uiError.errorDetail)));
                    }

                    @Override
                    public void onCancel() {
                        if (DEBUG)
                            Log.i(TAG, "SSOProxy.loginQQ#getToken onCancel");
                        EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_CANCEL, SSOEvent.PLATFORM_QQ));
                    }
                });
            } catch (JSONException e) {
                EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_QQ, e));
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (DEBUG)
                Log.i(TAG, "SSOProxy.loginQQ onError");
            EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_FAILURE, SSOEvent.PLATFORM_QQ,
                    new Exception(uiError.errorCode + "#" + uiError.errorMessage + "#" + uiError.errorDetail)));
        }

        @Override
        public void onCancel() {
            if (DEBUG)
                Log.i(TAG, "SSOProxy.loginQQ onCancel");
            EventBus.getDefault().post(new SSOEvent(SSOEvent.TYPE_CANCEL, SSOEvent.PLATFORM_QQ));
        }
    };

    /**
     * 登录QQ
     *
     * @param context context
     */
    public static void loginQQ(Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.loginQQ");
        }
        SSOProxy.context = context;
        QQSSOProxy.login(context, qqLoginListener);
    }

    /**
     * 登出QQ
     *
     * @param context context
     */
    public static void logoutQQ(Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.logoutQQ");
        }
        QQSSOProxy.logout(context);
        removeUser(context);
    }

    /**
     * QQ登录状态回调
     *
     * @param requestCode request
     * @param resultCode  result
     * @param data        data
     */
    public static void loginQQCallback(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
    }

    public static void login(Context context) {
        if (DEBUG) {
            Log.i(TAG, "SSOProxy.login");
        }
        Intent intent = new Intent();
        intent.setClass(context, SSOActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.snack_in, 0);
    }
}
