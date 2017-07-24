package com.google.blockly.android.demo;

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

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText emailEt, codeEt, passwordEt1, passwordEt2;
    Button codeBt, registerBt,backBt;
    String code = "";//验证码
    String email = "";//邮箱
    String password1 = "";//第一次输入密码
    String password2 = "";//再次输入密码

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
                }
                if(email.length()>50){
                    Toast.makeText(ForgetPasswordActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
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
                }
                if(email.length()>50){
                    Toast.makeText(ForgetPasswordActivity.this, "最长50字符，邮箱太长了{>~<}", Toast.LENGTH_SHORT).show();
                }

                if (code.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "请输入验证码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码不能为空{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!judgePassword(password1)) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码格式不正确，请输入字母或数字{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }

                if (password2.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "请再次输入密码{>~<}", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password1.equals(password2)) {
                    Toast.makeText(ForgetPasswordActivity.this, "两次密码不一致请重新输入{>~<}", Toast.LENGTH_SHORT).show();
                    passwordEt1.setText("");
                    passwordEt2.setText("");
                    return;
                }

                if(password1.length()>20){
                    Toast.makeText(ForgetPasswordActivity.this, "密码最长20字符，超过20呐{>~<}", Toast.LENGTH_SHORT).show();
                    return;

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
}