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
import com.google.blockly.android.demo.Post_receive.confirmRegisterReceive;
import com.google.blockly.android.demo.Post_receive.mailRegisterReceive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Htw on 2017/7/21.
 */

public class RegisterActivity extends AppCompatActivity {
    private final String url_mailregister ="http://192.168.0.121:3000/userRegister";
    private final String url_confirmregister = "http://192.168.0.121:3000/confirmRegister";
    private EditText emailEt, codeEt, passwordEt1, passwordEt2,nameEt;
    private Button codeBt, registerBt,backBt;
    private String code = "";//验证码
    private String email = "";//邮箱
    private String password1 = "";//第一次输入密码
    private String password2 = "";//再次输入密码
    private String  name="";//昵称

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
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
        nameEt= (EditText) findViewById(R.id.name_et);
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
                    Toast.makeText(RegisterActivity.this, "邮箱格式不正确，请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    emailEt.setText("");

                    return;
                }
                else if(email.length()>50){
                    Toast.makeText(RegisterActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if(email.equals("")){
                    Toast.makeText(RegisterActivity.this, "输入邮箱不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    mailRegister();
                }

            }
        });

        //点击注册
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入值
                name = nameEt.getText().toString();
                code = codeEt.getText().toString();
                password1 = passwordEt1.getText().toString();
                password2 = passwordEt2.getText().toString();


                //判断输入错误
                email = emailEt.getText().toString();
                if (!judgeEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "邮箱格式不正确，请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    emailEt.setText("");
                    return;
                }
                else if(email.length()>50){
                    Toast.makeText(RegisterActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if(email.equals("")){
                    Toast.makeText(RegisterActivity.this, "输入邮箱不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (code.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请输入验证码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.equals("")) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!judgePassword(password1)) {
                    Toast.makeText(RegisterActivity.this, "密码格式不正确，请输入字母或数字{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }else if(password1.length()>20){
                    Toast.makeText(RegisterActivity.this, "密码最长20字符，超过20呐{>~<}", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (password2.equals("")) {
                    Toast.makeText(RegisterActivity.this, "请再次输入密码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!password1.equals(password2)) {
                    Toast.makeText(RegisterActivity.this, "两次密码不一致请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }

                if(!judgeName(name)){
                    Toast.makeText(RegisterActivity.this, "昵称中不能有空格和'<'和'>'", Toast.LENGTH_SHORT).show();
                    return;
                }else if (name.equals("")){
                    Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    confirmRegister();
                }


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

    //判断昵称没有 < > 空格
    boolean judgeName(String str){
        char ch;
        for(int i=0;i<str.length();i++){
            ch=str.charAt(i);
            if(ch==' '||ch=='<'||ch=='>'){
                return  false;
            }
        }
        return  true;
    }



    /**
     * 用于确认邮箱，获取验证码
     * @author Axe
     */
    public void mailRegister(){
        final RequestQueue requestQueue = MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        String email = emailEt.getText().toString();
        try {
            jsonObject.put("userId",email);

        }catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("mailRegister_JsonObj",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_mailregister, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mailRegisterReceive mailreceive = new mailRegisterReceive();
                mailreceive.setStatus(response.optString("status"));
                mailreceive.setErrMsg(response.optString("errMsg"));
                Log.d("mailRegister_response",mailreceive.getErrMsg()+"  "+mailreceive.getStatus());
                judge_mailRegister(mailreceive.getStatus(),mailreceive.getErrMsg());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("mailRegister",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    /**
     * 对MailRegister事件的返回值作出判断
     */
    public void judge_mailRegister(String status,String errMsg){
        if(status.equals("601")){
            Toast.makeText(RegisterActivity.this,errMsg+"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }else if (status.equals("602")){
            Toast.makeText(RegisterActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else {
            Toast.makeText(RegisterActivity.this,"发送未知错误",Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 用于最终确认注册
     */
    public void confirmRegister(){
        final RequestQueue requestQueue = MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userPassword",password1);
            jsonObject.put("verifyCode",code);
            jsonObject.put("userName",name);
            jsonObject.put("userId",email);
        }catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("confirmRegister_JsonObj",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_confirmregister, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                confirmRegisterReceive confirmreceive = new confirmRegisterReceive();
                confirmreceive.setStatus(response.optString("status"));
                confirmreceive.setErrMsg(response.optString("errMsg"));
                confirmreceive.setRemainNum(response.optString("remainNum"));
                Log.d("confirmReceive_response",confirmreceive.getStatus()+" "+confirmreceive.getErrMsg()+" "+confirmreceive.getRemainNum());
                judge_confirmRegister(confirmreceive.getStatus(),confirmreceive.getErrMsg());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("confirmRegister",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    /**
     * 对confirmRegister事件返回值作出判断
     */
    public void judge_confirmRegister(String status,String errMsg){

        if (status.equals("601")){
            Toast.makeText(RegisterActivity.this,errMsg,Toast.LENGTH_SHORT);
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            return;

        }else if (status.equals("602")){
            Toast.makeText(RegisterActivity.this,errMsg,Toast.LENGTH_LONG);
            return;
        }else if (status.equals("603")){
            Toast.makeText(RegisterActivity.this,errMsg,Toast.LENGTH_LONG);
            return;
        }else if (status.equals("605")){
            Toast.makeText(RegisterActivity.this,errMsg,Toast.LENGTH_LONG);
            return;
        }else {
            Toast.makeText(RegisterActivity.this,"发生未知错误",Toast.LENGTH_LONG);
        }

    }



}
