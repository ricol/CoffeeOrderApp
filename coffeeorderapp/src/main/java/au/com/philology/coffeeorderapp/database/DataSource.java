package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TagDataSourceType;

public class DataSource extends DBObject
{
    public static final String COLUMN_RFID_READER_TYPE = "RFID_READER_TYPE";

    public TagDataSourceType readerType;
    static Object theObject = new Object();

    public DataSource(TagDataSourceType readerType, long timestamp)
    {
        this.readerType = readerType;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "DataSource:(" + this.readerType + ", " + this.timestamp + ")";
    }

    public static void updateRFIDReaderType(TagDataSourceType readerType, long timestamp)
    {
        synchronized (theObject)
        {
            int intmode = Common.ReaderTypeToInt(readerType);
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_DATA_SOURCE);

            DB.getSharedInstance()
                    .getTheDatabase()
                    .execSQL("INSERT INTO " + DB.TABLE_NAME_DATA_SOURCE + "(" + COLUMN_RFID_READER_TYPE + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)",
                            new Object[]
                                    {intmode, timestamp});
        }
    }

    public static TagDataSourceType getReaderType()
    {
        synchronized (theObject)
        {
            TagDataSourceType result = TagDataSourceType.EXTERNAL_READER;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_DATA_SOURCE, new String[]
                    {});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    int tmpResult = theCursor.getInt(1);
                    TagDataSourceType mode = Common.IntToReaderType(tmpResult);
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
            jsonObject.put(COLUMN_RFID_READER_TYPE, this.readerType);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_DATA_SOURCE);
        }
    }

}
