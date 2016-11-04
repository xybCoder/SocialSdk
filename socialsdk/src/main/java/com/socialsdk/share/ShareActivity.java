package com.socialsdk.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.socialsdk.R;
import com.socialsdk.ShareUtil;
import com.socialsdk.event.ShareEvent;
import com.socialsdk.bean.ShareScene;
import com.socialsdk.widget.ShareButton;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.common.Constants;

import org.greenrobot.eventbus.EventBus;

/**
 * 一键社会化分享
 *
 */
public class ShareActivity extends Activity implements IWeiboHandler.Response {

    private ShareScene scene;

    private ShareButton sbWechat;
    private ShareButton sbWeChatTimeline;
    private ShareButton sbWeibo;
    private ShareButton sbQQ;
    private ShareButton sbQZone;
    private ShareButton sbMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        getWindow().setGravity(Gravity.BOTTOM);
        scene = (ShareScene) getIntent().getExtras().getSerializable("scene");
        initViews();
    }

    private void initViews() {
        sbWechat = (ShareButton) findViewById(R.id.social_share_sb_wechat);
        sbWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.setType(ShareScene.SHARE_TYPE_WECHAT);
                ShareUtil.shareToWeChat(ShareActivity.this, scene);
            }
        });
        sbWeChatTimeline = (ShareButton) findViewById(R.id.social_share_sb_wechat_timeline);
        sbWeChatTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtil.shareToWeChatTimeline(ShareActivity.this, scene);
                scene.setType(ShareScene.SHARE_TYPE_WECHAT_TIMELINE);
            }
        });
        sbWeibo = (ShareButton) findViewById(R.id.social_share_sb_weibo);
        sbWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.setType(ShareScene.SHARE_TYPE_WEIBO);
                ShareUtil.shareToWeibo(ShareActivity.this, scene);
            }
        });
        sbQQ = (ShareButton) findViewById(R.id.social_share_sb_qq);
        sbQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.setType(ShareScene.SHARE_TYPE_QQ);
                ShareUtil.shareToQQ(ShareActivity.this, scene);
            }
        });
        sbQZone = (ShareButton) findViewById(R.id.social_share_sb_qzone);
        sbQZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.setType(ShareScene.SHARE_TYPE_QZONE);
                ShareUtil.shareToQZone(ShareActivity.this, scene);
            }
        });
        sbMore = (ShareButton) findViewById(R.id.social_share_sb_more);
        sbMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scene.setType(ShareScene.SHARE_TYPE_DEFAULT);
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, scene.getTitle() + "\n\r" + scene.getUrl());
                share.putExtra(Intent.EXTRA_TITLE, scene.getTitle());
                share.putExtra(Intent.EXTRA_SUBJECT, scene.getDesc());
                startActivity(share);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.snack_out);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (scene.getType() == ShareScene.SHARE_TYPE_WEIBO) {
            ShareProxy.shareToWeiboCallback(intent,this);
            finish();
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_SUCCESS, scene.getType(), scene.getId()));
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_CANCEL, scene.getType()));
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                EventBus.getDefault().post(new ShareEvent(ShareEvent.TYPE_FAILURE, scene.getType(), new Exception("WBConstants.ErrorCode.ERR_FAIL: "
                        + baseResponse.errMsg)));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_QZONE_SHARE || requestCode == Constants.REQUEST_QQ_SHARE) {
            ShareProxy.shareToQCallback(requestCode,resultCode,data);
            finish();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (scene.getType() == ShareScene.SHARE_TYPE_WECHAT || scene.getType() == ShareScene.SHARE_TYPE_WECHAT_TIMELINE) {
            finish();
        }
    }

}
