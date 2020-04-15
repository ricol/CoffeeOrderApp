package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import au.com.philology.coffeeorderapp.R;

public class ActivitySettingsScreen extends BasicActivity implements OnSeekBarChangeListener
{
    Button btnApply;
    EditText etSleeptime;
    TextView tvBrightnessValue;
    SeekBar sbBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_screen);

        this.btnApply = (Button) this.findViewById(R.id.btnApply);
        this.btnApply.setOnClickListener(this);
        this.tvBrightnessValue = (TextView) this.findViewById(R.id.tvBrightnessValue);
        this.etSleeptime = (EditText) this.findViewById(R.id.etSleepTime);
        this.sbBrightness = (SeekBar) this.findViewById(R.id.sbBrightness);
        this.sbBrightness.setOnSeekBarChangeListener(this);
        this.sbBrightness.setMax(255);

        int curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
        System.out.println("Current Systen Settings: " + curBrightnessValue);
        this.sbBrightness.setProgress(curBrightnessValue);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        System.out.println("Progress: " + progress);
        this.tvBrightnessValue.setText(Integer.toString(progress));

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = (progress * 1.0f) / this.sbBrightness.getMax();
        getWindow().setAttributes(layout);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
}