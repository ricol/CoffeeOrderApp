package au.com.philology.coffeeorderapp.datasource;

import android.app.Activity;
import android.content.Intent;

public class BasicCardReader implements ICardReader
{

    @Override
    public void setCardReaderDelegate(ICardReaderDelegate delegate)
    {
    }

    @Override
    public void setActivity(Activity activity)
    {
    }

    @Override
    public void activityOnResume()
    {
    }

    @Override
    public void activityOnPause()
    {
    }

    @Override
    public void resolveIntent(Intent theIntent)
    {
    }

    @Override
    public void connect()
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public Activity getTheActivity()
    {
        return null;
    }

    @Override
    public ICardReaderDelegate getTheDelegate()
    {
        return null;
    }

}
