package au.com.philology.coffeeorderapp.netconnection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import au.com.philology.coffeeorderapp.database.sync.server.CoffeeTypeSyncServer;
import au.com.philology.coffeeorderapp.database.sync.server.OrderSyncServer;
import au.com.philology.coffeeorderapp.database.sync.server.PreferenceSyncServer;
import au.com.philology.coffeeorderapp.database.sync.server.UserSyncServer;
import au.com.philology.tcpudp.IServerDelegate;

public class TheTCPServerDelegate implements IServerDelegate
{
    public static TheTCPServerDelegate theInstance;
    public static String TAG = "TheTCPServerDelegate";
    public ArrayList<IServerDelegate> serverDelegates = new ArrayList();
    Queue<ClientToServerMessage> theMessageQueue = new LinkedList<ClientToServerMessage>();
    ProcessThread theProessThread;

    public TheTCPServerDelegate()
    {
        super();

        theProessThread = new ProcessThread();
        theProessThread.start();
    }

    public static TheTCPServerDelegate getSharedInstance()
    {
        if (theInstance == null)
        {
            theInstance = new TheTCPServerDelegate();
        }
        return theInstance;
    }

    @Override
    public void ConnectionDelegateConnected(String arg0, int arg1)
    {
        for (IServerDelegate aDelegate : this.serverDelegates)
        {
            aDelegate.ConnectionDelegateConnected(arg0, arg1);
        }
    }

    @Override
    public void ConnectionDelegateLostConnection(String arg0, int arg1)
    {
        for (IServerDelegate aDelegate : this.serverDelegates)
        {
            aDelegate.ConnectionDelegateLostConnection(arg0, arg1);
        }
    }

    @Override
    public void ServerDelegateClientMessageReceived(String arg0, String arg1, int arg2)
    {
        for (IServerDelegate aDelegate : this.serverDelegates)
        {
            aDelegate.ServerDelegateClientMessageReceived(arg0, arg1, arg2);
        }

        ClientToServerMessage aMessage = new ClientToServerMessage(arg0, arg1, arg2);
        this.addMessage(aMessage);
    }

    @Override
    public void ServerDelegateStartListening(int arg0)
    {
        for (IServerDelegate aDelegate : this.serverDelegates)
        {
            aDelegate.ServerDelegateStartListening(arg0);
        }
    }

    @Override
    public void ServerDelegateStopListening(int arg0)
    {
        for (IServerDelegate aDelegate : this.serverDelegates)
        {
            aDelegate.ServerDelegateStopListening(arg0);
        }
    }

    public void clearAllDelegates()
    {
        this.serverDelegates.clear();
    }

    public void addDelegate(IServerDelegate aDelegate)
    {
        if (!this.serverDelegates.contains(aDelegate))
            this.serverDelegates.add(aDelegate);
    }

    public void removeDelegate(IServerDelegate aDelegate)
    {
        if (this.serverDelegates.contains(aDelegate))
            this.serverDelegates.remove(aDelegate);
    }

    synchronized void addMessage(ClientToServerMessage aMessage)
    {
        this.theMessageQueue.add(aMessage);
        System.out.println("add Queue: " + this.theMessageQueue.size() + " messages.");
    }

    synchronized void removeMessage(ClientToServerMessage aMessage)
    {
        this.theMessageQueue.remove(aMessage);
        System.out.println("remove Queue: " + this.theMessageQueue.size() + " messages.");
    }

    synchronized ClientToServerMessage getAMessage()
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
                ClientToServerMessage aMessage = getAMessage();
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

                    CoffeeTypeSyncServer.getSharedInstance().processCmd(aMessage.msg, aMessage.clientAddress, aMessage.clientPort);
                    PreferenceSyncServer.getSharedInstance().processCmd(aMessage.msg, aMessage.clientAddress, aMessage.clientPort);
                    UserSyncServer.getSharedInstance().processCmd(aMessage.msg, aMessage.clientAddress, aMessage.clientPort);
                    OrderSyncServer.getSharedInstance().processCmd(aMessage.msg, aMessage.clientAddress, aMessage.clientPort);

                    removeMessage(aMessage);
                }
            }
        }
    }

    class ClientToServerMessage
    {
        String msg;
        String clientAddress;
        int clientPort;

        public ClientToServerMessage(String msg, String clientAddress, int clientPort)
        {
            this.msg = msg;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
        }
    }
}
