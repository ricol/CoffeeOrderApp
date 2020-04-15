package au.com.philology.coffeeorderapp.printer;

public interface IPrinterDelegate
{
    public void PrinterConnected(String ip, int Port);

    public void PrinterLostConnection(String ip, int Port);

    public void PrinterConnectionTestPass(String ip, int Port);

    public void PrinterConnectionTestFailed(String ip, int Port);

    public void PrinterStartScanning(int startIP, int endIP, int port, int timeout);

    public void PrinterEndScanning(int startIP, int endIP, int port, int timeout);

    public void PrinterIsScanning(String ip, int port, int timeout);

    public void PrinterIpFoundForThePrinter(String ip, int port, int timeout);
}
