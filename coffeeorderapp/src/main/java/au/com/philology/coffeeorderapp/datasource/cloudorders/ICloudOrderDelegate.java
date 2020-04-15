package au.com.philology.coffeeorderapp.datasource.cloudorders;

public interface ICloudOrderDelegate
{
    public void cloudOrderDataReceived(String data);

    public void cloudOrderNewOrder(String deviceId, String tagId);

    public void cloudOrderStarted();

    public void cloudOrderEnded();
}
