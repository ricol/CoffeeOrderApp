package au.com.philology.coffeeorderapp.netconnection;

import java.util.LinkedList;
import java.util.Queue;

import au.com.philology.coffeeorderapp.database.sync.client.CoffeeTypeSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.OrderSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.PreferenceSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.UserSyncClient;
import au.com.philology.tcpudp.IClientDelegate;
import au.com.philology.tcpudp.IScanDelegate;

public class TheTCPClientDelegate implements IClientDelegate, IScanDelegate
{
    public static TheTCPClientDelegate theInstance;
    public static String TAG = "TheTCPClientDelegate";
    public IClientDelegate clientDelegate;
    public IScanDelegate scanDelegate;
    Queue<ServerToClientMessage> theMessageQueue = new LinkedList<ServerToClientMessage>();
    ProcessThread theProessThread;

    public TheTCPClientDelegate()
    {
        super();

        theProessThread = new ProcessThread();
        theProessThread.start();
    }

    public static TheTCPClientDelegate getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new TheTCPClientDelegate();
        return theInstance;
    }

    @Override
    public void ConnectionDelegateConnected(String address, int port)
    {
        if (this.clientDelegate != null)
            this.clientDelegate.ConnectionDelegateConnected(address, port);
    }

    @Override
    public void ConnectionDelegateLostConnection(String address, int port)
    {
        if (this.clientDelegate != null)
            this.clientDelegate.ConnectionDelegateLostConnection(address, port);

    }

    @Override
    public void ScanDelegateIsScanningIpForPort(String serverAddress, int port)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateIsScanningIpForPort(serverAddress, port);

    }

    @Override
    public void ScanDelegateIpFoundForPort(String serverAddress, int port)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateIpFoundForPort(serverAddress, port);
    }

    @Override
    public void ScanDelegateCompleteScanningIpForPort()
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateCompleteScanningIpForPort();
    }

    @Override
    public void ScanDelegateStartScanningIpForPort()
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateStartScanningIpForPort();
    }

    @Override
    public void ScanDelegateStartScanningPortForIp(String ip, int startPort, int endPort)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateStartScanningPortForIp(ip, startPort, endPort);

    }

    @Override
    public void ScanDelegateIsScanningPortForIp(String ip, int port)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateIsScanningPortForIp(ip, port);

    }

    @Override
    public void ScanDelegatePortFoundForIp(String ip, int port)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegatePortFoundForIp(ip, port);

    }

    @Override
    public void ScanDelegateCompleteScanningPortForIp(String ip, int startPort, int endPort)
    {
        if (this.scanDelegate != null)
            this.scanDelegate.ScanDelegateCompleteScanningPortForIp(ip, startPort, endPort);

    }

    @Override
    public void ClientDelegateMessageReceived(String msg, String address, int port)
    {
        if (this.clientDelegate != null)
            this.clientDelegate.ClientDelegateMessageReceived(msg, address, port);

        System.out.println("Message from the server: " + msg);
        ServerToClientMessage aMessage = new ServerToClientMessage(msg, address, port);
        this.addMessage(aMessage);
    }

    synchronized void addMessage(ServerToClientMessage aMessage)
    {
        this.theMessageQueue.add(aMessage);
        System.out.println("add Queue: " + this.theMessageQueue.size() + " messages.");
    }

    synchronized void removeMessage(ServerToClientMessage aMessage)
    {
        this.theMessageQueue.remove(aMessage);
        System.out.println("remove Queue: " + this.theMessageQueue.size() + " messages.");
    }

    synchronized ServerToClientMessage getAMessage()
    {
        return this.theMessageQueue.peek();
    }

    private class ProcessThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                if (this.isInterrupted())
                    return;

                // get a message from the queue
                ServerToClientMessage aMessage = getAMessage();
                while (aMessage == null)
                {
                    if (this.isInterrupted())
                        return;
                    aMessage = getAMessage();
                    try
                    {
                        Thread.sleep(10);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                // process the message

                if (this.isInterrupted())
                    return;

                if (aMessage != null)
                {

                    CoffeeTypeSyncClient.getSharedInstance().processCmd(aMessage.msg, aMessage.serverAddress, aMessage.serverPort);
                    PreferenceSyncClient.getSharedInstance().processCmd(aMessage.msg, aMessage.serverAddress, aMessage.serverPort);
                    UserSyncClient.getSharedInstance().processCmd(aMessage.msg, aMessage.serverAddress, aMessage.serverPort);
                    OrderSyncClient.getSharedInstance().processCmd(aMessage.msg, aMessage.serverAddress, aMessage.serverPort);

                    removeMessage(aMessage);
                }
            }
        }
    }

    class ServerToClientMessage
    {
        String msg;
        String serverAddress;
        int serverPort;

        public ServerToClientMessage(String msg, String serverAddress, int serverPort)
        {
            this.msg = msg;
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
        }
    }

    void stopProcessMsg()
    {
        if (theProessThread != null)
            theProessThread.interrupt();
    }

}
