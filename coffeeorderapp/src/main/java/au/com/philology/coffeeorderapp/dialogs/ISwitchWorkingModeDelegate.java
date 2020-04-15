package au.com.philology.coffeeorderapp.dialogs;

import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;

public interface ISwitchWorkingModeDelegate
{
    public void switchWorkingModeDialogModeChanged(TypeWorkingMode mode);

    public void switchWorkingModeDialogModeChangingFailed(TypeWorkingMode targetMode);
}
