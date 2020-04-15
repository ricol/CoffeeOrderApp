package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoffeeType extends DBObject
{
    public static final String COLUMN_COFFEE_ID = "COFFEE_ID";
    public static final String COLUMN_LABEL = "LABEL";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_IMAGE = "IMAGE";
    public static final String COLUMN_PRICE = "PRICE";

    public String id;
    public String label;
    public String description;
    public String image;
    public float price;
    static Object theObject = new Object();

    public CoffeeType(String coffeeid, String coffeelabel, String coffeedescription, float price, String coffeeimage, long timestamp)
    {
        this.id = coffeeid;
        this.description = coffeedescription;
        this.label = coffeelabel;
        this.price = price;
        this.image = coffeeimage;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "CoffeeType:(" + this.id + ", " + this.label + ", " + this.description + ", " + this.price + ", " + this.image + ", " + this.timestamp + ")";
    }

    static void insertCoffeetype(CoffeeType coffeetype)
    {
        synchronized (theObject)
        {
            if (coffeetype != null)
                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_COFFEETYPES + " (" + COLUMN_COFFEE_ID + ", " + COLUMN_LABEL + ", " + COLUMN_DESCRIPTION + ", "
                                        + COLUMN_PRICE + ", " + COLUMN_IMAGE + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?, ?, ?, ?, ?)",
                                new Object[]
                                        {coffeetype.id, coffeetype.label, coffeetype.description, Float.toString(coffeetype.price), coffeetype.image,
                                                coffeetype.timestamp});
        }
    }

    public static void removeCoffeetype(String coffeetypeid)
    {
        synchronized (theObject)
        {
            if (coffeetypeid != null)
                DB.getSharedInstance().getTheDatabase()
                        .execSQL("DELETE FROM " + DB.TABLE_NAME_COFFEETYPES + " WHERE " + COLUMN_COFFEE_ID + " = ?", new String[]
                                {coffeetypeid});
        }
    }

    public static CoffeeType getCoffeeType(String coffeetypeid)
    {
        synchronized (theObject)
        {
            if (coffeetypeid == null)
                return null;

            CoffeeType result = null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_COFFEETYPES + " WHERE " + COLUMN_COFFEE_ID + " = ?", new String[]
                            {coffeetypeid});

            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    CoffeeType theCoffeeType = new CoffeeType(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getFloat(4),
                            theCursor.getString(5), theCursor.getLong(6));
                    result = theCoffeeType;
                    break;
                }
            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<CoffeeType> getCoffeeTypes(String coffeetypeid)
    {
        synchronized (theObject)
        {
            ArrayList<CoffeeType> result = new ArrayList<CoffeeType>();
            if (coffeetypeid == null)
                return result;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_COFFEETYPES + " WHERE " + COLUMN_COFFEE_ID + " = ?", new String[]
                            {coffeetypeid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    CoffeeType theCoffeeType = new CoffeeType(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getFloat(4),
                            theCursor.getString(5), theCursor.getLong(6));
                    result.add(theCoffeeType);
                    theCursor.moveToNext();
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<CoffeeType> getAllCoffeeTypes()
    {
        synchronized (theObject)
        {
            ArrayList<CoffeeType> result = new ArrayList<CoffeeType>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_COFFEETYPES, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    CoffeeType theCoffeeType = new CoffeeType(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getFloat(4),
                            theCursor.getString(5), theCursor.getLong(6));
                    result.add(theCoffeeType);
                    theCursor.moveToNext();
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static boolean merge(CoffeeType object)
    {
        synchronized (theObject)
        {
            ArrayList<CoffeeType> all = CoffeeType.getCoffeeTypes(object.id);

            if (all.size() <= 0)
            {
                CoffeeType.insertCoffeetype(object);

                return true;
            } else
            {
                boolean bReadytoinsert = false;

                for (CoffeeType oldObject : all)
                {
                    if (oldObject.timestamp < object.timestamp)
                    {
                        CoffeeType.removeCoffeetype(oldObject.id);
                        bReadytoinsert = true;
                    }
                }

                if (bReadytoinsert)
                {
                    CoffeeType.insertCoffeetype(object);
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public String getJsonString()
    {
        return this.getJsonObject().toString();
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(CoffeeType.COLUMN_COFFEE_ID, this.id);
            jsonObject.put(CoffeeType.COLUMN_LABEL, this.label);
            jsonObject.put(CoffeeType.COLUMN_DESCRIPTION, this.description);
            jsonObject.put(CoffeeType.COLUMN_PRICE, this.price);
            jsonObject.put(CoffeeType.COLUMN_IMAGE, this.image);
            jsonObject.put(CoffeeType.COLUMN_TIMESTAMP, this.timestamp);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static DBObject getObjectFromJsonObject(JSONObject jsonObject)
    {
        try
        {
            CoffeeType tmpObject;
            tmpObject = new CoffeeType(jsonObject.getString(CoffeeType.COLUMN_COFFEE_ID), jsonObject.getString(CoffeeType.COLUMN_LABEL),
                    jsonObject.getString(CoffeeType.COLUMN_DESCRIPTION), (float) jsonObject.getDouble(CoffeeType.COLUMN_PRICE),
                    jsonObject.getString(CoffeeType.COLUMN_IMAGE), jsonObject.getLong(CoffeeType.COLUMN_TIMESTAMP));
            return tmpObject;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static DBObject getObjectFromJsonString(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return CoffeeType.getObjectFromJsonObject(jsonObject);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int getTotal()
    {
        synchronized (theObject)
        {
            int result = 0;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_COFFEETYPES, null);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_COFFEETYPES);
        }
    }
}
