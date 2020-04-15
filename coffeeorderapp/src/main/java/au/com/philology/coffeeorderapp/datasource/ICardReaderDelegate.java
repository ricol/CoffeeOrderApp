package au.com.philology.coffeeorderapp.datasource;

public interface ICardReaderDelegate
{
    public void CardReaderTagDetected(String text, String tag);

    public void CardReaderLostConnection(String text);

    public void CardReaderConnected(String text);

    public void CardReaderOperationCancelled(String text);

    public void CardReaderStateChanged(String state);

    public void CardReaderTryToConnect(String text);

    public void CardReaderTryToDisconnect(String text);
}
