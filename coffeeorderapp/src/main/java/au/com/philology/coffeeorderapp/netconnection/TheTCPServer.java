package au.com.philology.coffeeorderapp.netconnection;

import au.com.philology.tcpudp.IServerDelegate;
import au.com.philology.tcpudp.TcpServer;

public class TheTCPServer extends TcpServer
{
    public TheTCPServer(IServerDelegate serverDelegate)
    {
        super(serverDelegate);
    }

    public static TheTCPServer theInstance;

    public static TheTCPServer getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new TheTCPServer(TheTCPServerDelegate.getSharedInstance());
        return theInstance;
    }
}
