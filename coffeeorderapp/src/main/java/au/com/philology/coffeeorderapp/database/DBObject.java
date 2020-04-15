package au.com.philology.coffeeorderapp.database;

import org.json.JSONObject;

public abstract class DBObject
{
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TIMESTAMP = "TIMESTAMP";

    public long timestamp;

    public abstract String getJsonString();

    public abstract JSONObject getJsonObject();
}
