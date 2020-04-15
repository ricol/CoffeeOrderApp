package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;
import au.com.philology.common.utils;
import au.com.philology.tcpudp.IPAddress;

public class ActivitySettingsClients extends BasicActivity
{
    ListView lvAllClients;
    TextView tvCurrentIPAddress;
    ArrayAdapter<String> mArrayAdapterMsg;
    MyThread theThread = new MyThread();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_clients);

        this.lvAllClients = (ListView) this.findViewById(R.id.listViewAllClients);
        this.mArrayAdapterMsg = new ArrayAdapter<String>(this, R.layout.list_view_item);
        this.lvAllClients.setAdapter(mArrayAdapterMsg);

        this.tvCurrentIPAddress = (TextView) this.findViewById(R.id.tvCurrentIPAddress);

        if (WorkingMode.getWorkingMode() != TypeWorkingMode.SERVER_MODE)
        {
            utils.showTempInfor(this, "Erro!\nNot in Server Mode!");
            this.finish();
        }
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

        this.tvCurrentIPAddress.setText("Local IP: " + IPAddress.getLocalAddress());
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();

        this.theThread.interrupt();
    }

    synchronized void loadAllClients()
    {
        if (WorkingMode.getWorkingMode() == TypeWorkingMode.SERVER_MODE)
        {
            this.mArrayAdapterMsg.clear();

            ArrayList<String> allIPs = TheTCPServer.getSharedInstance().getAllClientsIp();
            ArrayList<Integer> allPorts = TheTCPServer.getSharedInstance().getAllClientsPort();

            for (int i = 0; i < allIPs.size(); i++)
            {
                String ip = allIPs.get(i);
                int port = allPorts.get(i);
                this.mArrayAdapterMsg.add(ip + " : " + port);
            }
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
                        loadAllClients();
                    }

                });
            }
        }
    }
}
