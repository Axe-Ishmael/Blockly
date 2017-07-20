package com.google.blockly.android.demo.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public   class ConnectThread extends Thread {
    public static boolean isConnect=false;
    public static BluetoothSocket mmSocket=null;
    private final BluetoothDevice mmDevice;
    BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "ConnectThread";
    Handler handler;
    Message msg;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.mBluetoothAdapter=mBluetoothAdapter;
        this.handler=handler;
        mmDevice = device;

        BluetoothSocket tmp = null;
        msg=new Message();

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            String uuid="00001101-0000-1000-8000-00805f9b34fb";
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) {
            Log.d(TAG, "ConnectThread: 初始化uuid错误");
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        Log.d(TAG, "run:线程开始 ");
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            isConnect=true;
            msg.what=1;
            handler.sendMessage(msg);
        } catch (IOException connectException) {
            isConnect=false;
            connectException.printStackTrace();
            Log.d(TAG, "run: 连接错误");
            msg.what=2;
            handler.sendMessage(msg);
            // Unable to connect; close the socket and get out
            try {
                isConnect=false;
                mmSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
                Log.d(TAG, "run: 关闭错误");
            }
            return;
        }catch(Exception e){
            isConnect=false;
            Log.d(TAG, "run: 出现错误");
            msg.what=2;
            handler.sendMessage(msg);
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            isConnect=false;
            mmSocket.close();
        } catch (IOException e) { }
    }
}