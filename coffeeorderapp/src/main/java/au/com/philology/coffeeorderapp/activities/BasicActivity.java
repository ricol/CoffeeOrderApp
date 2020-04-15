package au.com.philology.coffeeorderapp.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import au.com.philology.coffeeorderapp.R;

public class BasicActivity extends Activity implements OnClickListener
{
    boolean bAlreadyRun = false;
    ImageButton imgBtnInfor, imgBtnHome, imgBtnIcon, imgBtnBack;
    ImageView imageViewTopBar, imageViewBottomBar;
    ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        bAlreadyRun = false;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume()
    {
        super.onResume();

        if (!bAlreadyRun)
        {
            // add button and image
            this.layout = (ViewGroup) this.findViewById(R.id.layoutRoot);

            if (this.layout != null)
            {
                this.imgBtnHome = new ImageButton(this);
                this.imgBtnHome.setOnClickListener(this);
                RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                newParams.rightMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                newParams.topMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                this.imgBtnHome.setLayoutParams(newParams);
                this.imgBtnHome.setPadding(0, 0, 0, 0);
                this.imgBtnHome.setImageResource(R.drawable.buttonhome);
                this.imgBtnHome.setBackground(null);
                this.layout.addView(this.imgBtnHome);

                this.imgBtnBack = new ImageButton(this);
                this.imgBtnBack.setOnClickListener(this);
                newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                newParams.rightMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                newParams.topMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                this.imgBtnBack.setLayoutParams(newParams);
                this.imgBtnBack.setImageResource(R.drawable.buttonback);
                this.imgBtnBack.setBackground(null);
                this.imgBtnBack.setPadding(0, 0, 0, 0);
                this.layout.addView(this.imgBtnBack);

                this.imgBtnInfor = new ImageButton(this);
                this.imgBtnInfor.setOnClickListener(this);
                newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                newParams.rightMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                newParams.topMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                this.imgBtnInfor.setLayoutParams(newParams);
                this.imgBtnInfor.setImageResource(R.drawable.buttoninformation);
                this.imgBtnInfor.setBackground(null);
                this.imgBtnInfor.setPadding(0, 0, 0, 0);
                this.layout.addView(this.imgBtnInfor);

                this.imgBtnIcon = new ImageButton(this);
                this.imgBtnIcon.setBackground(null);
                this.imgBtnIcon.setOnClickListener(this);
                this.imgBtnIcon.setImageResource(R.drawable.virginicon);
                newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                newParams.leftMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                newParams.topMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
                this.imgBtnIcon.setLayoutParams(newParams);
                this.layout.addView(this.imgBtnIcon);

                this.imageViewTopBar = new ImageView(this);
                this.imageViewTopBar.setBackgroundColor(this.getResources().getColor(R.color.TopBarBackground));
                int height = (int) this.getResources().getDimension(R.dimen.topBarHeight);
                newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, height);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.topMargin = (int) (this.getResources().getDimension(R.dimen.contentTopMargin) - this.getResources()
                        .getDimension(R.dimen.topBarHeight));
                this.imageViewTopBar.setLayoutParams(newParams);
                this.layout.addView(this.imageViewTopBar, 0);

                this.imageViewBottomBar = new ImageView(this);
                this.imageViewBottomBar.setBackgroundColor(this.getResources().getColor(R.color.BottomBarBackground));
                newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources()
                        .getDimension(R.dimen.bottomBarHeight));
                newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                this.imageViewBottomBar.setLayoutParams(newParams);
                this.layout.addView(this.imageViewBottomBar, 0);
            }

            bAlreadyRun = true;
        }
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        if (v.equals(this.imgBtnInfor))
        {
            Intent tmpIntent = new Intent(this, ActivityInformation.class);
            this.startActivity(tmpIntent);
        } else if (v.equals(this.imgBtnHome) || v.equals(this.imgBtnBack))
        {
            this.finish();
        } else if (v.equals(this.imgBtnIcon))
        {
            Intent tmpIntent = new Intent(this, ActivityPrivacy.class);
            this.startActivity(tmpIntent);
        }
    }

    // 0<=progress<=1
    public void setScreenBrightness(float progress)
    {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = progress;
        getWindow().setAttributes(layout);
    }

    public void resetScreenBrightness()
    {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 0.7f;
        getWindow().setAttributes(layout);
    }

    public static void sendViewToBack(final View child)
    {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent)
        {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void showDialog(View theContent)
    {
        if (theContent == null)
            return;

        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(theContent);

        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        dialog.show();
    }

    public void showTempDialog(int time, View theContent)
    {
        if (theContent == null)
            return;

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(theContent);

        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.3f;
        lp.flags = android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                dialog.cancel();
            }
        }, time >= 1000 ? time : 1000);
    }
}
