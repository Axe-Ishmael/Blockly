package com.google.blockly.android.demo.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.blockly.android.R;
import com.google.blockly.android.demo.LuaActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Htw on 2017/7/8.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    List<BluetoothDevice> deviceList;
    List<String> nameList;
    Context context;
    BluetoothAdapter bluetoothAdapter;
    ProgressDialog progressDialog;
    ConnectThread connectThread;
    boolean isConnecting=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                progressDialog.dismiss();
                    Toast.makeText(context, "蓝牙连接成功（ゝω・）", Toast.LENGTH_SHORT).show();
                    isConnecting=true;
                    Intent intent=new Intent(context,LuaActivity.class);
                    context.startActivity(intent);

                    break;
                case 2:
                    progressDialog.dismiss();
                    Toast.makeText(context, "蓝牙连接失败，请重试(｡•́︿•̀｡)", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static final String TAG = "ImageAdapter";

    public ItemAdapter(List<BluetoothDevice> deviceList, Context context, ProgressDialog
            progressDialog) {
        this.deviceList = deviceList;
        this.context = context;
        this.progressDialog=progressDialog;
    }

    public void addItem(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        nameList=new ArrayList<>();
        nameList.add(0,device.getName()+"\n"+device.getAddress());
        deviceList.add(0,device);
        notifyItemInserted(nameList.size()-1);
        this.bluetoothAdapter=bluetoothAdapter;
    }

    public void clearAllData(){
        nameList.clear();
        deviceList.clear();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv.setText(nameList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getLayoutPosition();
                connectThread=new ConnectThread(deviceList.get(position),bluetoothAdapter,handler);
                connectThread.start();
                progressDialog = new ProgressDialog
                        (context);
                progressDialog.setTitle("正在连接");
                progressDialog.setMessage("请等待");
                progressDialog.setCancelable(true);
                progressDialog.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_text);
        }
    }


}
