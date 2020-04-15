package au.com.philology.coffeeorderapp.database.sync;

import android.app.Activity;

public abstract class DBObjectSync
{
    public static Activity theContext;

    public abstract void processCmd(String cmd, String address, int port);
}
