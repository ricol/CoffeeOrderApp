package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User extends DBObject
{
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_FIRST_NAME = "FIRST_NAME";
    public static final String COLUMN_LAST_NAME = "LAST_NAME";
    public static final String COLUMN_MOBILE = "MOBILE";

    public String first_name;
    public String last_name;
    public String mobile;
    public String user_id;

    static Object theObject = new Object();

    public User(String userid, String firstname, String lastname, String mobile, long timestamp)
    {
        this.first_name = firstname;
        this.last_name = lastname;
        this.user_id = userid;
        this.mobile = mobile;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "User:(" + this.user_id + ", " + this.first_name + ", " + this.last_name + ", " + this.mobile + ", " + this.timestamp + ")";
    }

    static void insertUser(User user)
    {
        synchronized (theObject)
        {
            if (user != null)
                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_USERS + "(" + COLUMN_USER_ID + ", " + COLUMN_FIRST_NAME + ", " + COLUMN_LAST_NAME + ", "
                                        + COLUMN_MOBILE + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?, ?, ?, ?)", new Object[]
                                        {user.user_id, user.first_name, user.last_name, user.mobile, user.timestamp});
        }
    }

    public static void removeUser(String userid)
    {
        synchronized (theObject)
        {
            if (userid != null)
                DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                        {userid});
        }
    }

    public static User getUser(String userid)
    {
        synchronized (theObject)
        {
            User result = null;
            if (userid == null)
                return null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                            {userid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    User theUser = new User(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getString(4),
                            theCursor.getLong(5));
                    result = theUser;
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<User> getUsers(String userid)
    {
        synchronized (theObject)
        {
            ArrayList<User> result = new ArrayList<User>();
            if (userid == null)
                return result;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                            {userid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    User theUser = new User(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getString(4),
                            theCursor.getLong(5));
                    result.add(theUser);
                    theCursor.moveToNext();
                }
            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<User> getAllUsers()
    {
        synchronized (theObject)
        {
            ArrayList<User> result = new ArrayList<User>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_USERS, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    User object = new User(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getString(4), theCursor.getLong(5));
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

    public static boolean merge(User object)
    {
        synchronized (theObject)
        {
            ArrayList<User> all = User.getUsers(object.user_id);

            if (all.size() <= 0)
            {
                User.insertUser(object);
                return true;
            } else
            {
                for (User oldObject : all)
                {
                    boolean bReadytoinsert = false;
                    if (oldObject.timestamp < object.timestamp)
                    {
                        User.removeUser(oldObject.user_id);
                        bReadytoinsert = true;
                    }

                    if (bReadytoinsert)
                    {
                        User.insertUser(object);
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
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(User.COLUMN_USER_ID, this.user_id);
            jsonObject.put(User.COLUMN_FIRST_NAME, this.first_name);
            jsonObject.put(User.COLUMN_LAST_NAME, this.last_name);
            jsonObject.put(User.COLUMN_MOBILE, this.mobile);
            jsonObject.put(User.COLUMN_TIMESTAMP, this.timestamp);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static DBObject getObjectFromJsonObject(JSONObject jsonObject)
    {
        try
        {
            User tmpObject = new User(jsonObject.getString(User.COLUMN_USER_ID), jsonObject.getString(User.COLUMN_FIRST_NAME),
                    jsonObject.getString(User.COLUMN_LAST_NAME), jsonObject.getString(User.COLUMN_MOBILE), jsonObject.getLong(User.COLUMN_TIMESTAMP));
            return tmpObject;
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static DBObject getObjectFromJsonString(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return User.getObjectFromJsonObject(jsonObject);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static int getTotal()
    {
        synchronized (theObject)
        {
            int result = 0;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_USERS, null);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_USERS);
        }
    }
}
