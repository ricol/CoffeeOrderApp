package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Order extends DBObject
{
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_COFFEE_ID = "COFFEE_ID";
    public static final String COLUMN_DEVICE_ID = "DEVICE_ID";

    public String user_id;
    public String coffee_id;
    public String device_id;
    static Object theObject = new Object();

    public Order(String userid, String coffeeid, String deviceid, long timestamp)
    {
        this.user_id = userid;
        this.coffee_id = coffeeid;
        this.device_id = deviceid;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "Order:(" + this.user_id + ", " + this.coffee_id + ", " + this.device_id + ", " + this.timestamp + ")";
    }

    static void insertOrder(Order aOrder)
    {
        synchronized (theObject)
        {
            if (aOrder != null)
                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_ORDERS + " (" + COLUMN_USER_ID + ", " + COLUMN_COFFEE_ID + ", " + COLUMN_DEVICE_ID + ", "
                                        + COLUMN_TIMESTAMP + ") VALUES (?, ?, ?, ?)", new Object[]
                                        {aOrder.user_id, aOrder.coffee_id, aOrder.device_id, aOrder.timestamp});
        }
    }

    public static void removeOrderForUser(String userid)
    {
        synchronized (theObject)
        {
            if (userid != null)
                DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_ORDERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                        {userid});
        }
    }

    public static ArrayList<Order> getOrdersForUser(String userid)
    {
        synchronized (theObject)
        {
            ArrayList<Order> result = new ArrayList<Order>();
            if (userid == null)
                return result;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_ORDERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]
                            {userid});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Order aOrder = new Order(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result.add(aOrder);
                    theCursor.moveToNext();
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static Order getOrderForUser(String userid, String coffeeid, String deviceid, long timestamp)
    {
        synchronized (theObject)
        {
            Order result = null;

            Cursor theCursor = DB
                    .getSharedInstance()
                    .getTheDatabase()
                    .rawQuery(
                            "SELECT * FROM " + DB.TABLE_NAME_ORDERS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_COFFEE_ID + " = ? AND "
                                    + COLUMN_DEVICE_ID + " = ? AND " + COLUMN_TIMESTAMP + " = ?", new String[]
                                    {userid, coffeeid, deviceid, Long.toString(timestamp)});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Order aOrder = new Order(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result = aOrder;
                    break;
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<Order> getAllOrders()
    {
        synchronized (theObject)
        {
            ArrayList<Order> result = new ArrayList<Order>();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_ORDERS, null);
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Order aOrder = new Order(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result.add(aOrder);
                    theCursor.moveToNext();
                }

            } finally
            {
                theCursor.close();
            }

            return result;
        }
    }

    public static ArrayList<Order> getOrders(Date dateFrom, Date dateTo)
    {
        synchronized (theObject)
        {
            ArrayList<Order> result = new ArrayList<Order>();

            long dateFromTimestamp = dateFrom.getTime();
            long dateToTimestamp = dateTo.getTime();

            Cursor theCursor = DB.getSharedInstance().getTheDatabase()
                    .rawQuery("SELECT * FROM " + DB.TABLE_NAME_ORDERS + " WHERE TIMESTAMP >= ? AND TIMESTAMP <= ?", new String[]
                            {Long.toString(dateFromTimestamp), Long.toString(dateToTimestamp)});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Order aOrder = new Order(theCursor.getString(1), theCursor.getString(2), theCursor.getString(3), theCursor.getLong(4));
                    result.add(aOrder);
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

    public static boolean merge(Order object)
    {
        Order aOrder = Order.getOrderForUser(object.user_id, object.coffee_id, object.device_id, object.timestamp);
        if (aOrder == null)
        {
            Order.insertOrder(object);
            return true;
        } else
            return false;
    }

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(Order.COLUMN_USER_ID, this.user_id);
            jsonObject.put(Order.COLUMN_COFFEE_ID, this.coffee_id);
            jsonObject.put(Order.COLUMN_DEVICE_ID, this.device_id);
            jsonObject.put(Order.COLUMN_TIMESTAMP, this.timestamp);
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
            Order tmpObject = new Order(jsonObject.getString(Order.COLUMN_USER_ID), jsonObject.getString(Order.COLUMN_COFFEE_ID),
                    jsonObject.getString(Order.COLUMN_DEVICE_ID), jsonObject.getLong(Order.COLUMN_TIMESTAMP));
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
            return Order.getObjectFromJsonObject(jsonObject);
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

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT COUNT(*) AS COUNT FROM " + DB.TABLE_NAME_ORDERS, null);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_ORDERS);
        }
    }

}
