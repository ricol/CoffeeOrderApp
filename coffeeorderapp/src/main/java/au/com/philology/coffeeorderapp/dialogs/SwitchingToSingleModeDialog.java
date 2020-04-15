package au.com.philology.coffeeorderapp.dialogs;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;

public class SwitchingToSingleModeDialog extends SwitchingWorkingModeDialog
{

    public SwitchingToSingleModeDialog(Activity activity, TextView tvMsg, ProgressBar theProgressBar, View theContentView, ISwitchWorkingModeDelegate delegate, boolean bSilent)
    {
        super(activity, tvMsg, theProgressBar, theContentView, delegate, bSilent);
        mode = TypeWorkingMode.SINGLE_MODE;
        theProgressBar.setMax(1);
        this.theProgressBar.setVisibility(View.VISIBLE);
    }

    public void changeMode()
    {
        reset();
        updateMsg("Initializing as single mode...");
        Common.sleep(DELAY);
        updateProgressBarValue(1);
        Common.sleep(DELAY);
        this.complete();
    }
}
