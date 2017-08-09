package com.google.blockly.android.demo.Wifi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by HeTingwei on 2017/8/9.
 */

public class ConnectionWifiThread extends Thread {

    byte[] bytes;
    Handler handler;

    private static final String TAG = "ConnectionWifiThread";

    Socket socket;
    Message msg;

    public ConnectionWifiThread(byte[] bytes, android.os.Handler handler) {
        this.bytes = bytes;
        this.handler = handler;
        msg = new Message();
    }

    @Override
    public void run() {
        super.run();
        Log.e(TAG, "run:0000000 ");
        try {
            socket = new Socket("192.168.4.1", 8089);
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bo = new BufferedOutputStream(os);
            bo.write(bytes);
            bo.flush();
            socket.close();
            msg.what = 1;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }


}
