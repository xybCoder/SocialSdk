package com.socialsdk.sso.wechat;

import android.content.Context;

import com.socialsdk.Config;
import com.socialsdk.WXapi;
import com.socialsdk.sso.SSOProxy;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
/**
 * 根据自身项目需求定制微信代理
 */

public class CustomWeChatSSoProxy {

    private static CustomWXCallback callback;

    public static void login(Context context, CustomWXCallback callback) {
        if (!SSOProxy.isTokenValid(context)) {
            CustomWeChatSSoProxy.callback = callback;
            SendAuth.Req req = new SendAuth.Req();
            req.scope = Config.WX_SCOPE;
            req.state = "wechat_sdk_test";
            WXapi.getIWXAPIInstance(context).sendReq(req);
        }
    }

    public static void authComplete(SendAuth.Resp resp) {

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                callback.onGetCodeSuccess(resp.code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                callback.onCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                callback.onFailure(new Exception("BaseResp.ErrCode.ERR_AUTH_DENIED"));
                break;
        }
    }
}
