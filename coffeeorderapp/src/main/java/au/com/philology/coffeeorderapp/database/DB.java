package au.com.philology.coffeeorderapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import au.com.philology.common.utils;

public class DB extends SQLiteOpenHelper
{
    static final String DATABASE_NAME = "database.db";
    static final int DATABASE_VERSION = 9;

    // key settings
    static final String TABLE_NAME_WORKMODE = "workmode";
    static final String TABLE_NAME_DATA_SOURCE = "dataSource";
    static final String TABLE_NAME_PRINTER = "printer";
    static final String TABLE_NAME_SERVER = "server";
    static final String TABLE_NAME_CLOUD_ORDERS = "cloudOrders";
    static final String TABLE_NAME_INTERESTED_CARD_READER_ID = "interestedCardReaderID";
    static final String TABLE_NAME_CLOUD_ORDER_FILTER = "cloudOrderFilter";
    static final String TABLE_NAME_DEVELOPER_MODE = "developerMode";

    // key contents
    static final String TABLE_NAME_USERS = "users";
    static final String TABLE_NAME_PREFERENCES = "preferences";
    static final String TABLE_NAME_COFFEETYPES = "coffeetypes";
    static final String TABLE_NAME_ORDERS = "orders";
    static final String TABLE_NAME_CARD_READER_IDENTIFIERS = "cardReaderIdentifiers";

    public static DB theInstance;

    public static DB getSharedInstance()
    {
        return theInstance;
    }

    public SQLiteDatabase getTheDatabase()
    {
        return this.getWritableDatabase();
    }

    public DB(Context context)
    {
        super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.tryToCreateTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        utils.print(DB.class.getName() + "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_WORKMODE);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_DATA_SOURCE);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_PRINTER);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_SERVER);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_COFFEETYPES);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_CLOUD_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_CLOUD_ORDER_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS);
        db.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_NAME_DEVELOPER_MODE);

        onCreate(db);
    }

    public void clear()
    {
        User.clear();
        Preference.clear();
        Order.clear();
    }

    void tryToCreateTables(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_WORKMODE + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkingMode.COLUMN_WORKING_MODE + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_DATA_SOURCE + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataSource.COLUMN_RFID_READER_TYPE + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_PRINTER + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Printer.COLUMN_PRINTER_IP + " TEXT, " + Printer.COLUMN_PRINTER_PORT + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_SERVER + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Server.COLUMN_SERVER_IP + " TEXT, " + Server.COLUMN_SERVER_PORT + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_USERS + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User.COLUMN_USER_ID + " TEXT, " + User.COLUMN_FIRST_NAME + " TEXT, " + User.COLUMN_LAST_NAME + " TEXT, " + User.COLUMN_MOBILE + " TEXT, "
                + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_PREFERENCES + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Preference.COLUMN_USER_ID + " TEXT, " + Preference.COLUMN_COFFEE_ID + " TEXT, " + Preference.COLUMN_PREFERENCE_JSON + " TEXT, "
                + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_COFFEETYPES + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CoffeeType.COLUMN_COFFEE_ID + " TEXT, " + CoffeeType.COLUMN_LABEL + " TEXT, " + CoffeeType.COLUMN_DESCRIPTION + " TEXT, "
                + CoffeeType.COLUMN_PRICE + " FLOAT, " + CoffeeType.COLUMN_IMAGE + " TEXT, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_ORDERS + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Order.COLUMN_USER_ID + " TEXT, " + Order.COLUMN_COFFEE_ID + " TEXT, " + Order.COLUMN_DEVICE_ID + " TEXT, " + DBObject.COLUMN_TIMESTAMP
                + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_CLOUD_ORDERS + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CloudOrder.COLUMN_CLOUD_ORDER + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_CLOUD_ORDER_FILTER + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CloudOrderFilter.COLUMN_CLOUD_ORDER_FILTER + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_INTERESTED_CARD_READER_ID + " (" + DBObject.COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + InterestedCardReaderID.COLUMN_INTERESTED_ID + " TEXT, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_CARD_READER_IDENTIFIERS + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CardReaderIdentifier.COLUMN_IDENTIFIER + " TEXT, " + CardReaderIdentifier.COLUMN_NAME + " TEXT, " + CardReaderIdentifier.COLUMN_DESC
                + " TEXT, " + CardReaderIdentifier.COLUMN_AUTO_PRINT + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DB.TABLE_NAME_DEVELOPER_MODE + " (" + DBObject.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DeveloperMode.COLUMN_DEVELOPER_MODE + " INTEGER, " + DBObject.COLUMN_TIMESTAMP + " INTEGER)");

    }
}
