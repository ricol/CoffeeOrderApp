package au.com.philology.coffeeorderapp.database.sync.client;

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
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;

public class PreferenceSyncClient extends DBObjectSyncClient
{
    public static DBObjectSyncClient theInstance;

    public static DBObjectSyncClient getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new PreferenceSyncClient();
        return theInstance;
    }

    @Override
    public void push(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_PREFERENCE + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void pushWithBroadcastRequest(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_PREFERENCE_REQUEST_BORADCAST + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }

    }

    @Override
    public void pushAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            ArrayList<Preference> all = Preference.getAllPreferences();

            for (Preference object : all)
            {
                PreferenceSyncClient.getSharedInstance().push(object);
            }
        }
    }

    @Override
    public void requestAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.REQUEST_ALL_PREFERENCES + Command.SEPERATOR + new Date().toString();
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

            if (command.equals(Command.RESPONSE_REQUEST_ALL_PREFERENCES))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);

                    Preference.clear();

                    JSONArray list = jsonObject.getJSONArray("LIST");

                    for (int i = 0; i < list.length(); i++)
                    {
                        JSONObject tmpJson = list.getJSONObject(i);

                        Preference tmpObject = (Preference) Preference.getObjectFromJsonObject(tmpJson);
                        Preference.merge(tmpObject);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_ADD_PREFERENCE))
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
                                            "new preference: " + tmpUser.first_name + " " + tmpUser.last_name + " - " + coffeetype.label + " added.",
                                            Toast.LENGTH_LONG);
                                }
                            });
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_DELETE_PREFERENCE))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    Preference tmpObject = (Preference) Preference.getObjectFromJsonObject(jsonObject);
                    Preference.removePreferenceForUser(tmpObject.user_id);
                    final User tmpUser = User.getUser(tmpObject.user_id);
                    final CoffeeType coffeetype = CoffeeType.getCoffeeType(tmpObject.coffee_id);
                    if (tmpUser != null && coffeetype != null && theContext != null)
                        theContext.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                DebugToast.show(theContext,
                                        "preference: " + tmpUser.first_name + " " + tmpUser.last_name + " - " + coffeetype.label + " deleted!",
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
