package au.com.philology.coffeeorderapp.monitor;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.dialogs.ISwitchWorkingModeDelegate;
import au.com.philology.coffeeorderapp.dialogs.SwitchingToClientModeDialog;
import au.com.philology.coffeeorderapp.dialogs.SwitchingWorkingModeDialog;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;

public class TheWorkingModeMonitor implements ISwitchWorkingModeDelegate
{
    final int DELAY = 1000;
    static TheWorkingModeMonitor theInstance;
    Activity theActivity;
    Set<IWorkingModeMonitorDelegate> theDelegates = new HashSet<>();

    Timer theTimer;

    public static TheWorkingModeMonitor getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new TheWorkingModeMonitor();
        return theInstance;
    }

    public void startMonitoring(Activity aActivity)
    {
        this.stopMonitoring();

        this.theActivity = aActivity;
        this.showMsg("start monitoring...");

        theTimer = new Timer();
        TimerTask theTimerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                if (SwitchingWorkingModeDialog.bInProgress)
                {
                    TheWorkingModeMonitor.this.showMsg("Switching working mode at the moment...ignore the current excution.");
                    return;
                }

                TypeWorkingMode mode = WorkingMode.getWorkingMode();

                if (mode == TypeWorkingMode.CLIENT_MODE)
                {
                    TheWorkingModeMonitor.this.showMsg("Checking mode...Client mode...");

                    if (!TheTCPClient.getSharedInstance().isConnected())
                    {

                        TheWorkingModeMonitor.this.showMsg("Switch to ClientMode...");
                        TheWorkingModeMonitor.this.runOnUIThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Activity theActivity = TheWorkingModeMonitor.this.theActivity;
                                ISwitchWorkingModeDelegate theDelegate = TheWorkingModeMonitor.this;
                                LayoutInflater factory = LayoutInflater.from(TheWorkingModeMonitor.this.theActivity);
                                View textView = factory.inflate(R.layout.progress_dialog_message, null);
                                TextView tvMsg = (TextView) textView.findViewById(R.id.tvMessage);
                                ProgressBar pb = (ProgressBar) textView.findViewById(R.id.progressBar);
                                SwitchingWorkingModeDialog aDialog = new SwitchingToClientModeDialog(theActivity, tvMsg, pb, textView, theDelegate, true);
                                aDialog.start();
                            }
                        });
                    }
                } else if (mode == TypeWorkingMode.SERVER_MODE)
                {
                    TheWorkingModeMonitor.this.showMsg("Checking mode...Server mode. No Action required.");
                } else if (mode == TypeWorkingMode.SINGLE_MODE)
                {
                    TheWorkingModeMonitor.this.showMsg("Checking mode...Single mode. No Action required.");
                }
            }
        };

        int seconds = Model.getAutoMonitoringWorkingModePeriod(theActivity);
        int milliseconds = seconds * 1000;
        theTimer.scheduleAtFixedRate(theTimerTask, milliseconds, milliseconds);
        DebugToast.show(theActivity, "Monitoring Timer: " + seconds + " second" + (seconds > 1 ? "s." : "."), Toast.LENGTH_LONG);
    }

    public void stopMonitoring()
    {
        this.showMsg("stop monitoring.");

        this.theActivity = null;

        if (theTimer == null)
            return;

        theTimer.cancel();
        theTimer = null;
    }

    public void addDelegate(IWorkingModeMonitorDelegate aDelegate)
    {
        this.theDelegates.add(aDelegate);
    }

    public void removeDelegate(IWorkingModeMonitorDelegate aDelegate)
    {
        this.theDelegates.remove(aDelegate);
    }

    void showMsg(String msg)
    {
        final String MSG = msg;

        this.runOnUIThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.i("DEBUG", MSG);
                if (TheWorkingModeMonitor.this.theActivity != null)
                    DebugToast.show(TheWorkingModeMonitor.this.theActivity, MSG, Toast.LENGTH_SHORT);
            }
        });
    }

    void runOnUIThread(Runnable theRunnable)
    {
        if (this.theActivity == null || theRunnable == null)
            return;
        this.theActivity.runOnUiThread(theRunnable);
    }

    @Override
    public void switchWorkingModeDialogModeChanged(TypeWorkingMode mode)
    {
        if (mode == TypeWorkingMode.CLIENT_MODE)
        {
            this.showMsg("Complete.");
        }
    }

    @Override
    public void switchWorkingModeDialogModeChangingFailed(TypeWorkingMode targetMode)
    {
        if (targetMode == TypeWorkingMode.CLIENT_MODE)
        {
            this.showMsg("Switch to Client mode failed!");
        }
    }
}
