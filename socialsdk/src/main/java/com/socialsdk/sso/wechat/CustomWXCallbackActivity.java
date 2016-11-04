package com.socialsdk.sso.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.socialsdk.WXapi;
import com.socialsdk.share.wechat.WeChatShareProxy;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 *
 */

public class CustomWXCallbackActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        IWXAPI api = WXapi.getIWXAPIInstance();
        if (null != api) {
            api.handleIntent(intent, this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        IWXAPI api = WXapi.getIWXAPIInstance();
        if (null != api) {
            api.handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            CustomWeChatSSoProxy.authComplete((SendAuth.Resp) resp);
        } else if (resp instanceof SendMessageToWX.Resp) {
            WeChatShareProxy.shareComplete((SendMessageToWX.Resp) resp);
            finish();
        }
    }

}
