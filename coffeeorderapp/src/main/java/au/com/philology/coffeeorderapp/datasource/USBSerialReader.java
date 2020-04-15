package au.com.philology.coffeeorderapp.datasource;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.services.usbserial.UsbService;

public class USBSerialReader extends BasicCardReader
{
    Activity theActivity;
    MyHandler mHandler = new MyHandler();
    ICardReaderDelegate theDelegate;
    UsbService theUsbService;
    public static USBSerialReader theInstance;

    public static USBSerialReader getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new USBSerialReader();
        return theInstance;
    }

    @Override
    public void connect()
    {
        super.connect();

        this.stopService();
        this.startService();

        if (this.theDelegate != null)
            this.theDelegate.CardReaderTryToConnect("Connect USB Serial Reader...");
    }

    @Override
    public void disconnect()
    {
        super.disconnect();

        this.stopService();

        if (this.theDelegate != null)
            this.theDelegate.CardReaderTryToDisconnect("Disconnect USB Serial Reader...");

    }

    @Override
    public void setCardReaderDelegate(ICardReaderDelegate delegate)
    {
        super.setCardReaderDelegate(delegate);
        this.theDelegate = delegate;
    }

    @Override
    public void setActivity(Activity activity)
    {
        super.setActivity(activity);
        this.theActivity = activity;
    }

    @Override
    public void activityOnResume()
    {
        super.activityOnResume();

        this.startService();
    }

    @Override
    public void activityOnPause()
    {
        super.activityOnPause();

        this.stopService();
    }

    @Override
    public void resolveIntent(Intent theIntent)
    {
        super.resolveIntent(theIntent);

        if (this.theDelegate != null)
            this.theDelegate.CardReaderOperationCancelled("USB Serial Reader Operation Cancelled.");
    }

    @Override
    public Activity getTheActivity()
    {
        // TODO Auto-generated method stub
        return this.theActivity;
    }

    @Override
    public ICardReaderDelegate getTheDelegate()
    {
        // TODO Auto-generated method stub
        return this.theDelegate;
    }

    private void startService()
    {
        setFilters(); // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start
        // UsbService(if
        // it was not
        // started
        // before) and
        // Bind it
    }

    private void stopService()
    {
        try
        {
            Intent aIntent = new Intent(theActivity, UsbService.class);
            theActivity.stopService(aIntent);
            theActivity.unregisterReceiver(mUsbReceiver);
            theActivity.unbindService(usbConnection);
        } catch (Exception e)
        {
            Log.e("DEBUG", "Exception: " + e);
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            String text = "BroadcastReceiver.onReceive..." + arg1;
            Log.i("DEBUG", text);

            if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_GRANTED)) // USB
            // PERMISSION
            // GRANTED
            {
                String msg = "USB Ready";
                DebugToast.show(arg0, msg, Toast.LENGTH_SHORT);
                if (theDelegate != null)
                    theDelegate.CardReaderStateChanged(msg);
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)) // USB
            // PERMISSION
            // NOT
            // GRANTED
            {
                String msg = "USB Permission not granted";
                Toast.makeText(arg0, msg, Toast.LENGTH_SHORT).show();
                if (theDelegate != null)
                    theDelegate.CardReaderStateChanged(msg);
            } else if (arg1.getAction().equals(UsbService.ACTION_NO_USB)) // NO
            // USB
            // CONNECTED
            {
                String msg = "No USB connected";
                DebugToast.show(arg0, msg, Toast.LENGTH_SHORT);
                if (theDelegate != null)
                    theDelegate.CardReaderStateChanged(msg);
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_DISCONNECTED)) // USB
            // DISCONNECTED
            {
                String msg = "USB disconnected";
                DebugToast.show(arg0, msg, Toast.LENGTH_SHORT);
                if (theDelegate != null)
                    theDelegate.CardReaderStateChanged(msg);
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_NOT_SUPPORTED)) // USB
            // NOT
            // SUPPORTED
            {
                String msg = "USB device not supported";
                Toast.makeText(arg0, msg, Toast.LENGTH_SHORT).show();
                if (theDelegate != null)
                    theDelegate.CardReaderStateChanged(msg);
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1)
        {
            String text = "ServiceConnection.onServiceConnected..." + "ComponentName: " + arg0 + "; IBinder: " + arg1;
            Log.i("DEBUG", text);
            if (theDelegate != null)
                theDelegate.CardReaderStateChanged("USB Serial Service Connected.");
            theUsbService = ((UsbService.UsbBinder) arg1).getService();
            theUsbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            String text = "ServiceConnection.onServiceDisconnected..." + "ComponentName: " + arg0;
            Log.i("DEBUG", text);
            if (theDelegate != null)
                theDelegate.CardReaderStateChanged("USB Serial Service Disconnected.");
            theUsbService = null;
        }
    };

    private class MyHandler extends Handler
    {
        private String aTag = "";

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    Log.i("DEBUG", "Data: " + data);
                    if (theActivity != null)
                        DebugToast.show(theActivity, "Raw Data From USB-Serial: " + data, Toast.LENGTH_LONG);
                    if (data.trim().equalsIgnoreCase(""))
                        break;

                    aTag = data.replace(Character.toString((char) 0x0d), "");
                    tagDetected(aTag);

                    break;
            }
        }

        void tagDetected(String tag)
        {
            if (theDelegate != null)
                theDelegate.CardReaderTagDetected("USB Serial Reader", tag);
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras)
    {
        if (theActivity == null)
            return;

        if (UsbService.SERVICE_CONNECTED == false)
        {
            Intent startService = new Intent(theActivity, service);
            if (extras != null && !extras.isEmpty())
            {
                Set<String> keys = extras.keySet();
                for (String key : keys)
                {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            theActivity.startService(startService);
        }
        Intent bindingIntent = new Intent(theActivity, service);
        theActivity.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters()
    {
        if (theActivity == null)
            return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        theActivity.registerReceiver(mUsbReceiver, filter);
    }
}
