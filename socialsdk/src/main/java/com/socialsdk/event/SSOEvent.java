package com.socialsdk.event;

import com.socialsdk.bean.SocialToken;
import com.socialsdk.bean.SocialUser;

/**
 *
 * 授权消息事件
 *
 */
public class SSOEvent {

    public static final int PLATFORM_DEFAULT = 0;
    public static final int PLATFORM_WEIBO = 1;
    public static final int PLATFORM_WECHAT = 2;
    public static final int PLATFORM_QQ = 3;

    public static final int TYPE_GET_TOKEN = 0;
    public static final int TYPE_GET_USER = 1;
    public static final int TYPE_FAILURE = 2;
    public static final int TYPE_CANCEL = 3;

    private int type;
    private int platform;
    private SocialUser user;
    private SocialToken token;
    private Exception exception;

    public SSOEvent(int type, int platform) {
        this.type = type;
        this.platform = platform;
    }

    public SSOEvent(int type, int platform, SocialUser user) {
        this.type = type;
        this.platform = platform;
        this.user = user;
    }

    public SSOEvent(int type, int platform, SocialToken token) {
        this.type = type;
        this.platform = platform;
        this.token = token;
    }

    public SSOEvent(int type, int platform, Exception exception) {
        this.type = type;
        this.platform = platform;
        this.exception = exception;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SocialUser getUser() {
        return user;
    }

    public void setUser(SocialUser user) {
        this.user = user;
    }

    public SocialToken getToken() {
        return token;
    }

    public void setToken(SocialToken token) {
        this.token = token;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
