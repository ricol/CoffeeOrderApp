package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.Printer;
import au.com.philology.coffeeorderapp.printer.IPrinterDelegate;
import au.com.philology.coffeeorderapp.printer.ThePrinter;
import au.com.philology.common.ValueValidator;
import au.com.philology.common.utils;
import au.com.philology.controls.AutoscrollListView;
import au.com.philology.dialogs.DialogProgress;

public class ActivitySettingsPrinter extends BasicActivity implements IPrinterDelegate, OnItemClickListener, OnCheckedChangeListener
{
    Button btnApply, btnTestPrinter, btnSearch;
    EditText etPrinterIp, etPrinterPort;
    AutoscrollListView lvAllPrinters;
    ArrayAdapter<String> mArrayAdapter;
    TextView tvSearchingMsg;
    CheckBox cbTestPage, cbPrintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_printer);

        this.btnApply = (Button) this.findViewById(R.id.btnApply);
        this.btnApply.setOnClickListener(this);

        this.btnTestPrinter = (Button) this.findViewById(R.id.btnTestPrinter);
        this.btnTestPrinter.setOnClickListener(this);

        this.etPrinterIp = (EditText) this.findViewById(R.id.etPrinterIP);
        this.etPrinterPort = (EditText) this.findViewById(R.id.etPrinterPort);

        Printer printer = Printer.getPrinter();
        if (printer != null)
        {
            this.etPrinterIp.setText(printer.printer_ip);
            this.etPrinterPort.setText(Integer.toString(printer.printer_port));
        }

        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_view_item);
        this.lvAllPrinters = (AutoscrollListView) this.findViewById(R.id.lvAllPrinters);
        this.lvAllPrinters.setAdapter(mArrayAdapter);
        this.lvAllPrinters.setOnItemClickListener(this);

        this.btnSearch = (Button) this.findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(this);

        this.tvSearchingMsg = (TextView) this.findViewById(R.id.tvSearchingMsg);

        this.cbTestPage = (CheckBox) this.findViewById(R.id.cbTestPage);
        this.cbTestPage.setChecked(true);

        this.cbPrintButton = (CheckBox) this.findViewById(R.id.cbPrintButton);
        this.cbPrintButton.setChecked(Model.isPrintButtonVisible(this));
        this.cbPrintButton.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();

        ThePrinter.getSharedInstance().stopScanning();
    }

    @Override
    public void PrinterConnectionTestPass(String ip, int Port)
    {
        this.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                utils.showDialog(ActivitySettingsPrinter.this, "Message", "The printer is available.");
                if (cbTestPage.isChecked())
                {
                    String text = Common.getPrinterReceiptFormat("Test", "Test", "Test", "Test", "Test", "Test", "Test", new Date().toString());
                    ThePrinter.getSharedInstance().print(text);
                }
            }

        });
    }

    @Override
    public void PrinterConnectionTestFailed(String ip, int Port)
    {
        this.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                utils.showDialog(ActivitySettingsPrinter.this, "Message", "The printer is NOT available!");
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

    @Override
    public void PrinterStartScanning(int startIP, int endIP, int port, int timeout)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mArrayAdapter.clear();
                btnSearch.setText("Stop searching");
            }
        });
    }

    @Override
    public void PrinterEndScanning(int startIP, int endIP, int port, int timeout)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tvSearchingMsg.setText("");
                btnSearch.setText("Search");
            }
        });
    }

    @Override
    public void PrinterIsScanning(String ip, int port, int timeout)
    {
        final String tmpIP = ip;
        final int tmpPort = port;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tvSearchingMsg.setText("Searching ... " + tmpIP + ":" + tmpPort);
            }
        });
    }

    @Override
    public void PrinterIpFoundForThePrinter(String ip, int port, int timeout)
    {
        final String tmpIP = ip;
        final int tmpPort = port;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mArrayAdapter.add("Printer found at:" + tmpIP + ":" + tmpPort);
                lvAllPrinters.scrollToBottom();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnApply))
        {
            String strIp = etPrinterIp.getText().toString();
            if (strIp == null || strIp.equals(""))
            {
                utils.showTempInfor(this, "Not specify a valid printer IP address.");
                return;
            }

            String strPort = etPrinterPort.getText().toString();
            if (strPort == null || strPort.equals(""))
            {
                utils.showTempInfor(this, "Not specify a valid printer port.");
                return;
            }

            if (!ValueValidator.isValidIP(etPrinterIp.getText().toString()))
            {
                utils.showDialog(this, "ERROR", "Invalid Printer IP!");
                return;
            }

            if (!ValueValidator.isValidInteger(etPrinterPort.getText().toString()))
            {
                utils.showDialog(this, "ERROR", "Invalid Printer Port!");
                return;
            }

            // check the printer
            int port = Integer.parseInt(strPort);
            boolean bShouldUpdatePrinter = true;

            Printer oldPrinter = Printer.getPrinter();
            if (oldPrinter != null && (oldPrinter.printer_ip.equals(strIp) && oldPrinter.printer_port == port))
            {
                bShouldUpdatePrinter = false;
            }

            if (bShouldUpdatePrinter)
            {
                Printer printer = new Printer(strIp, port, (new Date()).getTime());
                Printer.updatePrinter(printer);
                Toast.makeText(this, "New printer: " + strIp + "(" + port + ")", Toast.LENGTH_LONG).show();
            } else
            {
                Toast.makeText(this, "No change made.", Toast.LENGTH_LONG).show();
            }

        } else if (v.equals(this.btnTestPrinter))
        {
            DialogProgress aDialog = new DialogProgress(this, null, "Test", "Connecting...", ThePrinter.CONNECTION_TIMEOUT / 1000, true);
            aDialog.start();

            new Thread(new Runnable()
            {
                public void run()
                {
                    ThePrinter.getSharedInstance().delegate = ActivitySettingsPrinter.this;
                    ThePrinter.getSharedInstance().connectionTest(etPrinterIp.getText().toString(), Printer.PORT);
                }
            }).start();
        } else if (v.equals(this.btnSearch))
        {
            ThePrinter.getSharedInstance().delegate = this;
            if (this.btnSearch.getText().toString().equalsIgnoreCase("Search"))
                ThePrinter.getSharedInstance().startScanning();
            else
                ThePrinter.getSharedInstance().stopScanning();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String text = mArrayAdapter.getItem(position);
        String[] data = text.split(":");
        String ip = data[1];
        String port = data[2];

        this.etPrinterIp.setText(ip);
        this.etPrinterPort.setText(port);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.equals(this.cbPrintButton))
        {
            Model.setPrintButtonVisibility(this, isChecked);

            Toast.makeText(this, "Print Button is " + (isChecked ? " Visible." : " Hidden!"), Toast.LENGTH_LONG).show();
        }
    }
}
