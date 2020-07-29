package com.sparkle.roam.services;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestReceiver extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
    String name = intent.getAction();
    String device = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
    String rssi_msg = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
    Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
  }

}