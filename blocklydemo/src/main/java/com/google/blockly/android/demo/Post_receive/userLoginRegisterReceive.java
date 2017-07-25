package com.google.blockly.android.demo.Post_receive;

import org.json.JSONObject;

/**
 * Created by admin on 2017/7/24.
 */

public class userLoginRegisterReceive {

    public String status;
    public String errMsg;
    public String token;
    public JSONObject jsonObject;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrMsg() {

        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
