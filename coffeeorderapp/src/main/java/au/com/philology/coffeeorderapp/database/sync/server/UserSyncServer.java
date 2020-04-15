package au.com.philology.coffeeorderapp.database.sync.server;

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
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;

public class UserSyncServer extends DBObjectSyncServer
{
    public static DBObjectSyncServer theInstance;

    public static DBObjectSyncServer getSharedInstance()
    {
        if (theInstance == null)
            theInstance = new UserSyncServer();
        return theInstance;
    }

    @Override
    public void broadcast(DBObject object)
    {
        String cmd = Command.BROADCAST_ADD_USER + Command.SEPERATOR + object.getJsonString() + Command.SEPERATOR + new Date().toString();
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

            if (command.equals(Command.PUSH_USER))
            {
                // command from client
                User tmpObject = MergeObject(content);
                if (tmpObject != null)
                    UserSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.PUSH_USER_REQUEST_BROADCAST))
            {
                // command from client
                User tmpObject = MergeObject(content);
                if (tmpObject != null)
                    UserSyncServer.getSharedInstance().broadcast(tmpObject);
            } else if (command.equals(Command.REQUEST_ALL_USERS))
            {
                // command from client
                ArrayList<User> all = User.getAllUsers();
                JSONObject jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                try
                {
                    for (User object : all)
                    {
                        JSONObject aObject = object.getJsonObject();
                        jsonArray.put(aObject);
                    }

                    jsonObject.put("LIST", jsonArray);
                    jsonObject.put("TIMESTAMP", new Date().toString());

                    String cmdRespond = Command.RESPONSE_REQUEST_ALL_USERS + Command.SEPERATOR + jsonObject.toString() + Command.SEPERATOR
                            + new Date().toString();

                    TheTCPServer.getSharedInstance().send(cmdRespond, address, port);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    static User MergeObject(String content)
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
                            DebugToast.show(theContext, "new user: " + tmpObject.first_name + " " + tmpObject.last_name + " merged.", Toast.LENGTH_LONG);
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
