package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;

public class WorkingMode extends DBObject
{
    public static final String COLUMN_WORKING_MODE = "WORKING_MODE";

    public int working_mode;

    static Object theObject = new Object();

    public WorkingMode(int working_mode, long timestamp)
    {
        this.working_mode = working_mode;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "WorkingMode:(" + this.working_mode + ", " + this.timestamp + ")";
    }

    public static void updateWorkingMode(TypeWorkingMode mode, long timestamp)
    {
        synchronized (theObject)
        {
            int intmode = Common.WorkingModeToInt(mode);
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_WORKMODE);

            DB.getSharedInstance().getTheDatabase()
                    .execSQL("INSERT INTO " + DB.TABLE_NAME_WORKMODE + "(" + COLUMN_WORKING_MODE + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)", new Object[]
                            {intmode, timestamp});
        }
    }

    public static TypeWorkingMode getWorkingMode()
    {
        synchronized (theObject)
        {
            TypeWorkingMode result = TypeWorkingMode.SINGLE_MODE;
            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_WORKMODE, new String[]
                    {});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    int tmpResult = theCursor.getInt(1);
                    TypeWorkingMode mode = Common.IntToWorkingMode(tmpResult);
                    result = mode;
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    @Override
    public String getJsonString()
    {
        // TODO Auto-generated method stub

        return this.getJsonObject().toString();
    }

    @Override
    public JSONObject getJsonObject()
    {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(COLUMN_WORKING_MODE, this.working_mode);
            jsonObject.put(COLUMN_TIMESTAMP, this.timestamp);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static void clear()
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_WORKMODE);
        }
    }
}
