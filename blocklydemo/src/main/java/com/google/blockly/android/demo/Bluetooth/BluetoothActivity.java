package com.google.blockly.android.demo.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.blockly.android.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;//打开蓝牙成功与否
    private static final String TAG = "BluetoothActivity";
    int newState;//蓝牙状态
    Button btOpen, btRestart;
    RecyclerView recyclerView;
    ItemAdapter mAdapter;
    List<BluetoothDevice> deviceList;
    BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver queryReceiver, stateReceiver;
    boolean isClickOpen = false;//已经点击打开蓝牙
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        judgeHaveBluetooth();
        init();
        initBroadcast();//搜索设备结果的广播

    }

    //判断是否支持蓝牙并初始化BluetoothAdapter
    private void judgeHaveBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断设备是否支持蓝牙，不支持就没有后续操作了
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "你的设备不支持蓝牙!", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        btOpen = (Button) findViewById(R.id.button);
        btRestart = (Button) findViewById(R.id.bt_reStart);
        recyclerView = (RecyclerView) findViewById(R.id.list_source);

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter.isEnabled()) {
                    if (mAdapter.getItemCount() > 0) {
                        recyclerView.removeAllViews();
                        mAdapter.clearAllData();
                        mAdapter.notifyDataSetChanged();
                    }
                    mBluetoothAdapter.startDiscovery();
                }else{
                    Toast.makeText(BluetoothActivity.this, "请先打开蓝牙>_<", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceList = new ArrayList<>();
        mAdapter = new ItemAdapter(deviceList, this,progressDialog);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


    }

    private void initBroadcast() {
        queryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mAdapter.addItem(device, mBluetoothAdapter);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(queryReceiver, filter);

        stateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    switch (newState) {
                        case BluetoothAdapter.STATE_TURNING_ON://本地蓝牙适配器正在打开。本地客端在尝试使用适配器之前应该等待STATE_ON。
                            Log.d(TAG, "新状态：" + " 正在打开");
                            break;
                        case BluetoothAdapter.STATE_ON://表示本地蓝牙适配器已打开，并可以使用。
                            Log.d(TAG, "新状态：" + " 打开");
                            mBluetoothAdapter.startDiscovery();//开始扫描
                            //Toast.makeText(context, "打开", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://表示本地蓝牙适配器已关闭。本地客户端应立即尝试正确断开任何远程链接。
                            Log.d(TAG, "新状态：" + " 正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF://表示本地蓝牙适配器已关闭
                            Log.d(TAG, "新状态：" + " 关闭");
                            break;
                        default:
                            Log.d(TAG, "失败");
                            break;
                    }
                }
            }

        };
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(stateReceiver, filter2);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(queryReceiver);
        unregisterReceiver(stateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //打开蓝牙成功与否
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "蓝牙打开失败！", Toast.LENGTH_SHORT).show();
                } else {
                    isClickOpen = true;
                }
            default:
                break;
        }
    }



}
