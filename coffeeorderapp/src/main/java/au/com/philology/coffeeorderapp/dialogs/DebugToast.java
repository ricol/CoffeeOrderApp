package au.com.philology.coffeeorderapp.dialogs;

import android.content.Context;
import android.widget.Toast;

import au.com.philology.coffeeorderapp.database.DeveloperMode;

public class DebugToast
{
    public static void show(Context theContext, String msg, int time)
    {
        if (DeveloperMode.isDeveloperMode())
        {
            Toast.makeText(theContext, msg, time).show();
        }
    }
}
