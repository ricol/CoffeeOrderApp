package au.com.philology.coffeeorderapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClientDelegate;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServerDelegate;

public abstract class SwitchingWorkingModeDialog
{
    final int DELAY = 1000;
    AlertDialog mProgressDialog;
    Activity theActivity;
    public ISwitchWorkingModeDelegate theDelegate;
    View theContentView;
    TextView theTextViewMsg;
    ProgressBar theProgressBar;
    TypeWorkingMode mode;
    boolean bSilent = false;
    public static boolean bInProgress = false;
    public static boolean bStop = false;

    public SwitchingWorkingModeDialog(Activity activity, TextView tvMsg, ProgressBar theProgressBar, View theContentView, ISwitchWorkingModeDelegate delegate,
                                      boolean bSilent)
    {
        this.theActivity = activity;
        this.theTextViewMsg = tvMsg;
        this.theProgressBar = theProgressBar;
        this.theProgressBar.setVisibility(View.INVISIBLE);
        this.theContentView = theContentView;
        this.theDelegate = delegate;
        this.bSilent = bSilent;

        if (!this.bSilent)
        {
            mProgressDialog = new AlertDialog.Builder(theActivity).setIconAttribute(android.R.attr.alertDialogIcon).setTitle("Progress")
                    .setView(theContentView).setNeutralButton("Skip", null).create();
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnShowListener(new DialogInterface.OnShowListener()
            {

                @Override
                public void onShow(DialogInterface dialog)
                {
                    Button b = mProgressDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    b.setOnClickListener(new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {
                            skipCurrentProcess();
                        }
                    });
                }
            });
        }
    }

    void skipCurrentProcess()
    {

    }

    void changeMode()
    {

    }

    void reset()
    {
        TheTCPClient.getSharedInstance().stopScaningIpForPort();
        TheTCPClient.getSharedInstance().stopScanningPortForIp();
        TheTCPClient.getSharedInstance().disconnect();
        TheTCPClient.getSharedInstance().clientTestDelegate = null;
        TheTCPClientDelegate.getSharedInstance().scanDelegate = null;
        TheTCPClientDelegate.getSharedInstance().clientDelegate = null;

        TheTCPServer.getSharedInstance().stopListenning();
        TheTCPServer.getSharedInstance().stopScaningIpForPort();
        TheTCPServer.getSharedInstance().stopScanningPortForIp();
        TheTCPServerDelegate.getSharedInstance().clearAllDelegates();
    }

    void complete()
    {
        TheTCPClient.getSharedInstance().clientTestDelegate = null;
        TheTCPClientDelegate.getSharedInstance().clientDelegate = null;
        TheTCPClientDelegate.getSharedInstance().scanDelegate = null;

        WorkingMode.updateWorkingMode(mode, (new Date()).getTime());

        this.theActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                if (!SwitchingWorkingModeDialog.this.bSilent)
                    mProgressDialog.dismiss();
                if (theDelegate != null)
                    theDelegate.switchWorkingModeDialogModeChanged(mode);
                SwitchingWorkingModeDialog.bInProgress = false;
            }
        });
    }

    void updateMsg(String msg, int value)
    {
        if (this.bSilent)
            return;

        final String tmpMsg = msg;
        final int tmpValue = value;
        this.theActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                theTextViewMsg.setText(tmpMsg);
                theProgressBar.setProgress(tmpValue);
            }
        });
    }

    void updateMsg(String msg)
    {
        if (this.bSilent)
            return;

        final String tmpMsg = msg;
        this.theActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                theTextViewMsg.setText(tmpMsg);
            }
        });
    }

    void updateProgressBarValue(int value)
    {
        if (this.bSilent)
            return;

        final int tmpValue = value;
        this.theActivity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                theProgressBar.setProgress(tmpValue);
            }
        });
    }

    void runOnUIThread(Runnable theRunnale)
    {
        this.theActivity.runOnUiThread(theRunnale);
    }

    public void start()
    {
        if (!this.bSilent)
            mProgressDialog.show();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SwitchingWorkingModeDialog.bInProgress = true;
                changeMode();
            }
        }).start();
    }

    public static void requestStop()
    {
        SwitchingWorkingModeDialog.bStop = true;
    }
}
