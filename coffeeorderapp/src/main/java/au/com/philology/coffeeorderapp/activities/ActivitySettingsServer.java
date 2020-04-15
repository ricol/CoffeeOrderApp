package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;
import au.com.philology.common.utils;

public class ActivitySettingsServer extends BasicActivity
{
    Button btnApply, btnTestServer;
    EditText etServerIp, etServerPort;
    MyThread theThread = new MyThread();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_server);

        if (WorkingMode.getWorkingMode() != TypeWorkingMode.CLIENT_MODE)
        {
            utils.showTempInfor(this, "Erro!\nNot in Client Mode!");
            this.finish();
        }

        this.btnApply = (Button) this.findViewById(R.id.btnApply);
        this.btnApply.setOnClickListener(this);
        this.btnApply.setVisibility(View.INVISIBLE);

        this.etServerIp = (EditText) this.findViewById(R.id.etServerIp);
        this.etServerPort = (EditText) this.findViewById(R.id.etServerPort);
        this.etServerIp.setKeyListener(null);
        this.etServerPort.setKeyListener(null);

        String serverIp = "127.0.0.1";
        int serverPort = 0;

        if (WorkingMode.getWorkingMode() == TypeWorkingMode.CLIENT_MODE)
        {
            serverIp = TheTCPClient.getSharedInstance().getServerIp();
            serverPort = TheTCPClient.getSharedInstance().getServerPort();
        } else if (WorkingMode.getWorkingMode() == TypeWorkingMode.SERVER_MODE)
        {
            serverIp = TheTCPServer.getSharedInstance().getLocalIp();
            serverPort = TheTCPServer.getSharedInstance().port;
        }

        this.etServerIp.setText(serverIp);
        this.etServerPort.setText(Integer.toString(serverPort));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);

        if (!this.theThread.isAlive())
            this.theThread.start();
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();

        this.theThread.interrupt();
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnApply))
        {
            utils.showTempInfor(this, "Not support to specify server address!");
            return;

//			String strIp = etServerIp.getText().toString();
//			if (strIp == null || strIp.equals(""))
//			{
//				utils.showTempInfor(this, "Not specify a valid server IP address.");
//				return;
//			}
//
//			String strPort = etServerPort.getText().toString();
//			if (strPort == null || strPort.equals(""))
//			{
//				utils.showTempInfor(this, "Not specify a valid server port.");
//				return;
//			}
//
//			if (!ValueValidator.isValidIP(etServerIp.getText().toString()))
//			{
//				utils.showDialog(this, "ERROR", "Invalid Server IP!");
//				return;
//			}
//
//			if (!ValueValidator.isValidInteger(etServerPort.getText().toString()))
//			{
//				utils.showDialog(this, "ERROR", "Invalid Server Port!");
//				return;
//			}
        }
    }

    synchronized void refrshTheServer()
    {
        if (WorkingMode.getWorkingMode() == TypeWorkingMode.CLIENT_MODE)
        {
            this.etServerIp.setText("");
            this.etServerPort.setText("");

            String serverIp = TheTCPClient.getSharedInstance().getServerIp();
            int serverPort = TheTCPClient.getSharedInstance().getServerPort();
            this.etServerIp.setText(serverIp);
            this.etServerPort.setText("" + serverPort);
        }
    }

    private class MyThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                if (this.isInterrupted())
                    return;

                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        refrshTheServer();
                    }

                });
            }
        }
    }
}
