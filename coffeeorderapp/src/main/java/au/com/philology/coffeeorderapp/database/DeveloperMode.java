package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONObject;

public class DeveloperMode extends DBObject
{

    public static final String COLUMN_DEVELOPER_MODE = "DEVELOPER_MODE";

    public boolean developerMode;

    static Object theObject = new Object();

    public DeveloperMode(boolean developerMode, long timestamp)
    {
        this.developerMode = developerMode;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Developer Mode:(" + this.developerMode + ", " + this.timestamp + ")";
    }

    public static void update(boolean developerMode, long timestamp)
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_DEVELOPER_MODE);

            DB.getSharedInstance()
                    .getTheDatabase()
                    .execSQL("INSERT INTO " + DB.TABLE_NAME_DEVELOPER_MODE + "(" + COLUMN_DEVELOPER_MODE + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)",
                            new Object[]
                                    {developerMode ? 1 : 0, timestamp});
        }
    }

    public static boolean isDeveloperMode()
    {
        synchronized (theObject)
        {
            int result = 0;
            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_DEVELOPER_MODE, new String[]
                    {});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    int tmpResult = theCursor.getInt(1);
                    result = tmpResult;
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return result == 1;
        }
    }

    public static void clear()
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_DEVELOPER_MODE);
        }
    }

    @Override
    public String getJsonString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject getJsonObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
