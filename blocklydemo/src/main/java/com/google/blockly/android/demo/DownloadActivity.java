package com.google.blockly.android.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.blockly.MyApplication;
import com.google.blockly.android.demo.Post_receive.deleteReceive;
import com.google.blockly.android.demo.Post_receive.downloadReceive;
import com.google.blockly.android.demo.Post_receive.fileContent;
import com.google.blockly.android.demo.Post_receive.fileListContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Htw on 2017/7/21.
 */

public class DownloadActivity extends AppCompatActivity {

    private final String url_delete = "http://123.207.247.90:3000/AndrDelete";
    private final String url_download = "http://123.207.247.90:3000/AndrDownload";
    private RecyclerView recyclerView;
    private Button btReturn;
    private Intent intent ;
    private ArrayList<fileListContent> fileListContents;
    private String deleteId;
    private String downloadId;
    private String userId;
    private String token;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_layout);
//        sharedPreferences = MyApplication.getSharedPreferences();
        fileListContents= new ArrayList<fileListContent>();
        fileListContents = (ArrayList<fileListContent>)getIntent().getSerializableExtra("listContents");
        init();
    }

    private  void init(){
        btReturn= (Button) findViewById(R.id.back_bt);

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView= (RecyclerView) findViewById(R.id.rv_download);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ItemAdapter(fileListContents));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>{
        List<fileListContent>dataList;

        ItemAdapter(List<fileListContent>dataList){
            this.dataList=dataList;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    DownloadActivity.this).inflate(R.layout.download_item, parent,
                    false));

            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            
            holder.fileIdTv.setText(fileListContents.get(position).getFileId());
            holder.fileNameTv.setText(fileListContents.get(position).getFileName());


            /**
             * 删除按钮响应事件
             */
            holder.deleteBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DownloadActivity.this, "", Toast.LENGTH_SHORT).show();
                    deleteId =holder.fileIdTv.getText().toString();

                    sharedPreferences = MyApplication.getSharedPreferences();
                    RequestQueue requestQueue = MyApplication.getRequestQueue();
                    userId = sharedPreferences.getString("email","");
                    token = sharedPreferences.getString("token","");
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("fileId",deleteId);
                        jsonObject.put("userId",userId);
                        jsonObject.put("token",token);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_delete, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            String status;
                            String errMsg;
                            deleteReceive deleteReceive = new deleteReceive();
                            deleteReceive.setStatus(response.optString("status"));
                            deleteReceive.setErrMsg(response.optString("errMsg"));
                            deleteReceive.setJsonObject(response.optJSONObject("jsonStr"));//此处获取的Json对象暂时不用，为以后做扩展做准备
                            Log.d("delete_Response",response.toString());
                            status = deleteReceive.getStatus();
                            errMsg = deleteReceive.getErrMsg();

                            if (status.equals("200")){

                                Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                //添加使该项消失的方法
                                fileListContents.remove(position);
                                notifyItemRemoved(position);


                            }else if (status.equals("601")){

                                Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                return;
                            }else if (status.equals("602")){

                                Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                return;

                            }else {
                                Toast.makeText(DownloadActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();

                            }




                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Delete_Error",error.toString(),error);
                        }
                    });
                    requestQueue.add(jsonObjectRequest);




                }
            });


            /**
             * 下载按钮响应事件
             */
            holder.downloadBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadId =holder.fileIdTv.getText().toString();
                    downLoad();
                    
                }
            });



            /*
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    
                    Toast.makeText(DownloadActivity.this, dataList.get(position)+"", Toast.LENGTH_SHORT).show();
                }
            });*/
        }


        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView fileNameTv;
            TextView fileIdTv;
            Button deleteBt;
            Button downloadBt;

            public MyViewHolder(View itemView) {
                super(itemView);
                fileNameTv= (TextView) itemView.findViewById(R.id.tv_fileName);
                fileIdTv = (TextView) itemView.findViewById(R.id.tv_fileId);
                downloadBt= (Button) itemView.findViewById(R.id.down_bt);
                deleteBt= (Button) itemView.findViewById(R.id.delete_bt);

            }
        }
    }

    /**
     * 用于请求删除后端数据库保存的文件 AndrDelete
     */
    public void delete(){
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        userId = sharedPreferences.getString("email","");
        token = sharedPreferences.getString("token","");
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("fileId",deleteId);
            jsonObject.put("userId",userId);
            jsonObject.put("token",token);

        }catch (JSONException e){
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_delete, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String status;
                String errMsg;
                deleteReceive deleteReceive = new deleteReceive();
                deleteReceive.setStatus(response.optString("status"));
                deleteReceive.setErrMsg(response.optString("errMsg"));
                deleteReceive.setJsonObject(response.optJSONObject("jsonStr"));//此处获取的Json对象暂时不用，为以后做扩展做准备
                Log.d("delete_Response",response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
           public void onErrorResponse(VolleyError error) {
                Log.e("Delete_Error",error.toString(),error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * 用于对Delete事件的返回值做出判断
     */
    public void judge_delete(String status,String errMsg,int position){
        if (status.equals("200")){

            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            //添加使该项消失的方法
            fileListContents.remove(position);
            // notifyItemRemove();用球不起


        }else if (status.equals("601")){

            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            return;
        }else if (status.equals("602")){

            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            return;

        }else {
            Toast.makeText(DownloadActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * 用于AndrDownload下载请求
     */
    public void downLoad(){

        RequestQueue requestQueue = MyApplication.getRequestQueue();
        sharedPreferences = MyApplication.getSharedPreferences();
        userId = sharedPreferences.getString("email","");
        token = sharedPreferences.getString("token","") ;

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("fileId",downloadId);
            jsonObject.put("userId",userId);
            jsonObject.put("token",token);

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("Download_JsonObj",jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_download, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                downloadReceive downloadReceive = new downloadReceive();
                downloadReceive.setStatus(response.optString("status"));
                downloadReceive.setErrMsg(response.optString("errMsg"));
                downloadReceive.setJsonObject(response.optJSONObject("jsonStr"));
                Log.d("Download_Response",response.toString());

                judge_download(downloadReceive.getStatus(),downloadReceive.getErrMsg(),downloadReceive.getJsonObject());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Download_Error",error.toString(),error);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    /**
     * 用于对Download事件返回值作出判断
     */
    public void judge_download(String status,String errMsg,JSONObject jsonObject){
        if (status.equals("200")){
            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            //添加获取JSONObject内容的方法
            //并传给LuaActivity进行加载
            fileContent fileContent = new fileContent();
            fileContent = getfileContent(jsonObject);
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            bundle.putSerializable("fileContent",fileContent);
            intent.putExtras(bundle);
//            intent.setClass(DownloadActivity.this,LuaActivity.class);
//            startActivity(intent);
            this.setResult(RESULT_OK,intent);
            this.finish();
            Log.d("Finish","finishi()");


        }else if (status.equals("601")){

            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            return;

        }else if (status.equals("602")){

            Toast.makeText(DownloadActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            return;

        }else {
            Toast.makeText(DownloadActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 用于获取Download事件返回值中JSONObject对象中的内容
     */
    public fileContent getfileContent(JSONObject jsonObject){
        fileContent fileContent = new fileContent();
        fileContent.setFileId(jsonObject.optString("fileId"));
        fileContent.setFileName(jsonObject.optString("fileName"));
        fileContent.setFileData(jsonObject.optString("fileData"));
        Log.d("fileContent",fileContent.toString());

        return  fileContent;
    }




}