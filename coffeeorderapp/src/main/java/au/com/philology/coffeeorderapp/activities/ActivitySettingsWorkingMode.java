package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.dialogs.ISwitchWorkingModeDelegate;
import au.com.philology.coffeeorderapp.dialogs.SwitchingToClientModeDialog;
import au.com.philology.coffeeorderapp.dialogs.SwitchingToServerModeDialog;
import au.com.philology.coffeeorderapp.dialogs.SwitchingToSingleModeDialog;
import au.com.philology.coffeeorderapp.dialogs.SwitchingWorkingModeDialog;
import au.com.philology.coffeeorderapp.monitor.TheWorkingModeMonitor;
import au.com.philology.common.utils;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class ActivitySettingsWorkingMode extends BasicActivity implements IDialogConfirmDelegate, ISwitchWorkingModeDelegate, OnCheckedChangeListener,
        OnValueChangeListener
{
    static final int TAG_CHANGE_MODE = 100;
    Button btnApply;
    TextView tvWorkMode, tvMonitoringPeriod;
    RadioGroup rgWorkingMode;
    CheckBox cbAutoMonitoring;
    NumberPicker theNumberPicker;
    ArrayList<Integer> values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_workingmode);

        this.btnApply = (Button) this.findViewById(R.id.btnApply);
        this.btnApply.setOnClickListener(this);

        this.tvWorkMode = (TextView) this.findViewById(R.id.tvWorkMode);
        this.rgWorkingMode = (RadioGroup) this.findViewById(R.id.RGWorkingMode);

        this.cbAutoMonitoring = (CheckBox) this.findViewById(R.id.cbAutoMonitoring);
        this.cbAutoMonitoring.setChecked(Model.isAutoCheckWorkingMode(this));
        this.cbAutoMonitoring.setOnCheckedChangeListener(this);

        this.tvMonitoringPeriod = (TextView) this.findViewById(R.id.tvMonitoringTime);

        for (int i = 1; i <= Common.WORKING_MODE_CHECK_INTERVAL_COUNT; i++)
        {
            int num = i * Common.WORKING_MODE_CHECK_INTERVAL_STEP_IN_SECONDS;
            this.values.add(num);
        }

        this.theNumberPicker = (NumberPicker) this.findViewById(R.id.numberPicker1);
        android.widget.NumberPicker.Formatter theFormatter = new android.widget.NumberPicker.Formatter()
        {

            @Override
            public String format(int value)
            {
                return ActivitySettingsWorkingMode.this.getValue(value);
            }
        };

        this.theNumberPicker.setFormatter(theFormatter);
        this.theNumberPicker.setMaxValue(Common.WORKING_MODE_CHECK_INTERVAL_COUNT);
        this.theNumberPicker.setMinValue(1);
        int seconds = Model.getAutoMonitoringWorkingModePeriod(this);
        int position = seconds / Common.WORKING_MODE_CHECK_INTERVAL_STEP_IN_SECONDS;
        this.theNumberPicker.setValue(position);

        // small hack trick to temporarily solve a google bug for numberpicker
        // control
        try
        {
            java.lang.reflect.Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(this.theNumberPicker);
            inputText.setFilters(new InputFilter[0]);
        } catch (Exception e)
        {
            Log.i("DEBUG", "Exception: " + e);
        }

        this.theNumberPicker.setOnValueChangedListener(this);
        this.theNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        this.theNumberPicker.setVisibility(this.cbAutoMonitoring.isChecked() ? View.VISIBLE : View.INVISIBLE);
        this.tvMonitoringPeriod.setVisibility(this.cbAutoMonitoring.isChecked() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);

        this.updateMode();
    }

    void updateMode()
    {
        TypeWorkingMode mode = WorkingMode.getWorkingMode();

        String text = "";
        if (mode == TypeWorkingMode.SINGLE_MODE)
        {
            text = "Single mode";
            View button = this.rgWorkingMode.getChildAt(2);
            this.rgWorkingMode.check(button.getId());
        } else if (mode == TypeWorkingMode.CLIENT_MODE)
        {
            text = "Client mode";
            View button = this.rgWorkingMode.getChildAt(1);
            this.rgWorkingMode.check(button.getId());
        } else if (mode == TypeWorkingMode.SERVER_MODE)
        {
            text = "Server mode";
            View button = this.rgWorkingMode.getChildAt(0);
            this.rgWorkingMode.check(button.getId());
        }

        this.tvWorkMode.setText(text);
    }

    void applyChange()
    {
        int selected = rgWorkingMode.getCheckedRadioButtonId();
        View button = rgWorkingMode.findViewById(selected);
        selected = rgWorkingMode.indexOfChild(button);

        LayoutInflater factory = LayoutInflater.from(this);
        View textView = factory.inflate(R.layout.progress_dialog_message, null);
        TextView tvMsg = (TextView) textView.findViewById(R.id.tvMessage);
        ProgressBar pb = (ProgressBar) textView.findViewById(R.id.progressBar);

        if (selected == 0)
        {
            SwitchingWorkingModeDialog aDialog = new SwitchingToServerModeDialog(this, tvMsg, pb, textView, this, false);
            aDialog.start();
        } else if (selected == 1)
        {
            SwitchingWorkingModeDialog aDialog = new SwitchingToClientModeDialog(this, tvMsg, pb, textView, this, false);
            aDialog.start();
        } else if (selected == 2)
        {
            SwitchingWorkingModeDialog aDialog = new SwitchingToSingleModeDialog(this, tvMsg, pb, textView, this, false);
            aDialog.start();
        }
    }

    String getValue(int index)
    {
        if (index >= 1 && index <= Common.WORKING_MODE_CHECK_INTERVAL_COUNT)
        {
            int seconds = ActivitySettingsWorkingMode.this.values.get(index - 1);
            if (seconds >= 60)
            {
                int minutes = seconds / 60;
                int remainder = seconds - 60 * minutes;
                return minutes + " minute" + (minutes > 1 ? "s" : "") + (remainder > 0 ? " " + remainder + " second" + (remainder > 1 ? "s" : "") : "");
            } else
                return seconds + " seconds";
        } else
            return "Out of range";
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnApply))
        {
            String text = "";

            // check the working mode
            TypeWorkingMode oldMode = WorkingMode.getWorkingMode();
            TypeWorkingMode mode = TypeWorkingMode.SINGLE_MODE;

            int selected = rgWorkingMode.getCheckedRadioButtonId();
            View button = rgWorkingMode.findViewById(selected);
            selected = rgWorkingMode.indexOfChild(button);

            if (selected == 0)
                mode = TypeWorkingMode.SERVER_MODE;
            else if (selected == 1)
                mode = TypeWorkingMode.CLIENT_MODE;
            else if (selected == 2)
                mode = TypeWorkingMode.SINGLE_MODE;

            if (oldMode != mode)
            {
                if (mode == TypeWorkingMode.SERVER_MODE)
                    text = "Switch to Server Mode!";
                else if (mode == TypeWorkingMode.CLIENT_MODE)
                    text = "Switch to Client Mode!";
                else
                    text = "Switch to Single Mode!";

                DialogConfirm aDialog = new DialogConfirm(this, "Confirm", text, false, 0, this, TAG_CHANGE_MODE);
                aDialog.show();
            } else
                Toast.makeText(this, "No change made.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        if (SwitchingWorkingModeDialog.bInProgress)
        {
            Toast.makeText(this, "Working mode monitoring is in progress. stopping...", Toast.LENGTH_LONG).show();
            SwitchingWorkingModeDialog.requestStop();
        }

        this.applyChange();
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
    {

    }

    @Override
    public void switchWorkingModeDialogModeChanged(TypeWorkingMode mode)
    {
        utils.showTempInfor(this, "Switch mode succeed.");
        this.updateMode();
    }

    @Override
    public void switchWorkingModeDialogModeChangingFailed(TypeWorkingMode targetMode)
    {
        utils.showDialog(this, "Error", "Switching mode failed!");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Model.setAutoCheckWorkingMode(this, isChecked);

        if (isChecked)
            TheWorkingModeMonitor.getSharedInstance().startMonitoring(this);
        else
            TheWorkingModeMonitor.getSharedInstance().stopMonitoring();

        this.theNumberPicker.setVisibility(this.cbAutoMonitoring.isChecked() ? View.VISIBLE : View.INVISIBLE);
        this.tvMonitoringPeriod.setVisibility(this.cbAutoMonitoring.isChecked() ? View.VISIBLE : View.INVISIBLE);
        Toast.makeText(this, "Working Mode Auto Monitoring is " + (isChecked ? " Enabled." : " Disabled!"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
    {
        if (newVal >= 1 && newVal <= Common.WORKING_MODE_CHECK_INTERVAL_COUNT)
        {
            int value = this.values.get(newVal - 1);
            Log.i("DEBUG", "Value changed: " + value);

            Model.setAutoMonitoringWorkingModePeriod(this, value);
            TheWorkingModeMonitor.getSharedInstance().startMonitoring(this);
        } else
            Log.i("DEBUG", "Out of range");
    }
}
