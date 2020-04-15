package au.com.philology.coffeeorderapp.database.sync.server;

import au.com.philology.coffeeorderapp.database.DBObject;
import au.com.philology.coffeeorderapp.database.sync.DBObjectSync;

public abstract class DBObjectSyncServer extends DBObjectSync
{
    public abstract void broadcast(DBObject object);
}
