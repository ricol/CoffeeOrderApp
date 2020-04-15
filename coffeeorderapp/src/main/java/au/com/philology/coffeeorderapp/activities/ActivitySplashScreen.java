package au.com.philology.coffeeorderapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.CloudOrder;
import au.com.philology.coffeeorderapp.database.DB;
import au.com.philology.coffeeorderapp.database.Printer;
import au.com.philology.coffeeorderapp.database.Server;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.database.sync.DBObjectSync;
import au.com.philology.coffeeorderapp.database.sync.client.CoffeeTypeSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.OrderSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.PreferenceSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.UserSyncClient;
import au.com.philology.coffeeorderapp.datasource.cloudorders.TheCloudOrdersService;
import au.com.philology.coffeeorderapp.netconnection.IClientConnectionTestDelegate;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClientDelegate;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;
import au.com.philology.coffeeorderapp.printer.IPrinterDelegate;
import au.com.philology.coffeeorderapp.printer.ThePrinter;
import au.com.philology.tcpudp.IClientDelegate;
import au.com.philology.tcpudp.IPAddress;
import au.com.philology.tcpudp.IScanDelegate;

public class ActivitySplashScreen extends BasicActivity implements IPrinterDelegate, IScanDelegate, IClientDelegate, IClientConnectionTestDelegate
{
    final int DELAY = 500;
    boolean bCancel = false;
    boolean bServerFound = false;
    TextView tvProgress;
    Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.splash_screen);
        this.tvProgress = (TextView) this.findViewById(R.id.tvProgress);
        this.btnSkip = (Button) this.findViewById(R.id.btnSkip);
        this.btnSkip.setOnClickListener(this);
        this.btnSkip.setVisibility(View.INVISIBLE);

        IPAddress.theContext = this;

    }

    @Override
    public void onBackPressed()
    {

        Toast.makeText(this, "back is disabled during the initializing process!", Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        DBObjectSync.theContext = this;
        new BackgroundThread().start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.imgBtnHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);
        this.imageViewBottomBar.setVisibility(View.INVISIBLE);
        this.imageViewTopBar.setVisibility(View.INVISIBLE);
        // this.btnGoBack.setVisibility(View.INVISIBLE);
    }

    @Override
    public void finish()
    {
        DBObjectSync.theContext = null;

        super.finish();
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnSkip))
        {
            TheTCPClient.getSharedInstance().stopScaningIpForPort();
            TheTCPClient.getSharedInstance().stopScanningPortForIp();
            ThePrinter.getSharedInstance().stopScanning();
            this.btnSkip.setVisibility(View.INVISIBLE);
        }
    }

    void updateMsg(String msg)
    {
        final String tmpMsg = msg;
        ActivitySplashScreen.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                tvProgress.setText(tmpMsg);
            }
        });
    }

    void updateHiddenButtonVisibility(int visibility)
    {
        final int vi = visibility;

        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ActivitySplashScreen.this.btnSkip.setVisibility(vi);
            }
        });
    }

    @Override
    public void PrinterConnected(String ip, int Port)
    {

    }

    @Override
    public void PrinterLostConnection(String ip, int Port)
    {

    }

    public void continueProcess()
    {
        updateMsg("Initializing...");

        TypeWorkingMode mode = WorkingMode.getWorkingMode();

        if (mode == TypeWorkingMode.SINGLE_MODE)
        {
            Common.sleep(1000);
            updateMsg("Initializing as single mode...");
            Common.sleep(DELAY);
            this.abouttoRun();
        } else if (mode == TypeWorkingMode.SERVER_MODE)
        {
            // server mode
            Common.sleep(1000);
            updateMsg("Initializing as a server...");
            Common.sleep(DELAY);
            TheTCPServer.getSharedInstance().startListening(Server.PORT);
            this.abouttoRun();
        } else if (mode == TypeWorkingMode.CLIENT_MODE)
        {
            // client mode
            Common.sleep(DELAY);
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
        } else
        {
            this.abouttoRun();
        }
    }

    void uploadData()
    {
        // upload current content in the database to the server
        if (WorkingMode.getWorkingMode() == TypeWorkingMode.CLIENT_MODE)
        {
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
        }

        this.abouttoRun();
    }

    void abouttoRun()
    {
        ActivitySplashScreen.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (bCancel)
                    return;

                Intent intent = new Intent(ActivitySplashScreen.this, ActivityMain.class);
                ActivitySplashScreen.this.startActivity(intent);
                ActivitySplashScreen.this.finish();
            }
        });

        if (CloudOrder.getCloudOrders())
            TheCloudOrdersService.getSharedInstance().start();
    }

    class BackgroundThread extends Thread
    {
        @Override
        public void run()
        {
            updateMsg("Initializing the database...");

            Context theContext = getApplicationContext();

            if (DB.getSharedInstance() == null)
                DB.theInstance = new DB(theContext);

            Common.sleep(DELAY);

            updateMsg("Updating the coffee type database...");

            Model.getSharedInstance().write();

            Common.sleep(DELAY);

            updateMsg("Coffee type database updated.");

            Common.sleep(DELAY);

            ThePrinter.getSharedInstance().delegate = ActivitySplashScreen.this;

            Printer printer = Printer.getPrinter();
            if (printer != null)
            {
                updateMsg("Connecting to the Printer...");
                ThePrinter.getSharedInstance().connectionTest(printer.printer_ip, printer.printer_port);
            } else
            {
                updateMsg("Searching for the Printer...");
                Common.sleep(DELAY);
                ThePrinter.getSharedInstance().startScanning();
            }
        }
    }

    @Override
    public void PrinterConnectionTestPass(String ip, int Port)
    {
        updateMsg("Connected.");
        Common.sleep(DELAY);
        this.continueProcess();
    }

    @Override
    public void PrinterConnectionTestFailed(String ip, int Port)
    {
        updateMsg("Connection failed.");
        Common.sleep(DELAY);
        updateMsg("Searching for the Printer...");
        Common.sleep(DELAY);
        ThePrinter.getSharedInstance().startScanning();
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
    public void ClientDelegateMessageReceived(String msg, String address, int port)
    {

    }

    @Override
    public void ScanDelegateIsScanningIpForPort(String serverAddress, int port)
    {
        this.updateMsg("Searching for the Server - " + serverAddress + ":" + port);
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
        this.updateHiddenButtonVisibility(View.INVISIBLE);

        if (!this.bServerFound)
            this.abouttoRun();
    }

    @Override
    public void ScanDelegateStartScanningIpForPort()
    {
        this.updateMsg("Searching for the Server...");
        this.updateHiddenButtonVisibility(View.VISIBLE);
    }

    @Override
    public void ScanDelegateStartScanningPortForIp(String ip, int startPort, int endPort)
    {

    }

    @Override
    public void ScanDelegateIsScanningPortForIp(String ip, int port)
    {

    }

    @Override
    public void ScanDelegatePortFoundForIp(String ip, int port)
    {

    }

    @Override
    public void ScanDelegateCompleteScanningPortForIp(String ip, int startPort, int endPort)
    {

    }

    @Override
    public void PrinterStartScanning(int startIP, int endIP, int port, int timeout)
    {
        this.updateMsg("Searching for the Printer...");
        this.updateHiddenButtonVisibility(View.VISIBLE);
    }

    @Override
    public void PrinterEndScanning(int startIP, int endIP, int port, int timeout)
    {
        this.updateMsg("Complete printer searching.");
        this.updateHiddenButtonVisibility(View.INVISIBLE);
        this.continueProcess();
    }

    @Override
    public void PrinterIsScanning(String ip, int port, int timeout)
    {
        this.updateMsg("Searching for the Printer - " + ip + ":" + port);
    }

    @Override
    public void PrinterIpFoundForThePrinter(String ip, int port, int timeout)
    {
        this.updateMsg("Printer found at " + ip + ":" + port);

        Common.sleep(DELAY);
        Printer printer = new Printer(ip, port, (new Date()).getTime());
        Printer.updatePrinter(printer);

        ThePrinter.getSharedInstance().stopScanning();
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
}
