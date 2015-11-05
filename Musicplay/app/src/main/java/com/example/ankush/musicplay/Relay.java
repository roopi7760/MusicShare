package com.example.ankush.musicplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Relay extends Activity {

    private BluetoothServerSocket serv_sock_relay;
    private BluetoothAdapter blue_adapter_relay;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private String NAME = "Music Share";
    private TextView conn_status_relay;
    static SharedPreferences shared_pref;
    static String msg_dis_rep = "";
    private ArrayAdapter<String> arr_adapter;
    private static ListView list_view_relay;
    public String[] pair_relay;
    //private static ThreadToBeConnected rd_wrt_relay;
    public Context c;
    public static byte[] relay_filename_byte;
    public static boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relay);
        relay_filename_byte =  new byte[1024];

        //conn_status_relay = (TextView) findViewById(R.id.textViewRelayConnection);
        blue_adapter_relay = BluetoothAdapter.getDefaultAdapter();
        shared_pref = PreferenceManager.getDefaultSharedPreferences(this);
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = blue_adapter_relay.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        serv_sock_relay = tmp;
        c=this;

        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        pair_relay = new String[10];

        list_view_relay = (ListView) findViewById(R.id.listViewRelay);
        list_view_relay.setAdapter(arr_adapter);


        if (blue_adapter_relay == null) {
            // Device does not support Bluetooth
        }
        if (!blue_adapter_relay.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        showPaired();
        list_view_relay.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //Toast toast = Toast.makeText(getBaseContext(), paired[position], 3);
                //toast.show();
                createSocket(pair_relay[position]);
            }
        });

        //Start of Thread
        /*Thread thread = new Thread(){
            @Override
            public void run() {
                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned
                while (true) {
                    try {
                        socket = serv_sock_relay.accept();

//    	                rd_wrt_relay = new ConnectedThread(socket, c);
                    } catch (IOException e) {
                        break;
                    }
                       if(socket.isConnected())
                           Toast.makeText(getBaseContext(), "connected with sender successfully", Toast.LENGTH_SHORT).show();
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate thread)
                        manageConnSocket(socket);
                        //close socket
                        try{
                            serv_sock_relay.close();
                        }
                        catch(IOException e){

                        }
                        break;
                    }
                }
            }
        };
        thread.start();*/

    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            serv_sock_relay.close();
        } catch (IOException e) { }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ///getMenuInflater().inflate(R.menu.relay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NewApi")
    protected void createSocket(String devAddr){
        BluetoothSocket mSocket = null;
        BluetoothDevice device = blue_adapter_relay.getRemoteDevice(devAddr);
        Toast toast;
        //toast = Toast.makeText(getBaseContext(), device.getName(), 1);
        //toast.show();
        try{
            mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        }
        catch(IOException e){
            toast = Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
        /*try{
            mSocket.connect();

        }
        catch(IOException closeException){
            toast = Toast.makeText(getBaseContext(), closeException.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }*/
        ConnectThread connect = new ConnectThread(device);
        ///connect.ConnectThread();
        connect.run();
        if(mSocket.isConnected())
        Toast.makeText(getBaseContext(), "connected with reciever successfully", Toast.LENGTH_SHORT).show();
        manageConnSockRelaySender(mSocket);
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
            blue_adapter_relay.cancelDiscovery();
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
            ///if(mmSocket.isConnected())
                //Toast.makeText(getBaseContext(), "connected successfully", Toast.LENGTH_SHORT).show();
            // Do work to manage the connection (in a separate thread)

            // mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }
    }

    public void manageConnSocket(BluetoothSocket socket){
        //Toast toast = Toast.makeText(getApplicationContext(), "connection accepted", Toast.LENGTH_SHORT);
        //toast.show();
        conn_status_relay.post(new Runnable() {
            public void run() {
                conn_status_relay.setText("Connected");

                //Toast.makeText(getBaseContext(), "Ready to recieve messages", Toast.LENGTH_LONG).show();
            }
        });

        ///---ThreadToBeConnected read_write = new ThreadToBeConnected(socket, this);
       //--- read_write.start();
        //	rd_wrt_relay = new ConnectedThread(socket, this);
        //String buffer = new String();
        //buffer = ;
        //Handler mHandler = new Handler(Looper.getMainLooper());
    }

    private void manageConnSockRelaySender(BluetoothSocket mSocket){
        Toast.makeText(getBaseContext(), "Ready to send messages", Toast.LENGTH_SHORT).show();
        //---rd_wrt_relay = new ThreadToBeConnected(mSocket, this);
       //--- rd_wrt_relay.write(relay_filename_byte);
        isConnected = true;
    }

    private void showPaired(){
        Set<BluetoothDevice> pairedDevices = blue_adapter_relay.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            int cnt = 0;
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                arr_adapter.add(device.getName() + "\n" + device.getAddress());
                pair_relay[cnt] = device.getAddress();
                cnt++;
            }
        }
    }

    public static void refreshMessage(byte[] messageBytes) {
        int bytesRead = 0;
        if ((bytesRead = messageBytes.length) != -1) {
           //--- rd_wrt_relay.write(messageBytes);
        }
    }

    public static void relayMessage(byte[] buffer) {
        // TODO Auto-generated method stub
        relay_filename_byte = buffer;
        if(isConnected){
           //--- rd_wrt_relay.write(relay_filename_byte);
        }
    }
}
