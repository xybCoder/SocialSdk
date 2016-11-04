package com.socialsdk;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * IWXAPI需要在SSO授权和分享同时用到
 */
public class WXapi {

    private static IWXAPI api;

    public static IWXAPI getIWXAPIInstance(Context context) {
        if(TextUtils.isEmpty(Config.WX_APP_ID)){
            LogUtil.e("wechat appid is null");
        }
        if (null == api) {
            api = WXAPIFactory.createWXAPI(context,Config.WX_APP_ID, true);
            api.registerApp(Config.WX_APP_ID);
        }

        return api;
    }

    public static IWXAPI getIWXAPIInstance() {
        return api;
    }
}
