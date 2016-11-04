package com.socialsdk.sso.wechat;

import com.socialsdk.bean.SocialToken;
import com.socialsdk.bean.SocialUser;

/**
 * 微信授权回调接口
 */
public interface IWXCallback {
    void onGetCodeSuccess(String code);
    void onGetTokenSuccess(SocialToken token);

    void onGetUserInfoSuccess(SocialUser user);

    void onFailure(Exception e);

    void onCancel();
}
