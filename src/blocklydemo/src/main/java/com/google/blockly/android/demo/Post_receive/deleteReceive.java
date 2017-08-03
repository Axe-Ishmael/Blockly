package com.google.blockly.android.demo.Post_receive;

import org.json.JSONObject;

/**
 * Created by admin on 2017/7/26.
 */

public class deleteReceive {

    public String status;
    public String errMsg;
    public JSONObject jsonObject;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
