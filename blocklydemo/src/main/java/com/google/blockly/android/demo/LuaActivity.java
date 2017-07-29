/*
 *  Copyright 2017 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.android.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.blockly.MyApplication;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.BlocklyActivityHelper;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.LanguageDefinition;
import com.google.blockly.android.demo.Bluetooth.BluetoothActivity;
import com.google.blockly.android.demo.Bluetooth.ConnectThread;
import com.google.blockly.android.demo.Post_receive.fileContent;
import com.google.blockly.android.demo.Post_receive.fileListContent;
import com.google.blockly.android.demo.Post_receive.getListReceive;
import com.google.blockly.android.demo.Post_receive.uploadReceive;
import com.google.blockly.model.DefaultBlocks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Demo activity that programmatically adds a view to split the screen between the Blockly workspace
 * and an arbitrary other view or fragment.
 */
public class LuaActivity extends AbstractBlocklyActivity {



    private final String url_upload = "http://123.207.247.90:3000/AndrUpload";
    private final String url_filelistrequest = "http://123.207.247.90:3000/AndrGetList";
    protected BlocklyActivityHelper mBlocklyActivityHelper;

    private static int flags = 0;
    private boolean tag = true;

    private SharedPreferences sharedPreferences = MyApplication.getSharedPreferences();
//    private String xmltostring;
    private String filename = "";
    private String userId;
    private String token;

    public Button btn_save,btn_cancle;
    public EditText editText;
    public String dialogMsg = "";

    private static final String TAG = "LuaActivity";

    private static final String SAVE_FILENAME = "lua_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "lua_workspace_temp.xml";
    // Add custom blocks to this list.
    private static final List<String> BLOCK_DEFINITIONS = DefaultBlocks.getAllBlockDefinitions();
    private static final List<String> LUA_GENERATORS = Arrays.asList();

    private static final LanguageDefinition LUA_LANGUAGE_DEF
            = new LanguageDefinition("lua/lua_compressed.js", "Blockly.Lua");

    private TextView mGeneratedTextView;
    private Handler mHandler;
    static String LuaLanguage;//保存输出的Lua语言代码

