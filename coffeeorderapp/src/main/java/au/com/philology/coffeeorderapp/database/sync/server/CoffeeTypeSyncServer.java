package au.com.philology.coffeeorderapp.database.sync.server;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import au.com.philology.coffeeorderapp.common.Command;
import au.com.philology.coffeeorderapp.database.CoffeeType;
import au.com.philology.coffeeorderapp.database.DBObject;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;

public class CoffeeTypeSyncServer extends DBObjectSyncServer
{

    public static DBObjectSyncServer theInstance;

    public static DBObjectSyncServer getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new CoffeeTypeSyncServer();
        return theInstance;
    }

    @Override
    public void broadcast(DBObject object)
    {
        String cmd = Command.BROADCAST_ADD_COFFEETYPE + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
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

            if (command.equals(Command.PUSH_COFFEETYPE))
            {
                CoffeeType tmpObject = mergeObject(content);

                if (tmpObject != null)
                    CoffeeTypeSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.PUSH_COFFEETYPE_REQUEST_BROADCAST))
            {
                // command from client
                CoffeeType tmpObject = mergeObject(content);

                if (tmpObject != null)
                    CoffeeTypeSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.REQUEST_ALL_COFFEETYPES))
            {
                // command from client
                ArrayList<CoffeeType> all = CoffeeType.getAllCoffeeTypes();
                JSONObject jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                try
                {
                    for (CoffeeType object : all)
                    {
                        JSONObject aObject = object.getJsonObject();
                        jsonArray.put(aObject);
                    }

                    jsonObject.put("LIST", jsonArray);
                    jsonObject.put("TIMESTAMP", new Date().toString());

                    String cmdRespond = Command.RESPONSE_REQUEST_ALL_COFFEETYPES + Command.SEPERATOR + jsonObject.toString() + Command.SEPERATOR
                            + new Date().toString();

                    TheTCPServer.getSharedInstance().send(cmdRespond, address, port);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    static CoffeeType mergeObject(String content)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(content);
            final CoffeeType tmpObject = (CoffeeType) CoffeeType.getObjectFromJsonObject(jsonObject);
            if (CoffeeType.merge(tmpObject))
            {
                if (theContext != null)
                    theContext.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            DebugToast.show(theContext, "new coffee type: " + tmpObject.label + " merged.", Toast.LENGTH_LONG);
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
