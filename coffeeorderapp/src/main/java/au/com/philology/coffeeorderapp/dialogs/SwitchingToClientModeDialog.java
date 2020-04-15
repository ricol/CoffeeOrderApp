package au.com.philology.coffeeorderapp.dialogs;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.Server;
import au.com.philology.coffeeorderapp.database.sync.client.CoffeeTypeSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.OrderSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.PreferenceSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.UserSyncClient;
import au.com.philology.coffeeorderapp.netconnection.IClientConnectionTestDelegate;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClientDelegate;
import au.com.philology.tcpudp.IClientDelegate;
import au.com.philology.tcpudp.IScanDelegate;

public class SwitchingToClientModeDialog extends SwitchingWorkingModeDialog implements IScanDelegate, IClientDelegate, IClientConnectionTestDelegate
{
    boolean bServerFound = false;
    int current = 0;

    public SwitchingToClientModeDialog(Activity activity, TextView tvMsg, ProgressBar theProgressBar, View theContentView, ISwitchWorkingModeDelegate delegate,
                                       boolean bSilent)
    {
        super(activity, tvMsg, theProgressBar, theContentView, delegate, bSilent);
        mode = TypeWorkingMode.CLIENT_MODE;
        theProgressBar.setMax(255);
    }

    public void changeMode()
    {
        reset();
        updateMsg("Initializing as a client...");
        Common.sleep(DELAY);

        TheTCPClient.getSharedInstance().clientTestDelegate = this;
        TheTCPClientDelegate.getSharedInstance().clientDelegate = this;
        TheTCPClientDelegate.getSharedInstance().scanDelegate = this;

        Server theServer = Server.getServer();
        if (theServer != null)
        {
            updateMsg("Connecting to the Server...");
            TheTCPClient.getSharedInstance().connectionTest(theServer.server_ip, theServer.server_port);
        } else
        {
            updateMsg("Searching for the Server...");
            TheTCPClient.getSharedInstance().search();
        }
    }

    @Override
    public void ConnectionDelegateConnected(String address, int port)
    {
        this.uploadData();
    }

    @Override
    public void ConnectionDelegateLostConnection(String address, int port)
    {

    }

    @Override
    public void ClientConnectionTestPass(String ip, int Port)
    {
        updateMsg("Connected.");
        Common.sleep(DELAY);

        TheTCPClient.getSharedInstance().connect(ip, Port);
    }

    @Override
    public void ClientConnectionTestFailed(String ip, int Port)
    {
        this.updateMsg("Connection failed.");
        Common.sleep(DELAY);

        this.updateMsg("Searching for the Server...");
        Common.sleep(DELAY);

        TheTCPClient.getSharedInstance().search();
    }

    @Override
    public void ClientDelegateMessageReceived(String msg, String address, int port)
    {

    }

    @Override
    public void ScanDelegateIsScanningIpForPort(String serverAddress, int port)
    {
        this.updateMsg("Searching for the Server - " + serverAddress + ":" + port);
        this.updateProgressBarValue(current++);
    }

    @Override
    public void ScanDelegateIpFoundForPort(String serverAddress, int port)
    {
        this.updateMsg("Server found at " + serverAddress + ":" + port);

        TheTCPClient.getSharedInstance().stopScaningIpForPort();

        if (!TheTCPClient.getSharedInstance().isConnected())
            TheTCPClient.getSharedInstance().connect(serverAddress, port);

        this.bServerFound = true;
    }

    @Override
    public void ScanDelegateCompleteScanningIpForPort()
    {
        this.updateMsg("Complete Server Searching.");
        this.updateProgressBarValue(this.theProgressBar.getMax());
        this.runOnUIThread(new Runnable()
        {

            @Override
            public void run()
            {
                SwitchingToClientModeDialog.this.theProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        if (!this.bServerFound)
            this.complete();
    }

    @Override
    public void ScanDelegateStartScanningIpForPort()
    {
        this.updateMsg("Searching for the Server...");
        this.runOnUIThread(new Runnable()
        {

            @Override
            public void run()
            {
                SwitchingToClientModeDialog.this.theProgressBar.setVisibility(View.VISIBLE);
            }
        });
        this.updateProgressBarValue(0);
    }

    @Override
    public void ScanDelegateStartScanningPortForIp(String ip, int startPort, int endPort)
    {

    }

    @Override
    public void ScanDelegateIsScanningPortForIp(String ip, int port)
    {
        if (SwitchingWorkingModeDialog.bStop)
        {
            this.skipCurrentProcess();
            SwitchingWorkingModeDialog.bStop = false;
        }
    }

    @Override
    public void ScanDelegatePortFoundForIp(String ip, int port)
    {

    }

    @Override
    public void ScanDelegateCompleteScanningPortForIp(String ip, int startPort, int endPort)
    {

    }

    void uploadData()
    {
        // upload current content in the database to the server
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String serverIp = TheTCPClient.getSharedInstance().getServerIp();
            int serverPort = TheTCPClient.getSharedInstance().getServerPort();
            Server theServer = new Server(serverIp, serverPort, new Date().getTime());
            Server.updateServer(theServer);

            Common.sleep(DELAY);
            updateMsg("Pushing all coffee types...");
            CoffeeTypeSyncClient.getSharedInstance().pushAll();
            Common.sleep(DELAY);
            updateMsg("Request all coffee types...");
            CoffeeTypeSyncClient.getSharedInstance().requestAll();
            Common.sleep(DELAY);
            updateMsg("Pushing all users...");
            UserSyncClient.getSharedInstance().pushAll();
            Common.sleep(DELAY);
            updateMsg("Request all users...");
            UserSyncClient.getSharedInstance().requestAll();
            Common.sleep(DELAY);
            updateMsg("Pushing all preferences...");
            PreferenceSyncClient.getSharedInstance().pushAll();
            Common.sleep(DELAY);
            updateMsg("Request all preferences...");
            PreferenceSyncClient.getSharedInstance().requestAll();
            Common.sleep(DELAY);
            updateMsg("Pushing all orders...");
            OrderSyncClient.getSharedInstance().pushAll();
            Common.sleep(DELAY);
            updateMsg("Request all orders...");
            OrderSyncClient.getSharedInstance().requestAll();
        }

        this.complete();
    }

    public void skipCurrentProcess()
    {
        TheTCPClient.getSharedInstance().stopScaningIpForPort();
        TheTCPClient.getSharedInstance().stopScanningPortForIp();
    }
}
