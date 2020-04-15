package au.com.philology.coffeeorderapp.database.sync.client;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import au.com.philology.coffeeorderapp.common.Command;
import au.com.philology.coffeeorderapp.database.DBObject;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;

public class UserSyncClient extends DBObjectSyncClient
{
    public static DBObjectSyncClient theInstance;

    public static DBObjectSyncClient getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new UserSyncClient();
        return theInstance;
    }

    @Override
    public void push(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_USER + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void pushWithBroadcastRequest(DBObject object)
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.PUSH_USER_REQUEST_BROADCAST + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
            TheTCPClient.getSharedInstance().send(command);
        }
    }

    @Override
    public void pushAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            ArrayList<User> allUsers = User.getAllUsers();

            for (User aUser : allUsers)
            {
                UserSyncClient.getSharedInstance().push(aUser);
            }
        }
    }

    @Override
    public void requestAll()
    {
        if (TheTCPClient.getSharedInstance().isConnected())
        {
            String command = Command.REQUEST_ALL_USERS + Command.SEPERATOR + new Date().toString();
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

            if (command.equals(Command.RESPONSE_REQUEST_ALL_USERS))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);

                    User.clear();

                    JSONArray list = jsonObject.getJSONArray("LIST");

                    for (int i = 0; i < list.length(); i++)
                    {
                        JSONObject tmpJson = list.getJSONObject(i);

                        User tmpObject = (User) User.getObjectFromJsonObject(tmpJson);
                        User.merge(tmpObject);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_ADD_USER))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    final User tmpObject = (User) User.getObjectFromJsonObject(jsonObject);
                    if (User.merge(tmpObject))
                    {
                        if (theContext != null)
                            theContext.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    DebugToast.show(theContext, "new user: " + tmpObject.first_name + " " + tmpObject.last_name + " added.", Toast.LENGTH_LONG);
                                }
                            });
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            } else if (command.equals(Command.BROADCAST_DELETE_USER))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    final User tmpObject = (User) User.getObjectFromJsonObject(jsonObject);
                    User.removeUser(tmpObject.user_id);
                    if (theContext != null)
                        theContext.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                DebugToast.show(theContext, "user: " + tmpObject.first_name + " " + tmpObject.last_name + " deleted!", Toast.LENGTH_LONG);
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
