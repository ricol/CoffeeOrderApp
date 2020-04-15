package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

public class InterestedCardReaderID extends DBObject
{
    public static final String COLUMN_INTERESTED_ID = "INTERESTED_ID";

    public String interestedId;

    static Object theObject = new Object();

    public InterestedCardReaderID(String interestedId, long timestamp)
    {
        this.interestedId = interestedId;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Interested Card Reader Id:(" + this.interestedId + ", " + this.timestamp + ")";
    }

    public static void insertInterestedId(String identifier, long timestamp)
    {
        if (identifier == null)
            return;

        if (InterestedCardReaderID.isIncluded(identifier))
            return;

        synchronized (theObject)
        {
            if (identifier != null)
            {
                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID + " (" + COLUMN_INTERESTED_ID + ", "
                                        + COLUMN_TIMESTAMP + ") VALUES (?, ?)", new Object[]
                                        {identifier, timestamp});
            }
        }
    }

    public static void removeInterestedId(String identifier)
    {
        if (identifier == null)
            return;

        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase()
                    .execSQL("DELETE FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID + " WHERE " + COLUMN_INTERESTED_ID + " = ?", new String[]
                            {identifier});
        }
    }

    public void save()
    {
        InterestedCardReaderID.removeInterestedId(this.interestedId);
        InterestedCardReaderID.insertInterestedId(this.interestedId, this.timestamp);
    }

    public static InterestedCardReaderID getObject(String identifier)
    {
        if (identifier == null)
            return null;

        synchronized (theObject)
        {
            InterestedCardReaderID result = null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID + " WHERE " + COLUMN_INTERESTED_ID + " = ?", new String[]
                            {identifier});

            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    InterestedCardReaderID aResult = new InterestedCardReaderID(theCursor.getString(1), theCursor.getLong(2));
                    result = aResult;
                    break;
                }
            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static boolean isIncluded(String identifier)
    {
        if (identifier == null)
            return true;

        synchronized (theObject)
        {
            int num = 0;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT COUNT(*) FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID + " WHERE " + COLUMN_INTERESTED_ID + " = ?", new String[]
                            {identifier});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    num = theCursor.getInt(0);
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return num > 0;
        }
    }

    public static ArrayList<InterestedCardReaderID> getAll()
    {
        synchronized (theObject)
        {
            ArrayList<InterestedCardReaderID> result = new ArrayList<InterestedCardReaderID>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    InterestedCardReaderID theObject = new InterestedCardReaderID(theCursor.getString(1), theCursor.getLong(2));
                    result.add(theObject);
                    theCursor.moveToNext();
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
        return null;
    }

    @Override
    public JSONObject getJsonObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public static int getTotal()
    {
        synchronized (theObject)
        {
            int result = 0;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    result = theCursor.getInt(0);
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static void clear()
    {
        synchronized (theObject)
        {
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID);
        }
    }
}
