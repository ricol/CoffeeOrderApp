package au.com.philology.coffeeorderapp.common;

public class Common
{
    public static String ExternalRFIDReaderAddress = "00:06:66:63:53:34";
    public static String ExternalRFIDReaderName = "External RFID Reader";
    public static String HOCKEY_APP_ID = "e7f5a3b2b158b64aa2818f383f1e594f";
    public static long CLOUD_ORDER_CHECK_INTERVAL = 5000;
    public static int WORKING_MODE_CHECK_INTERVAL_MINIMUM_IN_SECONDS = 30;
    public static int WORKING_MODE_CHECK_INTERVAL_STEP_IN_SECONDS = 30;
    public static int WORKING_MODE_CHECK_INTERVAL_COUNT = 10;

    public static String NAME_INTENT_ORDER_PLACED = "IntentOrderPlaced";
    public static String KEY_IS_PRINT_BUTTON_VISIBLE = "keyIsPrintButtonVisible";
    public static String KEY_IS_AUTO_MONITORING_WORKING_MODE = "keyIsAutoMonitoringWorkingMode";
    public static String KEY_AUTO_MONITORING_WORKING_MODE_PEROID = "keyAutoMonitoringWorkingModePeriod";
    public static String APP_ID = "au.com.philology.coffeeorderapp";

    public enum TypeWorkingMode
    {
        SERVER_MODE, CLIENT_MODE, SINGLE_MODE
    }

    public enum TagDataSourceType
    {
        EXTERNAL_READER, USB_SERIAL_READER
    }

    public static void sleep(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getModeDescription(TypeWorkingMode mode)
    {
        if (mode == TypeWorkingMode.SERVER_MODE)
            return "Server mode";
        else if (mode == TypeWorkingMode.CLIENT_MODE)
            return "Client mode";
        else if (mode == TypeWorkingMode.SINGLE_MODE)
            return "Single mode";
        else
            return "Single mode";
    }

    public static int WorkingModeToInt(TypeWorkingMode mode)
    {
        if (mode == TypeWorkingMode.SERVER_MODE)
            return 0;
        else if (mode == TypeWorkingMode.CLIENT_MODE)
            return 1;
        else if (mode == TypeWorkingMode.SINGLE_MODE)
            return 2;
        else
            return 2;
    }

    public static TypeWorkingMode IntToWorkingMode(int mode)
    {
        if (mode == 0)
            return TypeWorkingMode.SERVER_MODE;
        else if (mode == 1)
            return TypeWorkingMode.CLIENT_MODE;
        else if (mode == 2)
            return TypeWorkingMode.SINGLE_MODE;
        else
            return TypeWorkingMode.SINGLE_MODE;
    }

    public static int ReaderTypeToInt(TagDataSourceType readerType)
    {
        if (readerType == TagDataSourceType.EXTERNAL_READER)
            return 0;
        else if (readerType == TagDataSourceType.USB_SERIAL_READER)
            return 1;
        else
            return 0;
    }

    public static TagDataSourceType IntToReaderType(int readerType)
    {
        if (readerType == 0)
            return TagDataSourceType.EXTERNAL_READER;
        else if (readerType == 1)
            return TagDataSourceType.USB_SERIAL_READER;
        else
            return TagDataSourceType.EXTERNAL_READER;
    }

    public static String getPrinterReceiptFormat(String firstname, String lastname, String Order, String Sugar, String Milk, String CupType, String Strength,
                                                 String Date)
    {
        String text = "                    **                    \n"
                + "                   ****                   \n"
                + "                 ********                 \n"
                + "               ************               \n"
                + "             ****************             \n"
                + "          **********************          \n"
                + "             Place Cup Here               \n"
                + "          **********************          \n"
                + "             ****************             \n"
                + "               ************               \n"
                + "                 ********                 \n"
                + "                   ****                   \n"
                + "                    **                    \n"
                + "                                          \n"
                + "                                          \n"
                + "          Coffee Order Receipt            \n"
                + "                                          \n"
                + "      Name:           "
                + firstname
                + " "
                + lastname
                + "\n\n"
                + "      Order:          "
                + Order
                + "\n"
                + "      Strength:       "
                + Strength
                + "\n"
                + "      Milk:           "
                + Milk
                + "\n"
                + "      Sugar:          "
                + Sugar
                + "\n"
                + "      Cup Type:       "
                + CupType
                + "\n\n"
                + "      Date:           "
                + Date
                + "\n"
                + "------------------------------------------\n\n\n\n\n\n\n\n\n\n\n\n";
        return text;
    }

    public static String getCapitalizeStr(String input)
    {
        if (input == null)
            return "";
        if (input.length() >= 2)
        {
            String firstCharacter = input.substring(0, 1).toUpperCase();
            String rest = input.substring(1).toLowerCase();
            return firstCharacter + rest;
        } else
            return input;
    }

}
