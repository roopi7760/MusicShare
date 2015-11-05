package com.example.ankush.musicplay;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Sender extends AppCompatActivity {
    public BluetoothAdapter ba;
    public String[] pairlist;
    private ListView plist;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PICK_FILE = 1;
    boolean start_play = false;
   // private String devadd="58:A2:B5:1A:BC:6C";
    private ArrayAdapter<String> array_adapter;
   // private static ThreadToBeConnected rd_wrt;
    private ArrayList<File> fileList = new ArrayList<File>();
    String flname = "";
    static String allFiles = "";
    File rootFile;
    boolean send_file = false;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ba=BluetoothAdapter.getDefaultAdapter();
        pairlist = new String[10];

        array_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        plist= (ListView)findViewById(R.id.lv_pair);
        plist.setAdapter(array_adapter);
        rootFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

       // allFiles = getfile(rootFile);

        if (ba == null) {

            // / Device does not support Bluetooth
        }
        if (!ba.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        showpairlist();
        plist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //Toast toast = Toast.makeText(getBaseContext(), pairlist[position], 3);
                //toast.show();
                createSocket(pairlist[position]);
            }
        });

    }
    @SuppressLint("NewApi")
    protected void createSocket(String devAddr) {
        BluetoothSocket mSocket = null;
        BluetoothDevice device = ba.getRemoteDevice(devAddr);
        Toast toast;
        //toast = Toast.makeText(getBaseContext(), device.getName(), 1);
        //toast.show();
        try{
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch(IOException e){
            toast = Toast.makeText(getBaseContext(),"create error", Toast.LENGTH_SHORT);
            toast.show();
        }
       //toast = Toast.makeText(getBaseContext(),"create error", Toast.LENGTH_SHORT);
        ///toast.show();
        ConnectThread connect = new ConnectThread(device);
        ///connect.ConnectThread();
        connect.run();

        /*try{
            /*ba.cancelDiscovery();
            TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String uuid = tManager.getDeviceId();
            mSocket.connect();
        }
        catch(IOException closeException){
                toast = Toast.makeText(getBaseContext(),"connect() error", Toast.LENGTH_SHORT);

                mSocket.isConnected();
                toast.show();
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        //manageConnectedSocket(mSocket);
        //BluetoothSocket mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            // Log.i(tag, "construct");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                // Log.i(tag, "get socket failed");

            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            ba.cancelDiscovery();
            //Log.i(tag, "connect - run");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                // Toast.makeText(getBaseContext(), "connected", Toast.LENGTH_SHORT).show();
                //Log.i(tag, "connect - succeeded");
            } catch (IOException connectException) {        //Log.i(tag, "connect failed");
                Toast.makeText(getBaseContext(), "connect error", Toast.LENGTH_SHORT).show();
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            if(mmSocket.isConnected())
                Toast.makeText(getBaseContext(), "connected successfully", Toast.LENGTH_SHORT).show();
            // Do work to manage the connection (in a separate thread)

            // mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }
    }

    private void manageConnectedSocket(BluetoothSocket mSocket){
        Toast.makeText(getBaseContext(), "Ready to send messages", Toast.LENGTH_SHORT).show();
       // rd_wrt = new ThreadToBeConnected(mSocket, this);

//        rd_wrt.writeFileNames(allFiles.getBytes());

        int sec = (int) System.currentTimeMillis();
        int newsec = 0;
        while(newsec != sec+30000)
        {
            newsec = (int) System.currentTimeMillis();
        }

//		rd_wrt.receiveFileName();
        //fileNameSendAgain();

//		while(true){
//			if(send_file){
//				break;
//			}
//		}
    }
    public void sendFile(View view) {

        send_file = true;
        start_play = true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case REQUEST_PICK_FILE:

                    break;
            }
        }

    }
    private void showpairlist(){
        Set<BluetoothDevice> pairlistDevices = ba.getBondedDevices();
        // If there are pairlist devices
        if (pairlistDevices.size() > 0) {
            // Loop through pairlist devices
            int cnt = 0;
            for (BluetoothDevice device : pairlistDevices) {
                // Add the name and address to an array adapter to show in a ListView
                array_adapter.add(device.getName() + "\n" + device.getAddress());
                pairlist[cnt] = device.getAddress();
                cnt++;
            }
        }
    }

}
