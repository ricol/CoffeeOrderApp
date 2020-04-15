package au.com.philology.coffeeorderapp.dialogs;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.Server;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;

public class SwitchingToServerModeDialog extends SwitchingWorkingModeDialog
{

    public SwitchingToServerModeDialog(Activity activity, TextView tvMsg, ProgressBar theProgressBar, View theContentView, ISwitchWorkingModeDelegate delegate, boolean bSilent)
    {
        super(activity, tvMsg, theProgressBar, theContentView, delegate, bSilent);
        mode = TypeWorkingMode.SERVER_MODE;
        theProgressBar.setMax(1);
    }

    public void changeMode()
    {
        reset();
        updateMsg("Initializing as a server...");
        Common.sleep(DELAY);
        TheTCPServer.getSharedInstance().startListening(Server.PORT);
        updateProgressBarValue(1);
        Common.sleep(DELAY);
        this.complete();
    }
}
