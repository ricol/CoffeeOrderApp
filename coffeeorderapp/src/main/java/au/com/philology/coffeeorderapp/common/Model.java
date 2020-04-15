package au.com.philology.coffeeorderapp.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.Hashtable;

import au.com.philology.coffeeorderapp.database.CoffeeType;

public class Model
{
    public static Model theInstance;

    public static Model getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new Model();
        return theInstance;
    }

    Model()
    {
        super();

        this.CoffeeChoice.put("sugar", mSugarChoices);
        this.CoffeeChoice.put("milk", mMilkChoices);
        this.CoffeeChoice.put("strength", mStrengthChoices);
        this.CoffeeChoice.put("cuptype", mCuptypeChoices);
    }

    private Hashtable<String, String[]> CoffeeChoice = new Hashtable<String, String[]>();
    private String[] mSugarChoices =
            {"none", "1 sugar", "2 sugar", "sweetener"};
    private String[] mMilkChoices =
            {"full cream", "skim", "soy", "none"};
    private String[] mStrengthChoices =
            {"regular", "de-caf", "double shot", "weak"};
    private String[] mCuptypeChoices =
            {"cup", "mug", "take-away"};

    public String[] mCoffeeIds =
            {"1", "2", "3", "4", "5", "6", "7", "8"};

    public String[] mCoffeeLables =
            {"Flat White", "Long Black", "Caffe Latte", "Cappuccino", "Macchiato", "Espresso", "Caffe Mocha", "Hot Chocolate"};
    public String[] mCoffeeImages =
            {"flat_white", "long_black", "latte", "cappuccino", "macchiato", "espresso", "caffe_mocha", "hot_chocolate"};
    public String[] mCoffeeDescriptions =
            {"flat_white", "long_black", "latte", "cappuccino", "macchiato", "espresso", "caffe_mocha", "hot_chocolate"};

    public float[] mPrices =
            {4.0f, 4.0f, 5.0f, 4.5f, 5.5f, 4.0f, 5.0f, 4.0f};

    public String sugarToString(int value)
    {
        String[] choices = this.CoffeeChoice.get("sugar");
        if (value >= 0 && value < choices.length)
            return choices[value];
        return choices[0];
    }

    public String milkToString(int value)
    {
        String[] choices = this.CoffeeChoice.get("milk");
        if (value >= 0 && value < choices.length)
            return choices[value];
        return choices[0];
    }

    public String strengthToString(int value)
    {
        String[] choices = this.CoffeeChoice.get("strength");
        if (value >= 0 && value < choices.length)
            return choices[value];
        return choices[0];
    }

    public String cuptypeToString(int value)
    {
        String[] choices = this.CoffeeChoice.get("cuptype");
        if (value >= 0 && value < choices.length)
            return choices[value];
        return choices[0];
    }

    public void write()
    {
        for (int i = 0; i < mCoffeeIds.length; i++)
        {
            CoffeeType tmpOldCoffeeType = CoffeeType.getCoffeeType(mCoffeeIds[i]);
            if (tmpOldCoffeeType == null)
            {
                CoffeeType tmpCoffeeType = new CoffeeType(mCoffeeIds[i], mCoffeeLables[i], mCoffeeDescriptions[i], mPrices[i], mCoffeeImages[i],
                        (new Date()).getTime());
                CoffeeType.merge(tmpCoffeeType);
            }
        }
    }

    public static boolean isPrintButtonVisible(Context theContext)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        return pref.getBoolean(Common.KEY_IS_PRINT_BUTTON_VISIBLE, false);
    }

    public static void setPrintButtonVisibility(Context theContext, boolean bVisible)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        pref.edit().putBoolean(Common.KEY_IS_PRINT_BUTTON_VISIBLE, bVisible).apply();
    }

    public static boolean isAutoCheckWorkingMode(Context theContext)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        return pref.getBoolean(Common.KEY_IS_AUTO_MONITORING_WORKING_MODE, true);
    }

    public static void setAutoCheckWorkingMode(Context theContext, boolean bAutoCheck)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        pref.edit().putBoolean(Common.KEY_IS_AUTO_MONITORING_WORKING_MODE, bAutoCheck).apply();
    }

    public static int getAutoMonitoringWorkingModePeriod(Context theContext)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        return pref.getInt(Common.KEY_AUTO_MONITORING_WORKING_MODE_PEROID, Common.WORKING_MODE_CHECK_INTERVAL_MINIMUM_IN_SECONDS);
    }

    public static void setAutoMonitoringWorkingModePeriod(Context theContext, int period)
    {
        SharedPreferences pref = theContext.getSharedPreferences(Common.APP_ID, Context.MODE_PRIVATE);
        pref.edit().putInt(Common.KEY_AUTO_MONITORING_WORKING_MODE_PEROID, period).apply();
    }
}
