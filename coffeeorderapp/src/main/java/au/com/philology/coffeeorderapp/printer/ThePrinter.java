package au.com.philology.coffeeorderapp.printer;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.database.Printer;
import au.com.philology.tcpudp.IClientDelegate;
import au.com.philology.tcpudp.IScanDelegate;
import au.com.philology.tcpudp.TcpClient;

public class ThePrinter implements IClientDelegate, IScanDelegate
{
    static String TAG = "ThePrinter";
    public static ThePrinter theInstance;
    public static int CONNECTION_TIMEOUT = 5000;
    TcpClient theClient;
    TcpClient theScannerPortClient;
    public IPrinterDelegate delegate;
    String text = "";

    public static ThePrinter getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new ThePrinter();
        return theInstance;
    }

    public ThePrinter()
    {
        this.theClient = new TcpClient(this, this);
    }

    public boolean isReady()
    {
        return theClient.isConnected();
    }

    public void connectionTest(String ip, int port)
    {
        this.theClient.connect(ip, port);

        Common.sleep(CONNECTION_TIMEOUT);

        if (this.theClient.isConnected())
        {
            this.theClient.disconnect();
            this.delegate.PrinterConnectionTestPass(ip, port);
        } else
        {
            this.delegate.PrinterConnectionTestFailed(ip, port);
        }
    }

    public void startScanning()
    {
        this.stopScanning();

        this.theScannerPortClient = new TcpClient(this, this);
        this.theScannerPortClient.startScaningIpForPort(Printer.PORT, Printer.IP_STASRT, Printer.IP_END, Printer.TIMEOUT);
    }

    public void stopScanning()
    {
        if (this.theScannerPortClient != null)
        {
            this.theScannerPortClient.stopScaningIpForPort();
            this.theScannerPortClient = null;
        }
    }

    public void print(String html)
    {
        this.text = html;

        if (this.isReady())
        {
            this.printText(this.text);
            this.text = "";
        } else
        {
            Printer printer = Printer.getPrinter();
            if (printer != null)
                this.theClient.connect(printer.printer_ip, printer.printer_port);
        }
    }

    @Override
    public void ConnectionDelegateConnected(String address, int port)
    {
        if (this.delegate != null)
            this.delegate.PrinterConnected(address, port);

        if (!this.text.equals(""))
        {
            this.printText(this.text);
            this.text = "";
            this.theClient.disconnect();
        }
    }

    @Override
    public void ConnectionDelegateLostConnection(String address, int port)
    {
        if (this.delegate != null)
            this.delegate.PrinterLostConnection(address, port);
    }

    @Override
    public void ScanDelegateIsScanningIpForPort(String serverAddress, int port)
    {
        if (this.delegate != null)
            this.delegate.PrinterIsScanning(serverAddress, port, Printer.TIMEOUT);

    }

    @Override
    public void ScanDelegateIpFoundForPort(String serverAddress, int port)
    {
        if (this.delegate != null)
            this.delegate.PrinterIpFoundForThePrinter(serverAddress, port, Printer.TIMEOUT);

    }

    @Override
    public void ScanDelegateCompleteScanningIpForPort()
    {
        if (this.delegate != null)
            this.delegate.PrinterEndScanning(Printer.IP_STASRT, Printer.IP_END, Printer.PORT, Printer.TIMEOUT);

    }

    @Override
    public void ScanDelegateStartScanningIpForPort()
    {
        if (this.delegate != null)
            this.delegate.PrinterStartScanning(Printer.IP_STASRT, Printer.IP_END, Printer.PORT, Printer.TIMEOUT);
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
    public void ClientDelegateMessageReceived(String msg, String address, int port)
    {

    }

    void printText(String text)
    {
        if (text != null)
        {
            this.theClient.send(text);

            char[] finish = new char[]
                    {29, 'V', 65, 0};
            this.theClient.send(finish);
        }
    }
}
