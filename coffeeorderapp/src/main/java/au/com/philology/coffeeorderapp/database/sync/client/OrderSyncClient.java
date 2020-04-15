package au.com.philology.coffeeorderapp.database.sync.client;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import au.com.philology.coffeeorderapp.common.Command;
import au.com.philology.coffeeorderapp.database.DBObject;
import au.com.philology.coffeeorderapp.database.Order;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;

public class OrderSyncClient extends DBObjectSyncClient
{

    public static DBObjectSyncClient theInstance;

    public static DBObjectSyncClient getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new OrderSyncClient();
        return theInstance;
    }

    @Override
    public void push(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_ORDER + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void pushWithBroadcastRequest(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_ORDER_REQUEST_BROADCAST + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void pushAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            ArrayList<Order> all = Order.getAllOrders();

            for (Order a : all)
            {
                OrderSyncClient.getSharedInstance().push(a);
            }
        }
    }

    @Override
    public void requestAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.REQUEST_ALL_ORDERS + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void processCmd(String cmd, String address, int port)
    {
        String[] array = cmd.split("[" + Command.SEPERATOR + "]");
        if (array.length > 1)
        {
            String command = array[0];
            String content = array[1];

            if (command.equals(Command.RESPONSE_REQUEST_ALL_ORDERS))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);

                    Order.clear();

                    JSONArray list = jsonObject.getJSONArray("LIST");

                    for (int i = 0; i < list.length(); i++)
                    {
                        JSONObject tmpJson = list.getJSONObject(i);

                        Order tmpObject = (Order) Order.getObjectFromJsonObject(tmpJson);
                        Order.merge(tmpObject);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_ADD_ORDER))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    final Order tmpObject = (Order) Order.getObjectFromJsonObject(jsonObject);
                    if (Order.merge(tmpObject))
                    {
                        if (theContext != null)
                            theContext.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    DebugToast.show(theContext,
                                            "new order: " + tmpObject.user_id + " " + tmpObject.coffee_id + " " + tmpObject.device_id + " added.",
                                            Toast.LENGTH_LONG);
                                }
                            });
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_DELETE_ORDER))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    final Order tmpObject = (Order) Order.getObjectFromJsonObject(jsonObject);
                    Order.removeOrderForUser(tmpObject.user_id);
                    if (theContext != null)
                        theContext.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                DebugToast.show(theContext, "user: " + tmpObject.user_id + " " + tmpObject.coffee_id + " " + tmpObject.device_id + " deleted!",
                                        Toast.LENGTH_LONG);
                            }
                        });
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
