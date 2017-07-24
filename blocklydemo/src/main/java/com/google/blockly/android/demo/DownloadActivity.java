package com.google.blockly.android.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Htw on 2017/7/21.
 */

public class DownloadActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btReturn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_layout);
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
        List <String>list=new ArrayList<>();
        for(int i=0;i<15;i++)
            list.add("文件"+i);
        recyclerView.setAdapter(new ItemAdapter(list));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>{
        List<String>dataList;

        ItemAdapter(List<String>dataList){
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
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tv.setText(dataList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DownloadActivity.this, dataList.get(position)+"", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_download);

            }
        }
    }
}