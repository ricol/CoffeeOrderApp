package au.com.philology.coffeeorderapp.database.sync.server;

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
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;

public class OrderSyncServer extends DBObjectSyncServer
{
    public static DBObjectSyncServer theInstance;

    public static DBObjectSyncServer getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new OrderSyncServer();
        return theInstance;
    }

    @Override
    public void broadcast(DBObject object)
    {
        String cmd = Command.BROADCAST_ADD_ORDER + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
        TheTCPServer.getSharedInstance().broadcast(cmd);
    }

    @Override
    public void processCmd(String cmd, String address, int port)
    {
        String[] array = cmd.split("[" + Command.SEPERATOR + "]");
        if (array.length > 1)
        {
            String command = array[0];
            String content = array[1];

            if (command.equals(Command.PUSH_ORDER))
            {
                // command from client
                Order tmpObject = MergeObject(content);
                if (tmpObject != null)
                    OrderSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.PUSH_ORDER_REQUEST_BROADCAST))
            {
                // command from client
                Order tmpObject = MergeObject(content);
                if (tmpObject != null)
                    OrderSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.REQUEST_ALL_ORDERS))
            {
                // command from client
                ArrayList<Order> all = Order.getAllOrders();
                JSONObject jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                try
                {
                    for (Order object : all)
                    {
                        JSONObject aObject = object.getJsonObject();
                        jsonArray.put(aObject);
                    }

                    jsonObject.put("LIST", jsonArray);
                    jsonObject.put("TIMESTAMP", new Date().toString());

                    String cmdRespond = Command.RESPONSE_REQUEST_ALL_ORDERS + Command.SEPERATOR + jsonObject.toString() + Command.SEPERATOR
                            + new Date().toString();

                    TheTCPServer.getSharedInstance().send(cmdRespond, address, port);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    static Order MergeObject(String content)
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
                            DebugToast.show(theContext, "new order: " + tmpObject.user_id + " " + tmpObject.coffee_id + " " + tmpObject.device_id + " merged.",
                                    Toast.LENGTH_LONG);
                        }
                    });
            }

            return tmpObject;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
