package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

public class CardReaderIdentifier extends DBObject
{
    public static final String COLUMN_IDENTIFIER = "IDENTIFIER";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESC = "DESC";
    public static final String COLUMN_AUTO_PRINT = "AUTO_PRINT";

    public String identifier;
    public String name;
    public String desc;
    public int autoprint;

    static Object theObject = new Object();

    public CardReaderIdentifier(String identifier, String name, String desc, int autoprint, long timestamp)
    {
        this.identifier = identifier;
        this.name = name;
        this.desc = desc;
        this.autoprint = autoprint;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Card Reader Identifier:(" + this.identifier + ", " + this.name + ", " + this.desc + ", " + this.autoprint + ", " + this.timestamp + ")";
    }

    public static void insert(String identifier, String name, String desc, int autoprint, long timestamp)
    {
        if (identifier == null || name == null || desc == null)
            return;

        if (CardReaderIdentifier.isIncluded(identifier))
            return;

        synchronized (theObject)
        {
            DB.getSharedInstance()
                    .getTheDatabase()
                    .execSQL(
                            "INSERT INTO " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS + " (" + COLUMN_IDENTIFIER + ", " + COLUMN_NAME + ", " + COLUMN_DESC + ", "
                                    + COLUMN_AUTO_PRINT + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?, ?, ?, ?)", new Object[]
                                    {identifier, name, desc, autoprint, timestamp});
        }
    }

    public static void remove(CardReaderIdentifier aObject)
    {
        CardReaderIdentifier.remove(aObject.identifier);
    }

    public static void remove(String identifier)
    {
        synchronized (theObject)
        {
            if (identifier != null)
                DB.getSharedInstance().getTheDatabase()
                        .execSQL("DELETE FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS + " WHERE " + COLUMN_IDENTIFIER + " = ?", new String[]
                                {identifier});
        }
    }

    public void save()
    {
        CardReaderIdentifier.remove(this.identifier);
        CardReaderIdentifier.insert(this.identifier, this.name, this.desc, this.autoprint, this.timestamp);
    }

    public static CardReaderIdentifier getObject(String identifier)
    {
        if (identifier == null)
            return null;

        synchronized (theObject)
        {
            CardReaderIdentifier result = null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS + " WHERE " + COLUMN_IDENTIFIER + " = ?", new String[]
                            {identifier});

            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    CardReaderIdentifier aResult = new CardReaderIdentifier(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3),
                            theCursor.getInt(4), theCursor.getLong(5));
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
                    .rawQuery("SELECT COUNT(*) FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS + " WHERE " + COLUMN_IDENTIFIER + " = ?", new String[]
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

    public static ArrayList<CardReaderIdentifier> getAll()
    {
        synchronized (theObject)
        {
            ArrayList<CardReaderIdentifier> result = new ArrayList<CardReaderIdentifier>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    CardReaderIdentifier theObject = new CardReaderIdentifier(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3),
                            theCursor.getInt(4), theCursor.getLong(5));
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

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS, null);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS);
        }
    }
}