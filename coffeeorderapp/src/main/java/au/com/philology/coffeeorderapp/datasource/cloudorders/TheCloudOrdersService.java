package au.com.philology.coffeeorderapp.datasource.cloudorders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.database.CloudOrder;

public class TheCloudOrdersService
{
    Set<ICloudOrderDelegate> theDelegates = new HashSet<>();

    Timer theTimer = new Timer();
    TimerTask theTimerTask = new TimerTask()
    {

        @Override
        public void run()
        {
            check();
        }
    };

    ThreadForCloudOrders theThread;

    public static TheCloudOrdersService theInstance = new TheCloudOrdersService();

    public static TheCloudOrdersService getSharedInstance()
    {
        return theInstance;
    }

    public TheCloudOrdersService()
    {
        theTimer.scheduleAtFixedRate(theTimerTask, Common.CLOUD_ORDER_CHECK_INTERVAL, Common.CLOUD_ORDER_CHECK_INTERVAL);
    }

    public void start()
    {
        if (this.isRunning())
            return;

        theThread = new ThreadForCloudOrders();
        theThread.start();
    }

    boolean isRunning()
    {
        if (theThread != null)
            return theThread.isAlive();
        else
            return false;
    }

    public void end()
    {
        if (theThread != null)
            theThread.interrupt();
        theThread = null;
    }

    void check()
    {
        if (CloudOrder.getCloudOrders())
        {
            this.start();
        } else
        {
            this.end();
        }
    }

    class ThreadForCloudOrders extends Thread
    {
        @Override
        public void run()
        {
            super.run();

            try
            {
                URL theUrl = new URL("https://api.particle.io/v1/devices/events?access_token=30158880cb163f21bd0458a4317258a553a5506b");
                BufferedReader in = new BufferedReader(new InputStreamReader(theUrl.openStream()));
                String inputLine;
                int num = 0;
                String newOrder = "";
                boolean bNewOrder = false;

                for (ICloudOrderDelegate aDelegate : theDelegates)
                {
                    aDelegate.cloudOrderStarted();
                }

                while ((inputLine = in.readLine()) != null && !Thread.interrupted())
                {
                    final String s = num++ + ":" + inputLine;

                    for (ICloudOrderDelegate aDelegate : theDelegates)
                    {
                        aDelegate.cloudOrderDataReceived(s);
                    }

                    if (bNewOrder)
                    {
                        newOrder += inputLine;
                        if (inputLine.equals(""))
                        {
                            processOrder(newOrder);
                            bNewOrder = false;
                        }
                    } else
                    {
                        if (inputLine.contains("Capture"))
                        {
                            bNewOrder = true;
                            newOrder = inputLine;
                        }
                    }
                }

                for (ICloudOrderDelegate aDelegate : theDelegates)
                {
                    aDelegate.cloudOrderEnded();
                }

            } catch (MalformedURLException e)
            {
                System.out.println(e);
            } catch (IOException e)
            {
                System.out.println(e);
            }
        }

        void processOrder(String aOrder)
        {
            try
            {
                String s = aOrder;
                int start = s.indexOf("{");
                int end = s.indexOf("}");
                s = s.substring(start, end + 1);
                System.out.println("S: " + s);

                JSONObject theJsonObject = new JSONObject(s);

                String tagId = theJsonObject.getString("data");
                String deviceId = theJsonObject.getString("coreid");

                for (ICloudOrderDelegate aDelegate : theDelegates)
                {
                    aDelegate.cloudOrderNewOrder(deviceId, tagId);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addDelegate(ICloudOrderDelegate aDelegate)
    {
        this.theDelegates.add(aDelegate);
    }

    public void removeDelegate(ICloudOrderDelegate aDelegate)
    {
        this.theDelegates.remove(aDelegate);
    }

}