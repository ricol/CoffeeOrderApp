package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONObject;

public class CloudOrder extends DBObject
{
    public static final String COLUMN_CLOUD_ORDER = "CLOUR_ORDER";

    public boolean cloudOrder;

    static Object theObject = new Object();

    public CloudOrder(boolean cloudOrder, long timestamp)
    {
        this.cloudOrder = cloudOrder;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Cloud Order:(" + this.cloudOrder + ", " + this.timestamp + ")";
    }

    public static void updateCloudOrder(boolean cloudOrder, long timestamp)
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_CLOUD_ORDERS);

            DB.getSharedInstance().getTheDatabase()
                    .execSQL("INSERT INTO " + DB.TABLE_NAME_CLOUD_ORDERS + "(" + COLUMN_CLOUD_ORDER + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)", new Object[]
                            {cloudOrder ? 1 : 0, timestamp});
        }
    }

    public static boolean getCloudOrders()
    {
        synchronized (theObject)
        {
            int result = 0;
            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_CLOUD_ORDERS, new String[]
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_CLOUD_ORDERS);
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
