package com.eklanku.otuChat.btprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public final class btConnection extends iConnection
{

 public btConnection()
 {
     _btAddress = "";
     _errorMessage = "";
     _btName = "";
     _isConnected = false;
     _btAdapter = null;
     device = null;
     _printerSocket = null;
 }

 public void Address(String Address)
 {
     _btAddress = Address;
 }

 public void PrinterName(String Name)
 {
     _btName = Name;
 }

 public String ErrorMessage()
 {
     return _errorMessage;
 }

 public boolean Connect()
     throws IOException
 {
     return Connect2Device();
 }

 @SuppressWarnings("rawtypes")
private boolean Connect2Device()
 {
     try
     {
         _btAdapter = BluetoothAdapter.getDefaultAdapter();
         _isConnected = false;
         _errorMessage = "";
         if(_btAdapter != null)
         {
             if(!_btAdapter.isEnabled())
                 _btAdapter.enable();
             if(_btAdapter.isEnabled())
             {
                 if(_btAddress == "" && _btName != "")
                 {
                     for(Iterator iterator = _btAdapter.getBondedDevices().iterator(); iterator.hasNext();)
                     {
                         BluetoothDevice _device = (BluetoothDevice)iterator.next();
                         if(_device.getName().trim().equals(_btName.trim()))
                             _btAddress = _device.getAddress();
                     }

                 }
                 device = _btAdapter.getRemoteDevice(_btAddress);
                 _printerSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                 _btAdapter.cancelDiscovery();
                 _isConnected = Connect2Socket();
             }
         }
     }
     catch(Exception e)
     {
         _isConnected = false;
         _errorMessage = e.getMessage();
     }
     return _isConnected;
 }

 private boolean Connect2Socket()
 {
     try
     {
         if(_printerSocket != null)
         {
             _printerSocket.connect();
             _isConnected = true;
         }
     }
     catch(Exception e)
     {
         _isConnected = false;
         _errorMessage = "Mobile Printer tidak terhubung !";
     }
     return _isConnected;
 }

 public boolean Disconnect()
 {
     boolean _disconnected = false;
     try
     {
         _printerSocket.close();
         _disconnected = true;
         _printerSocket = null;
     }
     catch(IOException e)
     {
         _disconnected = false;
     }
     return _disconnected;
 }

 public boolean isConnected()
 {
     return _isConnected;
 }

 private String _btAddress;
 private String _errorMessage;
 private String _btName;
 private boolean _isConnected;
 BluetoothAdapter _btAdapter;
 BluetoothDevice device;
 protected BluetoothSocket _printerSocket;
}