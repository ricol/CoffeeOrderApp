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
import au.com.philology.coffeeorderapp.database.Preference;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;

public class PreferenceSyncServer extends DBObjectSyncServer
{

    public static DBObjectSyncServer theInstance;

    public static DBObjectSyncServer getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new PreferenceSyncServer();
        return theInstance;
    }

    @Override
    public void broadcast(DBObject object)
    {
        String cmd = Command.BROADCAST_ADD_PREFERENCE + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
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

            if (command.equals(Command.PUSH_PREFERENCE))
            {
                // command from client
                Preference tmpObject = MergeObject(content);
                if (tmpObject != null)
                    PreferenceSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.PUSH_PREFERENCE_REQUEST_BORADCAST))
            {
                // command from client
                Preference tmpObject = MergeObject(content);
                if (tmpObject != null)
                    PreferenceSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.REQUEST_ALL_PREFERENCES))
            {
                // command from client
                ArrayList<Preference> all = Preference.getAllPreferences();
                JSONObject jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                try
                {
                    for (Preference object : all)
                    {
                        JSONObject aObject = object.getJsonObject();
                        jsonArray.put(aObject);
                    }

                    jsonObject.put("LIST", jsonArray);
                    jsonObject.put("TIMESTAMP", new Date().toString());

                    String cmdRespond = Command.RESPONSE_REQUEST_ALL_PREFERENCES + Command.SEPERATOR + jsonObject.toString() + Command.SEPERATOR
                            + new Date().toString();

                    TheTCPServer.getSharedInstance().send(cmdRespond, address, port);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    static Preference MergeObject(String content)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(content);
            Preference tmpObject = (Preference) Preference.getObjectFromJsonObject(jsonObject);
            if (Preference.merge(tmpObject))
            {
                final User tmpUser = User.getUser(tmpObject.user_id);
                final CoffeeType coffeetype = CoffeeType.getCoffeeType(tmpObject.coffee_id);
                if (tmpUser != null && coffeetype != null && theContext != null)
                    theContext.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            DebugToast.show(theContext,
                                    "new preference: " + tmpUser.first_name + " " + tmpUser.last_name + " - " + coffeetype.label + " merged.",
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
