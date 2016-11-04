package com.socialsdk.sso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.socialsdk.SSOUtil;
import com.socialsdk.R;
import com.socialsdk.widget.ShareButton;
import com.tencent.connect.common.Constants;

/**
 * 社交授权activity
 *
 */
public class SSOActivity extends Activity {

    private static final String TAG = "SSOActivity";
    private ShareButton llWeibo;
    private ShareButton llWeChat;
    private ShareButton llQQ;

    /**
     * type=0, 用户选择QQ或者微博登录
     * type=1，用户选择微信登录
     */
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_oauth);
        llWeibo = (ShareButton) findViewById(R.id.social_oauth_sb_weibo);
        llWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOProxy.loginWeibo(SSOActivity.this);
            }
        });
        llWeChat = (ShareButton) findViewById(R.id.social_oauth_sb_wechat);
        llWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOProxy.loginWeChat(SSOActivity.this);
                type = 1;
            }
        });
        llQQ = (ShareButton) findViewById(R.id.social_oauth_sb_qq);
        llQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOProxy.loginQQ(SSOActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SSOProxy.loginWeiboCallback(this,requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            SSOProxy.loginQQCallback(requestCode,resultCode,data);
        }

        if (type == 0) {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (type == 1) {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.snack_out);
    }
}
