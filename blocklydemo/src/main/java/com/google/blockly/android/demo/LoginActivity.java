package com.google.blockly.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Htw on 2017/7/21.
 */

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBt, registerBt, forgetBt;
    String email="";//用户id或者说是邮箱地址
    String password="";//用户登录密码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {
        emailEt = (EditText) findViewById(R.id.email_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        loginBt = (Button) findViewById(R.id.login_bt);
        registerBt = (Button) findViewById(R.id.register_bt);
        forgetBt = (Button) findViewById(R.id.forget_bt);

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





}
