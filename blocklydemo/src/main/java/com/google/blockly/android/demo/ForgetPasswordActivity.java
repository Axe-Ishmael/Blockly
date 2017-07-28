package com.google.blockly.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.blockly.MyApplication;
import com.google.blockly.android.demo.Post_receive.mailForgetReceive;
import com.google.blockly.android.demo.Post_receive.resetPasswordReceive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */

public class ForgetPasswordActivity extends AppCompatActivity {

    private final String url_mailForget = "http://123.207.247.90:3000/forgetPassword";
    private final String url_resetPassword = "http://123.207.247.90:3000/resetPassword";
    private EditText emailEt, codeEt, passwordEt1, passwordEt2;
    private Button codeBt, registerBt,backBt;
    private String code = "";//验证码
    private String email = "";//邮箱
    private String password1 = "";//第一次输入密码
    private String password2 = "";//再次输入密码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_layout);
        initView();
    }

    void initView() {
        emailEt = (EditText) findViewById(R.id.email_et);
        codeEt = (EditText) findViewById(R.id.code_et);
        passwordEt1 = (EditText) findViewById(R.id.password_et1);
        passwordEt2 = (EditText) findViewById(R.id.password_et2);
        codeBt = (Button) findViewById(R.id.get_code_bt);
        registerBt = (Button) findViewById(R.id.register_bt);
        backBt= (Button) findViewById(R.id.back_bt);
        //点击返回
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击获取验证码
        codeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEt.getText().toString();
                if (!judgeEmail(email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "邮箱格式不正确，请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    emailEt.setText("");
                    return;
                }else if(email.length()>50){
                    Toast.makeText(ForgetPasswordActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    mailForget();
                }

            }
        });

        //点击修改密码
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入值
                code = codeEt.getText().toString();
                password1 = passwordEt1.getText().toString();
                password2 = passwordEt2.getText().toString();


                //判断输入错误
                email = emailEt.getText().toString();
                if (!judgeEmail(email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "邮箱格式不正确，请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    emailEt.setText("");
                    return;
                }else if(email.length()>50){
                    Toast.makeText(ForgetPasswordActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (code.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "请输入验证码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码不能为空{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!judgePassword(password1)) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码格式不正确，请输入字母或数字{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }else if (password2.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "请再次输入密码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!password1.equals(password2)) {
                    Toast.makeText(ForgetPasswordActivity.this, "两次密码不一致请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }else if(password1.length()>20){
                    Toast.makeText(ForgetPasswordActivity.this, "密码最长20字符，超过20呐{>~<}", Toast.LENGTH_SHORT).show();
                    return;

                }

                resetPassword();

                //以下可以对数据进行正确处理的逻辑

            }
        });

    }


    //判断密码格式正确性
    boolean judgePassword(String str) {
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch < 'z') || (ch > 'A' && ch < 'Z')) {
            } else {
                return false;
            }
        }
        return true;
    }

    //判断邮箱格式正确性
    boolean judgeEmail(String str) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        boolean isMatched = matcher.matches();
        return isMatched;
    }

    /**
     * 用于获取验证码的网络通讯请求
     *
     */
    public void mailForget(){
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",email);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_mailForget, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mailForgetReceive mailForgetReceive = new mailForgetReceive();
                mailForgetReceive.setStatus(response.optString("status"));
                mailForgetReceive.setErrMsg(response.optString("errMsg"));
                Log.d("mailForget_Response",mailForgetReceive.getStatus()+"  "+mailForgetReceive.getErrMsg());
                judge_mailForget(mailForgetReceive.getStatus(),mailForgetReceive.getErrMsg());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("mailForget_Error",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    /**
     * 用于对请求验证码返回值作出判断
     */

    public void judge_mailForget(String status,String errMsg){
        if (status.equals("601")){

            Toast.makeText(ForgetPasswordActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else if (status.equals("602")){
            Toast.makeText(ForgetPasswordActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;

        }else {

            Toast.makeText(ForgetPasswordActivity.this,"发生未知错误！",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 用于修改密码的网络请求
     */
    public void resetPassword(){
        final RequestQueue requestQueue =MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userPassword",password1);
            jsonObject.put("verifyCode",code);
            jsonObject.put("userId",email);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("resetPassword_JsonObj",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_resetPassword, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                resetPasswordReceive resetPasswordReceive = new resetPasswordReceive();
                resetPasswordReceive.setStatus(response.optString("status"));
                resetPasswordReceive.setErrMsg(response.optString("errMsg"));
                resetPasswordReceive.setRemainNum(response.optString("remainNum"));
                Log.d("resetPassword_Response",resetPasswordReceive.getStatus()+"  "+resetPasswordReceive.getErrMsg()+" "+resetPasswordReceive.getRemainNum());

                judge_resetPassword(resetPasswordReceive.getStatus(),resetPasswordReceive.getErrMsg(),resetPasswordReceive.getRemainNum());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("resetPassword_Error",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 用于对修改密码时间返回值的判断
     */

    public void judge_resetPassword(String status,String errMsg,String remainNum){
        if (status.equals("601")){
            Toast.makeText(ForgetPasswordActivity.this,errMsg,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
            startActivity(intent);
        }else if (status.equals("602")){
            Toast.makeText(ForgetPasswordActivity.this,errMsg+" "+"剩余验证码输入次数："+remainNum,Toast.LENGTH_LONG).show();
            return;
        }else if (status.equals("603")){
            Toast.makeText(ForgetPasswordActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else if (status.equals("605")){
            Toast.makeText(ForgetPasswordActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else {
            Toast.makeText(ForgetPasswordActivity.this,"发生未知错误！",Toast.LENGTH_LONG).show();
        }

    }

}