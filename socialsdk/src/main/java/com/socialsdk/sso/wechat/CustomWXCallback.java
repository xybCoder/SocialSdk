package com.socialsdk.sso.wechat;

import com.socialsdk.bean.SocialToken;

/**
 * 根据项目需求定制
 */

public interface CustomWXCallback {

    /**
     * （从第三方服务器）获取code成功
     * @param code
     */
    void onGetCodeSuccess(String code);

    /**
     * （从自有服务器）获取openid ,access_token成功
     * @param token
     */
    void onGetTokenSuccess(SocialToken token);

    void onFailure(Exception e);

    void onCancel();
}
