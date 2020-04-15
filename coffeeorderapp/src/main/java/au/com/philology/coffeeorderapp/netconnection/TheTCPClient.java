package au.com.philology.coffeeorderapp.netconnection;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.database.Server;
import au.com.philology.tcpudp.IClientDelegate;
import au.com.philology.tcpudp.IScanDelegate;
import au.com.philology.tcpudp.TcpClient;

public class TheTCPClient extends TcpClient
{
    static int CONNECTION_TIMEOUT = 3000;
    public IClientConnectionTestDelegate clientTestDelegate;
    TcpClient tcpClientTest;

    public TheTCPClient(IClientDelegate clientDelegate, IScanDelegate scanDelegate)
    {
        super(clientDelegate, scanDelegate);
    }

    public static TheTCPClient theInstance;

    public static TheTCPClient getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new TheTCPClient(TheTCPClientDelegate.getSharedInstance(), TheTCPClientDelegate.getSharedInstance());
        return theInstance;
    }

    public void search()
    {
        this.startScaningIpForPort(Server.PORT, Server.IP_STASRT, Server.IP_END, Server.TIMEOUT);
    }

    public void connectionTest(String ip, int port)
    {
        this.tcpClientTest = new TcpClient(null, null);

        this.tcpClientTest.connect(ip, port);

        Common.sleep(CONNECTION_TIMEOUT);

        if (this.tcpClientTest.isConnected())
        {
            this.tcpClientTest.disconnect();
            this.clientTestDelegate.ClientConnectionTestPass(ip, port);
        } else
        {
            this.clientTestDelegate.ClientConnectionTestFailed(ip, port);
        }
    }
}
