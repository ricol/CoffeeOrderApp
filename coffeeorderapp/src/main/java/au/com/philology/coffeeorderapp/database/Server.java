package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends DBObject
{
    public static final int PORT = 8000;
    public static int TIMEOUT = 400;
    public static int IP_STASRT = 1;
    public static int IP_END = 254;

    public static final String COLUMN_SERVER_IP = "SERVER_IP";
    public static final String COLUMN_SERVER_PORT = "SERVER_PORT";

    public String server_ip;
    public int server_port;

    static Object theObject = new Object();

    public Server(String server_ip, int server_port, long timestamp)
    {
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "ServerIP:(" + this.server_ip + ", " + this.server_port + ", " + this.timestamp + ")";
    }

    public static void updateServer(Server server)
    {
        synchronized (theObject)
        {
            if (server != null)
            {
                DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_SERVER);

                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_SERVER + "(" + COLUMN_SERVER_IP + ", " + COLUMN_SERVER_PORT + ", " + COLUMN_TIMESTAMP
                                        + ") VALUES (?, ?, ?)", new Object[]
                                        {server.server_ip, server.server_port, server.timestamp});
            }
        }
    }

    public static Server getServer()
    {
        synchronized (theObject)
        {
            Server result = null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_SERVER, new String[]
                    {});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Server server = new Server(theCursor.getString(1), theCursor.getInt(2), theCursor.getLong(3));
                    result = server;
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
            jsonObject.put(COLUMN_SERVER_IP, this.server_ip);
            jsonObject.put(COLUMN_SERVER_PORT, this.server_port);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_SERVER);
        }
    }
}