    private String mNoCodeText;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==789){
                Toast.makeText(LuaActivity.this, msg.obj.toString()+"", Toast.LENGTH_SHORT).show();
            }
        }
    };


    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LuaLanguage = generatedCode.toString();
                            Log.d("LuaLanguage", LuaLanguage);
                            mGeneratedTextView.setText(generatedCode);
                            updateTextMinWidth();

                            //当文件已经保存时，生成lua代码

                            if (ConnectThread.mmSocket == null || !ConnectThread.mmSocket.isConnected()) {

                                Toast.makeText(LuaActivity.this, "请在本应用中选择并连接蓝牙", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LuaActivity.this, BluetoothActivity.class);
                                startActivity(intent);

                            } else {
                                OutputStream out = null;
                                try {
                                    out = ConnectThread.mmSocket.getOutputStream();
                                    byte[] b = LuaLanguage.getBytes();
                                    out.write(b);
                                    Toast.makeText(LuaActivity.this, "传输成功", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LuaActivity.this, "传输失败", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder dialog = new AlertDialog.Builder
                                            (LuaActivity.this);
                                    dialog.setTitle("传输失败了");
                                    dialog.setMessage("可能是蓝牙连接断开导致，要不试试重新连接蓝牙");
                                    dialog.setPositiveButton("重连蓝牙", new DialogInterface.
                                            OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(LuaActivity.this, BluetoothActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    dialog.setNegativeButton("取消", new DialogInterface.
                                            OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    dialog.show();
                                }


                            }
                        }
                    });
                }
            };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            hideView();
            showAlertDialog(LuaActivity.this);

            return true;
        } else if (id == R.id.action_load) {
//            onLoadWorkspace();
            /*
            Intent intent=new Intent(LuaActivity.this,DownloadActivity.class);
            startActivity(intent);
            fileContent fileContent = new fileContent();
            fileContent = (fileContent)getIntent().getSerializableExtra("fileContent");
            String fileSavedPath = savexmlString(fileContent);
            onLoadWorkspace(fileSavedPath);

            */
            fileListRequest();



            return true;
        } else if (id == R.id.action_clear) {
            onClearWorkspace();
            return true;
        } else if (id == R.id.action_run) {
            if (getController().getWorkspace().hasBlocks()) {
                onRunCode();

            } else {
                Log.i(TAG, "No blocks in workspace. Skipping run request.");
                Toast.makeText(this, "请先保存文件", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home && mNavigationDrawer != null) {
            setNavDrawerOpened(!isNavDrawerOpen());
        }else if (id == R.id.action_showcode){

            hideView();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        //initReadBluethThread();
    }

    @Override
    protected View onCreateContentView(int parentId) {
        View root = getLayoutInflater().inflate(R.layout.split_content, null);
        mGeneratedTextView = (TextView) root.findViewById(R.id.generated_code);
        updateTextMinWidth();

        mNoCodeText = mGeneratedTextView.getText().toString(); // Capture initial value.

        return root;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.split_actionbar;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected LanguageDefinition getBlockGeneratorLanguage() {
        return LUA_LANGUAGE_DEF;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return DefaultBlocks.TOOLBOX_PATH;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return LUA_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        // Uses the same callback for every generation call.
        return mCodeGeneratorCallback;
    }

    @Override
    public void onClearWorkspace() {
        super.onClearWorkspace();
        mGeneratedTextView.setText(mNoCodeText);
        updateTextMinWidth();
    }

    /**
     * Estimate the pixel size of the longest line of text, and set that to the TextView's minimum
     * width.
     */
    private void updateTextMinWidth() {
        String text = mGeneratedTextView.getText().toString();
        int maxline = 0;
        int start = 0;
        int index = text.indexOf('\n', start);
        while (index > 0) {
            maxline = Math.max(maxline, index - start);
            start = index + 1;
            index = text.indexOf('\n', start);
        }
        int remainder = text.length() - start;
        if (remainder > 0) {
            maxline = Math.max(maxline, remainder);
        }

        float density = getResources().getDisplayMetrics().density;
        mGeneratedTextView.setMinWidth((int) (maxline * 13 * density));
    }

    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }

    /**
     * 提示用户填写文件保存名
     */
    public void showAlertDialog(Activity mActivity){

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        //final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        builder.setView(LayoutInflater.from(mActivity).inflate(R.layout.alert_dialog,null));
        final AlertDialog alertDialog   = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setContentView(R.layout.alert_dialog);
        btn_save = (Button)alertDialog.findViewById(R.id.btn_save);
        btn_cancle = (Button)alertDialog.findViewById(R.id.btn_cancel);
        editText = (EditText)alertDialog.findViewById(R.id.et_content);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SaveButton","点击了");
                flags = 0;
                String i = "";
                i= flags+"";
                Log.d("Flag_clicked",i);
                dialogMsg = editText.getText().toString();
                if(dialogMsg == null){
                    editText.setError("输入内容不能为空！");
                    Toast.makeText(LuaActivity.this,"Blank",Toast.LENGTH_LONG).show();
                }else {
                    flags = 1;
                    String o = "";
                    o = flags+"";
                    Log.d("Flag_save",o);
                    Log.d("SaveButton","保存");

                    onSaveWorkspace(dialogMsg);
                    andrUpload();
                    Log.d("SaveButton","执行了");
//                    andrUpload();
                    Log.d("Save","执行了");

                    alertDialog.dismiss();


                }
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flags = 0;
                alertDialog.dismiss();
            }
        });

    }


/*
    public void onSaveWorkspace(String curfilename,int a){
        String filename = curfilename+"_"+getWorkspaceSavePath();
        xmltostring = mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(filename);
        andrUpload();
    }
    */




