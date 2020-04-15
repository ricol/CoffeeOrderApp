package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TagDataSourceType;
import au.com.philology.coffeeorderapp.database.CloudOrder;
import au.com.philology.coffeeorderapp.database.DataSource;
import au.com.philology.coffeeorderapp.datasource.ICardReaderDelegate;
import au.com.philology.coffeeorderapp.datasource.TheDataSource;
import au.com.philology.coffeeorderapp.datasource.cloudorders.ICloudOrderDelegate;
import au.com.philology.coffeeorderapp.datasource.cloudorders.TheCloudOrdersService;
import au.com.philology.controls.AutoscrollListView;

public class ActivitySettingsDataSource extends BasicActivity implements ICardReaderDelegate, OnCheckedChangeListener, ICloudOrderDelegate
{
    Button btnApply, btnEditFilter;
    AutoscrollListView listViewMsg;
    ArrayAdapter<String> mArrayAdapterMsg;
    RadioGroup rgReaderSource;
    CheckBox cbProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_data_source);

        this.btnApply = (Button) this.findViewById(R.id.btnApply);
        this.btnApply.setOnClickListener(this);

        this.listViewMsg = (AutoscrollListView) this.findViewById(R.id.listViewMessage);
        this.mArrayAdapterMsg = new ArrayAdapter<String>(this, R.layout.list_view_item);
        this.listViewMsg.setAdapter(this.mArrayAdapterMsg);

        this.cbProcess = (CheckBox) this.findViewById(R.id.cbProcessCloudOrders);
        this.cbProcess.setOnCheckedChangeListener(this);

        this.btnEditFilter = (Button) this.findViewById(R.id.btnEditFilter);
        this.btnEditFilter.setOnClickListener(this);

        this.rgReaderSource = (RadioGroup) this.findViewById(R.id.rgReaderType);
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();

        this.refreshDataSource();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);

        TheDataSource.getSharedInstance().setCardReaderDelegate(this);

        this.cbProcess.setChecked(CloudOrder.getCloudOrders());
        TheCloudOrdersService.getSharedInstance().addDelegate(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        TheDataSource.getSharedInstance().setCardReaderDelegate(null);

        TheCloudOrdersService.getSharedInstance().removeDelegate(this);
    }

    @Override
    public void finish()
    {
        // TODO Auto-generated method stub
        super.finish();

        TheCloudOrdersService.getSharedInstance().removeDelegate(this);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        setIntent(intent);
        TheDataSource.getSharedInstance().resolveIntent(intent);
    }

    void refreshDataSource()
    {
        TagDataSourceType readerType = DataSource.getReaderType();
        String text = "";
        if (readerType == TagDataSourceType.EXTERNAL_READER)
        {
            text = "External Reader";
            View button = this.rgReaderSource.getChildAt(0);
            this.rgReaderSource.check(button.getId());
        } else if (readerType == TagDataSourceType.USB_SERIAL_READER)
        {
            text = "USB Serial Reader";
            View button = this.rgReaderSource.getChildAt(1);
            this.rgReaderSource.check(button.getId());
        } else
        {
            text = "Undefined!";
        }

        System.out.println(text);
    }

    @Override
    public void CardReaderTagDetected(String text, String tag)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text + ":" + tag);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void CardReaderLostConnection(String text)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void CardReaderConnected(String text)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void CardReaderStateChanged(String state)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + state);
        this.listViewMsg.scrollToBottom();

    }

    @Override
    public void CardReaderOperationCancelled(String text)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void CardReaderTryToConnect(String text)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void CardReaderTryToDisconnect(String text)
    {
        this.mArrayAdapterMsg.add("[Local data source]" + text);
        this.listViewMsg.scrollToBottom();
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnApply))
        {
            String text = "";

            // check the RFID reader source
            TagDataSourceType oldType = DataSource.getReaderType();
            int source = this.rgReaderSource.getCheckedRadioButtonId();
            Button button = (Button) this.rgReaderSource.findViewById(source);
            source = this.rgReaderSource.indexOfChild(button);
            TagDataSourceType sourceType = Common.IntToReaderType(source);

            if (oldType != sourceType)
            {
                DataSource.updateRFIDReaderType(sourceType, (new Date()).getTime());
                if (sourceType == TagDataSourceType.EXTERNAL_READER)
                    TheDataSource.switchToExternalRFIDReader();
                else if (sourceType == TagDataSourceType.USB_SERIAL_READER)
                    TheDataSource.switchToUSBSerialReader();

                TheDataSource.getSharedInstance().connect();
                TheDataSource.getSharedInstance().activityOnResume();

                this.refreshDataSource();

                if (!text.equals(""))
                    text += "\n";
                if (sourceType == TagDataSourceType.EXTERNAL_READER)
                    text += "New RFID Source: External RFID Reader!";
                else if (sourceType == TagDataSourceType.USB_SERIAL_READER)
                    text += "New RFID Source: USB SERIAL READER!";
            }

            if (!text.equals(""))
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "No change made.", Toast.LENGTH_LONG).show();
        } else if (v.equals(this.btnEditFilter))
        {
            Intent aIntent = new Intent(this, ActivitySettingsCloudOrdersFilter.class);
            this.startActivity(aIntent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
            TheCloudOrdersService.getSharedInstance().start();
        } else
        {
            TheCloudOrdersService.getSharedInstance().end();
        }

        CloudOrder.updateCloudOrder(isChecked, new Date().getTime());
    }

    @Override
    public void cloudOrderDataReceived(String data)
    {
        final String d = data;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mArrayAdapterMsg.add("[Remote data source]" + d);
                listViewMsg.scrollToBottom();
            }
        });
    }

    @Override
    public void cloudOrderStarted()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mArrayAdapterMsg.add(new Date() + ": Listening for remote data source started...");
                listViewMsg.scrollToBottom();
            }
        });
    }

    @Override
    public void cloudOrderEnded()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mArrayAdapterMsg.add(new Date() + ": stopped listening.");
                listViewMsg.scrollToBottom();
            }
        });
    }

    @Override
    public void cloudOrderNewOrder(String deviceId, String tagId)
    {

    }
}
