package com.socialsdk.share.wechat;
public interface IWXShareCallback {

    void onSuccess();

    void onCancel();

    void onFailure(Exception e);
}
