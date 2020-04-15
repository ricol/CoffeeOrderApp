package au.com.philology.coffeeorderapp.database.sync.client;

import au.com.philology.coffeeorderapp.database.DBObject;
import au.com.philology.coffeeorderapp.database.sync.DBObjectSync;

public abstract class DBObjectSyncClient extends DBObjectSync
{
    public abstract void push(DBObject object);

    public abstract void pushWithBroadcastRequest(DBObject object);

    public abstract void pushAll();

    public abstract void requestAll();
}