/*
   //@Override
    public void onLoadWorkspace(String fileSavedPath) {
        mBlocklyActivityHelper.loadWorkspaceFromAppDirSafely(fileSavedPath);
    }
*/


    /**
     * 用于保存XML文件的网络请求
     */
    public void andrUpload(){
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        sharedPreferences = MyApplication.getSharedPreferences();
        userId = sharedPreferences.getString("email","");
        token = sharedPreferences.getString("token","");
        JSONObject jsonObject = new JSONObject();
        String fileName = dialogMsg+"_"+getWorkspaceSavePath();
        try{
            jsonObject.put("fileName",fileName);
            jsonObject.put("fileString",AbstractBlocklyActivity.xmltostring);//AbstractBlocklyActivity.xmltostring
            jsonObject.put("fileId",0);//这里应该通过一个方法来判断id的值，先暂时写成这样
            jsonObject.put("userId",userId);
            jsonObject.put("token",token);

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("AndrUpload_JsonObj",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_upload, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                uploadReceive uploadReceive = new uploadReceive();
                uploadReceive.setStatus(response.optString("status"));
                uploadReceive.setErrMsg(response.optString("errMsg"));
                Log.d("AndrUpload_Response",response.toString());

                judge_upload(uploadReceive.getStatus(),uploadReceive.getErrMsg());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AndrUpload_Error",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    /**
     * 用户请求文件列表的网络通信
     */
    public void fileListRequest(){
        String userId = sharedPreferences.getString("email","");
        String token  = sharedPreferences.getString("token","");
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("token",token);
            Log.d("fileListRequest_JsonObj",jsonObject.toString());


        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_filelistrequest, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getListReceive getListReceive = new getListReceive();
                getListReceive.setStatus(response.optString("status"));
                getListReceive.setErrMsg(response.optString("errMsg"));
                getListReceive.setJsonObject(response.optJSONObject("jsonStrArray"));
                Log.d("getFileList_Response",response.toString());

                judge_fileListRequest(getListReceive.getStatus(),getListReceive.getErrMsg(),getListReceive.getJsonObject());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fileListRequest_Error",error.toString(),error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }




    /**
     * 用于对AndrUpload事件返回值作出判断
     */
    public void judge_upload(String status,String errMsg){

        if (status.equals("201")){
            Toast.makeText(LuaActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;

        }else if (status.equals("202")){
            Toast.makeText(LuaActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else if(status.equals("603")){
            Toast.makeText(LuaActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;
        }else {
            Toast.makeText(LuaActivity.this,"发生未知错误！",Toast.LENGTH_LONG).show();

        }

    }

    /**
     * 用于对fileListRequest事件的返回值做出判断
     */
    public void judge_fileListRequest(String status, String errMsg,JSONObject jsonObject){

        if (status.equals("200")){
            //此处添加获取JsonArray内容的方法
            //要将fileListContents传往DownLoadActivity
//            List<fileListContent> fileListContents = new ArrayList<fileListContent>();//获取jsonArray中的内容
            ArrayList<fileListContent> fileListContents = new ArrayList<fileListContent>();
            fileListContents = getfilelistContent(jsonObject);
            Intent intent = new Intent();
            intent.setClass(LuaActivity.this,DownloadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("listContents", fileListContents);
            intent.putExtras(bundle);
            startActivityForResult(intent,0);


            Toast.makeText(LuaActivity.this,errMsg,Toast.LENGTH_LONG).show();
        }else if (status.equals("601")){
            Toast.makeText(LuaActivity.this,errMsg,Toast.LENGTH_LONG).show();
            return;

        }else {
            Toast.makeText(LuaActivity.this,"发生未知错误！",Toast.LENGTH_LONG).show();

        }

    }

    /**
     * 获取JSONObject对象中的JSONArray的内容
     *
     */

    public ArrayList<fileListContent> getfilelistContent(JSONObject jsonObject){

        ArrayList<fileListContent> fileListContents = new ArrayList<fileListContent>();
        JSONArray jsonArray = jsonObject.optJSONArray("fileList");
        for (int i = 0;i < jsonArray.length();i++){
            JSONObject jsonObject1 = (JSONObject) jsonArray.opt(i);
            fileListContent fileListContent = new fileListContent();
            fileListContent.setFileId(jsonObject1.optString("fileId"));
            fileListContent.setFileName(jsonObject1.optString("fileName"));
            fileListContents.add(fileListContent);
        }

        return fileListContents;
    }

    /**
     * 存储下载的XML文件（String to XML）并返回文件名
     */
    public String savexmlString(fileContent fileContent){
        String fileName = fileContent.getFileName();
        String fileId = fileContent.getFileId();
        String xmlString = fileContent.getFileData();
        FileOutputStream fileOutputStream = null;
        BufferedWriter writer  = null;
        try{
            fileOutputStream = openFileOutput(fileName,MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            writer.write(xmlString);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return fileName;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                fileContent fileContent = new fileContent();
                fileContent = (fileContent)data.getExtras().getSerializable("fileContent");
                String fileSavedPath = savexmlString(fileContent);
                onLoadWorkspace(fileSavedPath);
                break;

        }
    }

    /**
     * 待用蓝牙模块
     */
    private void initReadBluethThread() {



        new Thread() {
            // Keep listening to the InputStream until an exception occurs

            @Override
            public void run() {
                super.run();
                while(true){
                    byte[] buffer = new byte[1024];  // buffer store for the stream
                    try {
                        if (ConnectThread.mmSocket != null && ConnectThread.mmSocket.isConnected()) {

                            // Read from the InputStream
                            ConnectThread.mmSocket.getInputStream().read(buffer);
                            // Send the obtained bytes to the UI activity
                            String result = new String(buffer);
                            Message msg=new Message();
                            msg.what=789;
                            msg.obj=result;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        }.start();
    }

    /**
     * 测试TextView消失
     */
    public void hideView(){
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.textView_Layout);
        if (tag){
            relativeLayout.setVisibility(View.GONE);
            tag = false;
        }else {
            relativeLayout.setVisibility(View.VISIBLE);
            tag = true;
        }
    }




}
