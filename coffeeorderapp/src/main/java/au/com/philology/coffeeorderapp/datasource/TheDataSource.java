package au.com.philology.coffeeorderapp.datasource;

import au.com.philology.coffeeorderapp.common.Common.TagDataSourceType;
import au.com.philology.coffeeorderapp.database.DataSource;

public class TheDataSource
{
    ICardReader theCardReader;

    public static ICardReader theInstance;

    public static ICardReader getSharedInstance()
    {
        if (theInstance == null)
        {
            TagDataSourceType readerType = DataSource.getReaderType();
            if (readerType == TagDataSourceType.EXTERNAL_READER)
                TheDataSource.switchToExternalRFIDReader();
            else if (readerType == TagDataSourceType.USB_SERIAL_READER)
                TheDataSource.switchToUSBSerialReader();
        }

        return theInstance;
    }

    public static void switchToExternalRFIDReader()
    {
        if (theInstance != null)
        {
            if (theInstance instanceof SerMagicGearsApp)
                return;

            theInstance.activityOnPause();
            theInstance.disconnect();
        }

        ICardReader oldOne = theInstance;
        theInstance = SerMagicGearsApp.getSharedInstance();

        if (oldOne != null)
        {
            theInstance.setActivity(oldOne.getTheActivity());
            theInstance.setCardReaderDelegate(oldOne.getTheDelegate());
            oldOne.setActivity(null);
            oldOne.setCardReaderDelegate(null);
        }
    }

    public static void switchToUSBSerialReader()
    {
        if (theInstance != null)
        {
            if (theInstance instanceof USBSerialReader)
                return;

            theInstance.activityOnPause();
            theInstance.disconnect();
        }

        ICardReader oldOne = theInstance;
        theInstance = USBSerialReader.getSharedInstance();

        if (oldOne != null)
        {
            theInstance.setActivity(oldOne.getTheActivity());
            theInstance.setCardReaderDelegate(oldOne.getTheDelegate());
            oldOne.setActivity(null);
            oldOne.setCardReaderDelegate(null);
        }
    }
}
