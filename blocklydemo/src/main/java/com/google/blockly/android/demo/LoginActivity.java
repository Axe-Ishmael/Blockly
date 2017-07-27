package com.google.blockly.android.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.blockly.MyApplication;
import com.google.blockly.android.demo.Post_receive.userLoginRegisterReceive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Htw on 2017/7/21.
 */

public class LoginActivity extends AppCompatActivity {

    public static String token;
    private final String url_login = "http://192.168.0.121:3000/userLogin";

    private boolean choseSavePass;
    private boolean choseAutoLogin;

    private CheckBox savePass;
    private CheckBox autoLogin;
    private EditText emailEt, passwordEt;
    private Button loginBt, registerBt, forgetBt;
    private String email="";//用户id或者说是邮箱地址
    private String password="";//用户登录密码
    private String savedemail ="";
    private String savedpassword = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {

        sharedPreferences = MyApplication.getSharedPreferences();
        savePass = (CheckBox)findViewById(R.id.savePass);
        //autoLogin = (CheckBox)findViewById(R.id.autoLogin);
        emailEt = (EditText) findViewById(R.id.email_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        loginBt = (Button) findViewById(R.id.login_bt);
        registerBt = (Button) findViewById(R.id.register_bt);
        forgetBt = (Button) findViewById(R.id.forget_bt);

        choseSavePass = sharedPreferences.getBoolean("chosesavePass",false);
        //choseAutoLogin = sharedPreferences.getBoolean("choseautoLogin",false);
        savedemail = sharedPreferences.getString("email","");
        savedpassword = sharedPreferences.getString("password","");
        if(choseSavePass){
            emailEt.setText(savedemail);
            passwordEt.setText(savedpassword);
            savePass.setChecked(true);
        }



        //按钮监听
        //登录键
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailEt.getText().toString();
                password=passwordEt.getText().toString();


                if(!judgeEmail(email)){
                    Toast.makeText(LoginActivity.this, "邮箱格式不正确请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "密码不能为空{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()>20){
                    Toast.makeText(LoginActivity.this, "密码不能超过20字符{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!judgePassword(password)){
                    passwordEt.setText("");
                    Toast.makeText(LoginActivity.this, "密码格式不正确，请输入字幕或数字{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(LoginActivity.this,LuaActivity.class);
               startActivity(intent);
//                userLogin();

            }
        });

        //注册
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //忘记密码
        forgetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    //判断密码格式正确性
    boolean judgePassword(String str){
        char ch;
        for(int i=0;i<str.length();i++){
            ch=str.charAt(i);
            if((ch>='0'&&ch<='9')||(ch>='a'&&ch<'z')||(ch>'A'&&ch<'Z')){
            }else{
                return false;
            }
        }
        return  true;
    }
//判断邮箱格式正确性
    boolean judgeEmail(String str){
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        boolean isMatched = matcher.matches();
        return  isMatched;
    }

    /**
     *用于登录操作的网络通讯
     */
    public void userLogin(){
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
//        String email ,password;
        try {
            jsonObject.put("userId",email);
            jsonObject.put("userPassword",password);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("userLogin",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_login, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                userLoginRegisterReceive userLoginReceive = new userLoginRegisterReceive();
                userLoginReceive.setStatus(response.optString("status"));
                userLoginReceive.setErrMsg(response.optString("eerMsg"));
                userLoginReceive.setToken(response.optString("token"));
                userLoginReceive.setJsonObject(response.optJSONObject("jsonStr"));
                Log.d("userLogin_response",userLoginReceive.getStatus()+" "+userLoginReceive.getErrMsg()+" "+userLoginReceive.getToken());

                judge_userLogin(userLoginReceive.getStatus(),userLoginReceive.getErrMsg(),userLoginReceive.getToken(),userLoginReceive.getJsonObject());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("userLogin",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    /**
     * 对userLogin事件返回值作出判断
     */
    public void judge_userLogin(String status,String errMsg,String token,JSONObject jsonStr){
        if (status.equals("601")){

            Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
            sharedPreferences = MyApplication.getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email",email);
            editor.putString("password",password);
            editor.putString("token",token);
            editor.putBoolean("chosesavePass",savePass.isChecked());
            editor.apply();
            Intent intent = new Intent(LoginActivity.this,LuaActivity.class);
            startActivity(intent);
        }else if (status.equals("602")){
            Toast.makeText(LoginActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else{
            Toast.makeText(LoginActivity.this,"发生未知错误！",Toast.LENGTH_LONG).show();
        }


    }





}
