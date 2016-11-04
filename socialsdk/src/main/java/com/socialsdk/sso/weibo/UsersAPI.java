
package com.socialsdk.sso.weibo;

import android.content.Context;
import android.util.SparseArray;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

/**
 * 用户相关的接口
 */
public class UsersAPI extends AbsOpenAPI {

    private static final int READ_USER = 0;
    private static final int READ_USER_BY_DOMAIN = 1;
    private static final int READ_USER_COUNT = 2;

    private static final String API_BASE_URL = API_SERVER + "/users";

    private static final SparseArray<String> sAPIList = new SparseArray<String>();

    static {
        sAPIList.put(READ_USER, API_BASE_URL + "/show.json");
        sAPIList.put(READ_USER_BY_DOMAIN, API_BASE_URL + "/domain_show.json");
        sAPIList.put(READ_USER_COUNT, API_BASE_URL + "/counts.json");
    }

    public UsersAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    public void show(long uid, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("uid", uid);
        requestAsync(sAPIList.get(READ_USER), params, HTTPMETHOD_GET, listener);
    }

    public void show(String screen_name, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("screen_name", screen_name);
        requestAsync(sAPIList.get(READ_USER), params, HTTPMETHOD_GET, listener);
    }

    public void domainShow(String domain, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("domain", domain);
        requestAsync(sAPIList.get(READ_USER_BY_DOMAIN), params, HTTPMETHOD_GET, listener);
    }

    public void counts(long[] uids, RequestListener listener) {
        WeiboParameters params = buildCountsParams(uids);
        requestAsync(sAPIList.get(READ_USER_COUNT), params, HTTPMETHOD_GET, listener);
    }

    public String showSync(long uid) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("uid", uid);
        return requestSync(sAPIList.get(READ_USER), params, HTTPMETHOD_GET);
    }

    public String showSync(String screen_name) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("screen_name", screen_name);
        return requestSync(sAPIList.get(READ_USER), params, HTTPMETHOD_GET);
    }

    public String domainShowSync(String domain) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("domain", domain);
        return requestSync(sAPIList.get(READ_USER_BY_DOMAIN), params, HTTPMETHOD_GET);
    }

    public String countsSync(long[] uids) {
        WeiboParameters params = buildCountsParams(uids);
        return requestSync(sAPIList.get(READ_USER_COUNT), params, HTTPMETHOD_GET);
    }

    private WeiboParameters buildCountsParams(long[] uids) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        StringBuilder strb = new StringBuilder();
        for (long cid : uids) {
            strb.append(cid).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        params.put("uids", strb.toString());
        return params;
    }
}
