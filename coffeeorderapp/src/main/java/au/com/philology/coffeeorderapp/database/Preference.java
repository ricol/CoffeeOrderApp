package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Preference extends DBObject
{
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_COFFEE_ID = "COFFEE_ID";
    public static final String COLUMN_PREFERENCE_JSON = "PREFERENCE_JSON";

    public static final String PREFERENCE_SUGAR = "SUGAR";
    public static final String PREFERENCE_MILK = "MILK";
    public static final String PREFERENCE_CUP_TYPE = "CUP_TYPE";
    public static final String PREFERENCE_STRENGTH = "STRENGTH";

    public String user_id;
    public String coffee_id;
    public String preference_json;

    static Object theObject = new Object();

    public Preference(String userid, String coffeeid, String preferenceJson, long timestamp)
    {
        this.user_id = userid;
        this.coffee_id = coffeeid;
        this.preference_json = preferenceJson;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Preference:(" + this.user_id + ", " + this.coffee_id + ", " + this.getSugar() + ", " + this.getMilk() + ", " + this.getCuptype() + ", "
                + this.getStrength() + ", " + this.timestamp + ")";
    }

    static void insertPreference(Preference preference)
    {
        synchronized (theObject)
        {
            if (preference != null)
                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_PREFERENCES + " (" + COLUMN_USER_ID + ", " + COLUMN_COFFEE_ID + ", " + COLUMN_PREFERENCE_JSON
                                        + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?, ?, ?)", new Object[]
                                        {preference.user_id, preference.coffee_id, preference.preference_json, preference.timestamp});
        }
    }

    public static void removePreferenceForUser(String userid)
    {
        synchronized (theObject)
        {
            if (userid != null)
                DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_PREFERENCES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                        {userid});
        }
    }

    public static Preference getPreferenceForUser(String userid)
    {
        synchronized (theObject)
        {
            Preference result = null;

            if (userid == null)
                return null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_PREFERENCES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                            {userid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Preference thePreference = new Preference(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result = thePreference;
                    break;
                }
            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<Preference> getPreferencesForUser(String userid)
    {
        synchronized (theObject)
        {
            ArrayList<Preference> result = new ArrayList<Preference>();
            if (userid == null)
                return result;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_PREFERENCES + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                            {userid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Preference thePreference = new Preference(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result.add(thePreference);
                    theCursor.moveToNext();
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<Preference> getAllPreferences()
    {
        synchronized (theObject)
        {
            ArrayList<Preference> result = new ArrayList<Preference>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_PREFERENCES, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Preference object = new Preference(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result.add(object);
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
        return this.getJsonObject().toString();
    }

    public static boolean merge(Preference object)
    {
        synchronized (theObject)
        {
            ArrayList<Preference> all = Preference.getPreferencesForUser(object.user_id);

            if (all.size() <= 0)
            {
                Preference.insertPreference(object);
                return true;
            } else
            {
                for (Preference oldObject : all)
                {
                    boolean bReadytoinsert = false;

                    if (oldObject.timestamp < object.timestamp)
                    {
                        Preference.removePreferenceForUser(oldObject.user_id);
                        bReadytoinsert = true;
                    }

                    if (bReadytoinsert)
                    {
                        Preference.insertPreference(object);
                        return true;
                    }
                }
            }

            return false;
        }

    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(Preference.COLUMN_USER_ID, this.user_id);
            jsonObject.put(Preference.COLUMN_COFFEE_ID, this.coffee_id);
            jsonObject.put(Preference.COLUMN_PREFERENCE_JSON, this.preference_json);
            jsonObject.put(Preference.COLUMN_TIMESTAMP, this.timestamp);
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
            Preference tmpObject = new Preference(jsonObject.getString(Preference.COLUMN_USER_ID), jsonObject.getString(Preference.COLUMN_COFFEE_ID),
                    jsonObject.getString(Preference.COLUMN_PREFERENCE_JSON), jsonObject.getLong(Preference.COLUMN_TIMESTAMP));
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
            return Preference.getObjectFromJsonObject(jsonObject);
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

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_PREFERENCES, null);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_PREFERENCES);
        }
    }

    public static String getPreferenceJsonRepresentation(String sugar, String milk, String cuptype, String strength)
    {
        JSONObject aObject = new JSONObject();
        try
        {
            aObject.put(Preference.PREFERENCE_SUGAR, sugar);
            aObject.put(Preference.PREFERENCE_MILK, milk);
            aObject.put(Preference.PREFERENCE_CUP_TYPE, cuptype);
            aObject.put(Preference.PREFERENCE_STRENGTH, strength);
            return aObject.toString();
        } catch (Exception e)
        {
            return "";
        }
    }

    public String getMilk()
    {
        JSONObject aObject = this.getTheJSONObjectForThePreference();
        if (aObject != null)
        {
            try
            {
                return aObject.getString(PREFERENCE_MILK);
            } catch (Exception e)
            {
                return "";
            }
        } else
            return "";
    }

    public String getStrength()
    {
        JSONObject aObject = this.getTheJSONObjectForThePreference();
        if (aObject != null)
        {
            try
            {
                return aObject.getString(PREFERENCE_STRENGTH);
            } catch (Exception e)
            {
                return "";
            }
        } else
            return "";
    }

    public String getCuptype()
    {
        JSONObject aObject = this.getTheJSONObjectForThePreference();
        if (aObject != null)
        {
            try
            {
                return aObject.getString(PREFERENCE_CUP_TYPE);
            } catch (Exception e)
            {
                return "";
            }
        } else
            return "";
    }

    public String getSugar()
    {
        JSONObject aObject = this.getTheJSONObjectForThePreference();
        if (aObject != null)
        {
            try
            {
                return aObject.getString(PREFERENCE_SUGAR);
            } catch (Exception e)
            {
                return "";
            }
        } else
            return "";
    }

    public JSONObject getTheJSONObjectForThePreference()
    {
        try
        {
            JSONObject aObject = new JSONObject(this.preference_json);
            return aObject;
        } catch (Exception e)
        {
            return null;
        }
    }
}
