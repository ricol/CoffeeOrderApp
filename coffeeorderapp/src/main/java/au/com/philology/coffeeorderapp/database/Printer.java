package au.com.philology.coffeeorderapp.database;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class Printer extends DBObject
{
    public static final int PORT = 9100;
    public static int TIMEOUT = 500;
    public static int IP_STASRT = 1;
    public static int IP_END = 254;

    public static final String COLUMN_PRINTER_IP = "PRINTER_IP";
    public static final String COLUMN_PRINTER_PORT = "PRINTER_PORT";

    public String printer_ip;
    public int printer_port;
    static Object theObject = new Object();

    public Printer(String printer_ip, int printer_port, long timestamp)
    {
        this.printer_ip = printer_ip;
        this.printer_port = printer_port;
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return "PrinterIP:(" + this.printer_ip + ", " + this.printer_port + ", " + this.timestamp + ")";
    }

    public static void updatePrinter(Printer printer)
    {
        synchronized (theObject)
        {
            if (printer != null)
            {
                DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_PRINTER);

                DB.getSharedInstance()
                        .getTheDatabase()
                        .execSQL(
                                "INSERT INTO " + DB.TABLE_NAME_PRINTER + "(" + COLUMN_PRINTER_IP + ", " + COLUMN_PRINTER_PORT + ", " + COLUMN_TIMESTAMP
                                        + ") VALUES (?, ?, ?)", new Object[]
                                        {printer.printer_ip, printer.printer_port, printer.timestamp});
            }
        }
    }

    public static Printer getPrinter()
    {
        synchronized (theObject)
        {
            Printer result = null;

            Cursor theCursor = DB.getSharedInstance().getTheDatabase().rawQuery("SELECT * FROM " + DB.TABLE_NAME_PRINTER, new String[]
                    {});
            try
            {
                theCursor.moveToFirst();
                while (!theCursor.isAfterLast())
                {
                    Printer printer = new Printer(theCursor.getString(1), theCursor.getInt(2), theCursor.getLong(3));
                    result = printer;
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
            jsonObject.put(COLUMN_PRINTER_IP, this.printer_ip);
            jsonObject.put(COLUMN_PRINTER_PORT, this.printer_port);
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
            DB.getSharedInstance().getTheDatabase().execSQL("DELETE FROM " + DB.TABLE_NAME_PRINTER);
        }
    }
}
