package au.com.philology.coffeeorderapp.netconnection;

public interface IClientConnectionTestDelegate
{
    public void ClientConnectionTestPass(String ip, int Port);

    public void ClientConnectionTestFailed(String ip, int Port);
}
