package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.Date;

public class CloudOrderFilter extends DBObject
{
    public static enum Filter_Type
    {
        FILTER_ALL, FILTER_INTERESTED
    }

    ;

    public static final String COLUMN_CLOUD_ORDER_FILTER = "CLOUR_ORDER_FILTER";

    public boolean cloudOrderFilter;

    static Object theObject = new Object();

    public CloudOrderFilter(boolean cloudOrderFilter, long timestamp)
    {
        this.cloudOrderFilter = cloudOrderFilter;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Cloud Order Filter:(" + this.cloudOrderFilter + ", " + this.timestamp + ")";
    }

    public static void updateCloudOrderFilter(Filter_Type filterType, long timestamp)
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_CLOUD_ORDER_FILTER);

            DB.getSharedInstance()
                    .getTheDatabase()
                    .execSQL("INSERT INTO " + DB.TABLE_NAME_CLOUD_ORDER_FILTER + "(" + COLUMN_CLOUD_ORDER_FILTER + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)",
                            new Object[]
                                    {filterType == Filter_Type.FILTER_INTERESTED ? 1 : 0, timestamp});
        }
    }

    public static Filter_Type getCloudOrderFilter()
    {
        synchronized (theObject)
        {
            int result = 0;
            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_CLOUD_ORDER_FILTER, new String[]
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

            return result == 1 ? Filter_Type.FILTER_INTERESTED : Filter_Type.FILTER_ALL;
        }
    }

    public static void clear()
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_CLOUD_ORDER_FILTER);
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

    public static void filterAll()
    {
        CloudOrderFilter.updateCloudOrderFilter(Filter_Type.FILTER_ALL, new Date().getTime());
    }

    public static void filterInterested()
    {
        CloudOrderFilter.updateCloudOrderFilter(Filter_Type.FILTER_INTERESTED, new Date().getTime());
    }

    public static boolean isFilterInterested()
    {
        boolean bResult = CloudOrderFilter.getCloudOrderFilter() == Filter_Type.FILTER_INTERESTED;
        return bResult;
    }
}
