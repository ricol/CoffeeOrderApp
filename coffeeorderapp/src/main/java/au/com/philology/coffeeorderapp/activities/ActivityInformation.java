package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.database.DeveloperMode;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class ActivityInformation extends BasicActivity implements IDialogConfirmDelegate
{
    static final int TAG_ACCESS_SETTINGS = 100;
    static final int TAG_ACCESS_ADMINISTRATION = 101;

    ImageButton btnSettings, btnAdministrator;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.information);

        this.btnSettings = (ImageButton) this.findViewById(R.id.imageBtnSettings);
        this.btnSettings.setOnClickListener(this);

        this.btnAdministrator = (ImageButton) this.findViewById(R.id.ImageButtonAdministration);
        this.btnAdministrator.setOnClickListener(this);

        this.tvVersion = (TextView) this.findViewById(R.id.tvVersion);

        try
        {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            this.tvVersion.setText("Version: " + info.versionName);
        } catch (NameNotFoundException e)
        {
            this.tvVersion.setText("Version: Error!");
        }
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        super.onClick(v);

        if (v.equals(this.btnAdministrator))
        {
            if (DeveloperMode.isDeveloperMode())
            {
                Intent tmpIntent = new Intent(ActivityInformation.this, ActivityAdministrator.class);
                startActivity(tmpIntent);
                finish();
            } else
            {
                DialogConfirm aDialog = new DialogConfirm(this, "Administrator Account Required!", "", true, R.layout.alert_dialog_text_entry, this,
                        TAG_ACCESS_ADMINISTRATION);
                aDialog.show();
            }
        } else if (v.equals(this.btnSettings))
        {
            if (DeveloperMode.isDeveloperMode())
            {
                Intent tmpIntent = new Intent(ActivityInformation.this, ActivitySettings.class);
                startActivity(tmpIntent);
                finish();
            } else
            {
                DialogConfirm aDialog = new DialogConfirm(this, "Administrator Account Required!", "", true, R.layout.alert_dialog_text_entry, this,
                        TAG_ACCESS_SETTINGS);
                aDialog.show();
            }
        }
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theView)
    {
        if (theDialog.tag == TAG_ACCESS_SETTINGS)
        {
            final EditText etUsername = (EditText) theView.findViewById(R.id.etUsername);
            final EditText etPassword = (EditText) theView.findViewById(R.id.etPassword);
            String username = ActivityInformation.this.getString(R.string.administratorSettingsUsername);
            String password = ActivityInformation.this.getString(R.string.administratorSettingsPassword);
            if (etUsername.getText().toString().equals(username) && etPassword.getText().toString().equals(password))
            {
                Intent tmpIntent = new Intent(ActivityInformation.this, ActivitySettings.class);
                startActivity(tmpIntent);
                finish();
            } else
            {
                Toast.makeText(ActivityInformation.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
            }
        } else if (theDialog.tag == TAG_ACCESS_ADMINISTRATION)
        {
            final EditText etUsername = (EditText) theView.findViewById(R.id.etUsername);
            final EditText etPassword = (EditText) theView.findViewById(R.id.etPassword);

            String username = ActivityInformation.this.getString(R.string.administratorOrderUsername);
            String password = ActivityInformation.this.getString(R.string.administratorOrderPassword);
            if (etUsername.getText().toString().equals(username) && etPassword.getText().toString().equals(password))
            {
                Intent tmpIntent = new Intent(ActivityInformation.this, ActivityAdministrator.class);
                startActivity(tmpIntent);
                finish();
            } else
            {
                Toast.makeText(ActivityInformation.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theView)
    {

    }
}
