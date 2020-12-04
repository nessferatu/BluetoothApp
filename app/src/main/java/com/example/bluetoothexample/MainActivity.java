package com.example.bluetoothexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button b1,b2,b3,b4,b5;
    private BluetoothAdapter BA;
    private BluetoothDevice device;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String>  mAdapter;
    private BroadcastReceiver mReceiver;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button4);
        b5 = findViewById(R.id.button5);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = findViewById(R.id.listView);





    }


    ///////////////////////////////
    public void discover(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }



        // If another discovery is in progress, cancels it before starting the new one.
        if (BA.isDiscovering()) {
            Toast.makeText(getApplicationContext(),"Already scanning.",Toast.LENGTH_SHORT).show();
            BA.cancelDiscovery();
        }

        //Start scanning for devices.
        BA.startDiscovery();

        //Find paired devices first
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();


        for(BluetoothDevice bt: pairedDevices){

            list.add(bt.getName()+"\nSignal: dBm"+"\nMAC: "+bt.getAddress());
        }

        Toast.makeText(getApplicationContext(),"Scanning for devices.",Toast.LENGTH_SHORT).show();

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                String action = intent.getAction();

                //Finding unpaired devices
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    // Get the BluetoothDevice object from the Intent
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the BluetoothDevice device class
                   // bluetoothClass = device.getBluetoothClass();

                    String deviceName;

                    if(device.getName()!= null){
                        deviceName = device.getName();
                    }else{
                        deviceName = "Unknown Device";
                    }


                    mAdapter.add(deviceName +"\nSignal: "+rssi+ "dBm" +"\nMAC: "+ device.getAddress());


                }

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    Toast.makeText(getApplicationContext(),"Scanning complete.",Toast.LENGTH_SHORT).show();
                    unregisterReceiver(mReceiver);
                }
            }
        };




        mAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,list);
        lv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();




        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


    }

    public void on(View v){
        if(!BA.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn,0);
            Toast.makeText(getApplicationContext(),"Turned On", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(getApplicationContext(),"Already On", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned Off", Toast.LENGTH_LONG).show();
    }

    public void visible(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible,0);
    }


    public void list(View v) {
       pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt: pairedDevices){
            list.add(bt.getName()+"\n"+bt.getAddress());
        }

        Toast.makeText(getApplicationContext(),"Showing Paired Devices", Toast.LENGTH_LONG).show();

        mAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,list);
        lv.setAdapter(mAdapter);

    }
}
