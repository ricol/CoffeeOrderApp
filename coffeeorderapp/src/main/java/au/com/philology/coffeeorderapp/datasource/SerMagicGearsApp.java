package au.com.philology.coffeeorderapp.datasource;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import au.com.philology.coffeeorderapp.common.Common;

class SerMagicGearsApp extends BasicCardReader
{
    Activity theActivity;
    BroadcastReceiver theReceiver;
    ICardReaderDelegate theDelegate;
    // declare events
    static final String SM_BCAST_SCAN = "com.restock.serialmagic.gears.action.SCAN";
    static final String SM_BCAST_RAW_DATA = "com.restock.serialmagic.gears.action.RAWDATA";
    static final String SM_BCAST_STATE_REPLY = "com.restock.serialmagic.gears.action.STATEREPLY";
    static final String SM_BCAST_FOCUS = "com.restock.serialmagic.gears.action.FOCUS";
    static final String SM_BCAST_CONNECTION = "com.restock.serialmagic.gears.action.CONNECTION";
    static final String SM_BCAST_TILT = "com.restock.serialmagic.gears.action.TILT";
    static final String RFIDReaderAddress = Common.ExternalRFIDReaderAddress;
    static final String RFIDReaderName = Common.ExternalRFIDReaderName;

    // declare event string
    static final String SM_CONNECT_GLOBAL_EVENT = "com.restock.serialmagic.gears.action.CONNECT";

    public static SerMagicGearsApp theInstance;

    public static SerMagicGearsApp getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new SerMagicGearsApp();
        return theInstance;
    }

    void unregister()
    {
        if (this.theActivity != null)
        {
            if (this.theReceiver != null)
            {
                try
                {
                    this.theActivity.unregisterReceiver(this.theReceiver);
                } catch (Exception e)
                {

                }
            }
        }

        this.theReceiver = null;
    }

    void register()
    {
        this.unregister();

        if (this.theActivity != null)
        {
            // create and register the broadcast receiver
            IntentFilter filter = new IntentFilter(SM_BCAST_SCAN);

            filter.addAction(SM_BCAST_STATE_REPLY);
            filter.addAction(SM_BCAST_CONNECTION);
            filter.addAction(SM_BCAST_FOCUS);
            filter.addAction(SM_BCAST_RAW_DATA);
            filter.addAction(SM_BCAST_TILT);

            this.theReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    String s = intent.getAction();
                    if (s.equals(SM_BCAST_SCAN))
                    {
                        String scan = intent.getStringExtra("scan");
                        // do something with the scan data
                        if (theDelegate != null)
                            theDelegate.CardReaderTagDetected("External Reader", scan);
                    }
                    // if (s.equals(SM_BCAST_RAWDATA))
                    // {
                    // byte[] baData = intent.getByteArrayExtra("data");
                    // // do something with the raw data
                    // } else
                    if (s.equals(SM_BCAST_STATE_REPLY))
                    {
                        String state = intent.getStringExtra("state");
                        String device = intent.getStringExtra("device");
                        // do something with the device name and state
                        if (theDelegate != null)
                            theDelegate.CardReaderStateChanged("state: " + state + "; device: " + device);
                    } else if (s.equals(SM_BCAST_FOCUS))
                    {
                        // do something
                        if (theDelegate != null)
                            theDelegate.CardReaderStateChanged("FOCUS.");
                    } else if (s.equals(SM_BCAST_CONNECTION))
                    {
                        String state = intent.getStringExtra("state");
                        // do something with the state

                        if (state.equalsIgnoreCase("Connected"))
                        {
                            if (theDelegate != null)
                                theDelegate.CardReaderConnected("External Reader Connected.");
                        } else if (state.equalsIgnoreCase("Fail") || state.equalsIgnoreCase("Disconnected"))
                        {
                            if (theDelegate != null)
                                theDelegate.CardReaderLostConnection("External Reader Disconnected!");
                        }
                    } else if (s.equals(SM_BCAST_TILT))
                    {
                        Bundle bundle = intent.getExtras();
                        String message = bundle.getString("tilt");
                        String name = bundle.getString("name");
                        String addr = bundle.getString("addr");
                        if (theDelegate != null)
                            theDelegate.CardReaderStateChanged("TILT: " + "message: " + message + "; name: " + name + "; addr: " + addr);
                    }
                }
            };

            this.theActivity.registerReceiver(this.theReceiver, filter);
        }
    }

    @Override
    public void connect()
    {
        super.connect();

        if (this.theDelegate != null)
            this.theDelegate.CardReaderTryToConnect("Connect to External Reader...");

        Intent i = new Intent(SM_CONNECT_GLOBAL_EVENT);

        i.putExtra("address", RFIDReaderAddress);
        i.putExtra("action", "connect");
        i.putExtra("name", RFIDReaderName);

        if (theActivity != null)
            theActivity.sendBroadcast(i);
    }

    @Override
    public void disconnect()
    {
        super.disconnect();

        if (this.theDelegate != null)
            this.theDelegate.CardReaderTryToDisconnect("Disconnect External Reader...");

        Intent i = new Intent(SM_CONNECT_GLOBAL_EVENT);

        i.putExtra("address", RFIDReaderAddress);
        i.putExtra("action", "disconnect");
        i.putExtra("name", RFIDReaderName);

        if (theActivity != null)
            theActivity.sendBroadcast(i);
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
        this.register();
    }

    @Override
    public void activityOnPause()
    {
        super.activityOnPause();
        this.unregister();
    }

    @Override
    public void resolveIntent(Intent theIntent)
    {
        super.resolveIntent(theIntent);

        if (this.theDelegate != null)
            this.theDelegate.CardReaderOperationCancelled("External Reader Operation Cancelled.");
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
}
