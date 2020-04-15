package au.com.philology.coffeeorderapp.datasource;

import android.app.Activity;
import android.content.Intent;

public interface ICardReader
{
    public void setCardReaderDelegate(ICardReaderDelegate delegate);

    public void setActivity(Activity activity);

    public void activityOnResume();

    public void activityOnPause();

    public void resolveIntent(Intent theIntent);

    public void connect();

    public void disconnect();

    public Activity getTheActivity();

    public ICardReaderDelegate getTheDelegate();
}
