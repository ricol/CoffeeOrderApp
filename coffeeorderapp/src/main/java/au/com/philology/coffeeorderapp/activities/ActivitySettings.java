package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import net.hockeyapp.android.FeedbackManager;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.DB;
import au.com.philology.coffeeorderapp.database.DeveloperMode;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;
import au.com.philology.tcpudp.IPAddress;

public class ActivitySettings extends BasicActivity implements IDialogConfirmDelegate, OnCheckedChangeListener
{
    Button btnClearDatabase, btnServerOrClient, btnPrinter, btnDataSource, btnWorkingMode, btnFeedback, btnQuitApp;
    TextView tvLocalIp;
    CheckBox cbDeveloperMode;

    static final int TAG_CONFIRM_QUIT_APP = 100;
    static final int TAG_CONFIRM_CLEAR_DATABASE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings);

        String iIPv4 = IPAddress.getLocalAddress();
        this.tvLocalIp = (TextView) this.findViewById(R.id.tvIPValue);
        this.tvLocalIp.setText(iIPv4);

        this.btnClearDatabase = (Button) this.findViewById(R.id.btnClearDatabase);
        this.btnClearDatabase.setOnClickListener(this);

        this.btnPrinter = (Button) this.findViewById(R.id.btnPrinter);
        this.btnPrinter.setOnClickListener(this);

        this.btnDataSource = (Button) this.findViewById(R.id.btnDataSource);
        this.btnDataSource.setOnClickListener(this);

        this.btnWorkingMode = (Button) this.findViewById(R.id.btnWorkingMode);
        this.btnWorkingMode.setOnClickListener(this);

        this.btnServerOrClient = (Button) this.findViewById(R.id.btnServerOrClient);
        this.btnServerOrClient.setOnClickListener(this);

        this.btnFeedback = (Button) this.findViewById(R.id.btnFeedback);
        this.btnFeedback.setOnClickListener(this);

        this.btnQuitApp = (Button) this.findViewById(R.id.btnQuitApp);
        this.btnQuitApp.setOnClickListener(this);

        this.cbDeveloperMode = (CheckBox) this.findViewById(R.id.cbDeveloperMode);
        this.cbDeveloperMode.setChecked(DeveloperMode.isDeveloperMode());
        this.cbDeveloperMode.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnClearDatabase))
        {
            DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Clear the database?", false, 0, this, TAG_CONFIRM_CLEAR_DATABASE);
            aDialog.show();
        } else if (v.equals(this.btnServerOrClient))
        {
            if (WorkingMode.getWorkingMode() == TypeWorkingMode.CLIENT_MODE)
            {
                Intent aIntent = new Intent(this, ActivitySettingsServer.class);
                this.startActivity(aIntent);
            } else if (WorkingMode.getWorkingMode() == TypeWorkingMode.SERVER_MODE)
            {
                Intent aIntent = new Intent(this, ActivitySettingsClients.class);
                this.startActivity(aIntent);
            }
        } else if (v.equals(this.btnPrinter))
        {
            Intent aIntent = new Intent(this, ActivitySettingsPrinter.class);
            this.startActivity(aIntent);
        } else if (v.equals(this.btnWorkingMode))
        {
            Intent aIntent = new Intent(this, ActivitySettingsWorkingMode.class);
            this.startActivity(aIntent);
        } else if (v.equals(this.btnDataSource))
        {
            Intent aIntent = new Intent(this, ActivitySettingsDataSource.class);
            this.startActivity(aIntent);
        } else if (v.equals(this.btnFeedback))
        {
            FeedbackManager.register(this, Common.HOCKEY_APP_ID, null);
            FeedbackManager.showFeedbackActivity(this);
        } else if (v.equals(this.btnQuitApp))
        {
            DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Quit the App?", false, 0, this, TAG_CONFIRM_QUIT_APP);
            aDialog.show();
        }
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);

        this.btnServerOrClient.setVisibility(View.INVISIBLE);
        if (WorkingMode.getWorkingMode() == TypeWorkingMode.CLIENT_MODE)
        {
            this.btnServerOrClient.setText("SERVER");
            this.btnServerOrClient.setVisibility(View.VISIBLE);
        } else if (WorkingMode.getWorkingMode() == TypeWorkingMode.SERVER_MODE)
        {
            this.btnServerOrClient.setText("CLIENTS");
            this.btnServerOrClient.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        if (theDialog.tag == TAG_CONFIRM_CLEAR_DATABASE)
        {
            DB.theInstance.clear();
            Toast.makeText(this, "Database has been reset!", Toast.LENGTH_SHORT).show();
        } else if (theDialog.tag == TAG_CONFIRM_QUIT_APP)
        {
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
    {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.equals(this.cbDeveloperMode))
        {
            DeveloperMode.update(isChecked, new Date().getTime());

            Toast.makeText(this, "Developer Mode " + (isChecked ? " Enabled." : " Disabled!"), Toast.LENGTH_LONG).show();
        }
    }
}