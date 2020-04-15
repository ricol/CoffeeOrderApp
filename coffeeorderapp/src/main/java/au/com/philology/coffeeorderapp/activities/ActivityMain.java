package au.com.philology.coffeeorderapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.CardReaderIdentifier;
import au.com.philology.coffeeorderapp.database.CloudOrderFilter;
import au.com.philology.coffeeorderapp.database.CoffeeType;
import au.com.philology.coffeeorderapp.database.DB;
import au.com.philology.coffeeorderapp.database.DeveloperMode;
import au.com.philology.coffeeorderapp.database.InterestedCardReaderID;
import au.com.philology.coffeeorderapp.database.Preference;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.database.sync.DBObjectSync;
import au.com.philology.coffeeorderapp.database.sync.client.UserSyncClient;
import au.com.philology.coffeeorderapp.database.sync.server.UserSyncServer;
import au.com.philology.coffeeorderapp.datasource.ICardReaderDelegate;
import au.com.philology.coffeeorderapp.datasource.TheDataSource;
import au.com.philology.coffeeorderapp.datasource.cloudorders.ICloudOrderDelegate;
import au.com.philology.coffeeorderapp.datasource.cloudorders.TheCloudOrdersService;
import au.com.philology.coffeeorderapp.dialogs.DebugToast;
import au.com.philology.coffeeorderapp.monitor.TheWorkingModeMonitor;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClient;
import au.com.philology.coffeeorderapp.netconnection.TheTCPClientDelegate;
import au.com.philology.coffeeorderapp.netconnection.TheTCPServer;
import au.com.philology.coffeeorderapp.printer.ThePrinter;
import au.com.philology.common.utils;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class ActivityMain extends BasicActivity implements ICardReaderDelegate, ICloudOrderDelegate, IDialogConfirmDelegate
{
    ImageButton btnInformation, imgBtnIconAlt;
    Button btnTest;
    TextView tvHint;

    public final static int REQUEST_CODE = 1000;

    Lock theLockForTag = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false))
            finish();
        else
        {
            setContentView(R.layout.main);

            this.btnTest = (Button) this.findViewById(R.id.btnTest);
            this.btnTest.setOnClickListener(this);

            this.tvHint = (TextView) this.findViewById(R.id.tvMainHint);
            String txt = "<html>Tap your <font color='#E1223C'><strong>Velocity Card</strong></font> on the <font color='#E1223C'><strong>Card Reader</strong></font> to begin</html>";
            this.tvHint.setText(Html.fromHtml(txt));

            utils.tag = this.getResources().getString(R.string.app_name);

            DBObjectSync.theContext = this;

            checkForUpdates();

            TheDataSource.getSharedInstance().setActivity(this);

            if (Model.isAutoCheckWorkingMode(this))
                TheWorkingModeMonitor.getSharedInstance().startMonitoring(this);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Common.NAME_INTENT_ORDER_PLACED));
        }
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();

        TheDataSource.getSharedInstance().activityOnResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume()
    {
        super.onResume();

        this.imgBtnHome.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        // this.btnGoBack.setVisibility(View.INVISIBLE);

        this.btnTest.setVisibility(DeveloperMode.isDeveloperMode() ? View.VISIBLE : View.INVISIBLE);

        TheDataSource.getSharedInstance().setCardReaderDelegate(this);
        TheCloudOrdersService.getSharedInstance().addDelegate(this);

        checkForCrashes();
        checkForUpdates();

        if (this.imgBtnIconAlt == null)
        {
            this.imgBtnIconAlt = new ImageButton(this);
            this.imgBtnIconAlt.setBackground(null);
            this.imgBtnIconAlt.setOnClickListener(this);
            this.imgBtnIconAlt.setImageResource(R.drawable.virginicon_alt);

            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            newParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            newParams.leftMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
            newParams.topMargin = (int) this.getResources().getDimension(R.dimen.imageButtonMargin);
            this.imgBtnIconAlt.setLayoutParams(newParams);
            this.layout.addView(this.imgBtnIconAlt);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        TheDataSource.getSharedInstance().setCardReaderDelegate(null);

        TheCloudOrdersService.getSharedInstance().removeDelegate(this);

        UpdateManager.unregister();
    }

    @Override
    public void finish()
    {
        TheWorkingModeMonitor.getSharedInstance().stopMonitoring();
        TheDataSource.getSharedInstance().activityOnPause();

        // close sockets
        TypeWorkingMode workmode = WorkingMode.getWorkingMode();
        if (workmode == TypeWorkingMode.CLIENT_MODE)
        {
            TheTCPClient.getSharedInstance().stopScaningIpForPort();
            TheTCPClient.getSharedInstance().stopScanningPortForIp();
            TheTCPClient.getSharedInstance().disconnect();
        } else if (workmode == TypeWorkingMode.SERVER_MODE)
        {
            TheTCPServer.getSharedInstance().stopListenning();
            TheTCPServer.getSharedInstance().stopScaningIpForPort();
            TheTCPServer.getSharedInstance().stopScanningPortForIp();
        }

        TheTCPClientDelegate.getSharedInstance().scanDelegate = null;
        TheTCPClientDelegate.getSharedInstance().clientDelegate = null;
        DBObjectSync.theContext = null;

        // close the database
        DB.getSharedInstance().close();

        super.finish();
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Quit the app?", false, 0, this, 0);
        aDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menuItemClear)
        {
            DB.theInstance.clear();

            Toast.makeText(this, "Database has been reset!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menuItemAbout)
        {
            Toast.makeText(this, "About", Toast.LENGTH_LONG).show();
            return true;
        } else if (item.getItemId() == R.id.menuItemSettings)
        {
            Intent tmpIntent = new Intent(this, ActivitySettings.class);
            this.startActivity(tmpIntent);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        setIntent(intent);
        TheDataSource.getSharedInstance().resolveIntent(intent);
    }

    void showCoffeePreferenceForUser(String userid)
    {
        User tmpUser = User.getUser(userid);
        if (tmpUser != null)
        {
            Preference tmpPreference = Preference.getPreferenceForUser(tmpUser.user_id);
            if (tmpPreference != null)
            {
                Intent tmpIntent = new Intent(this, ActivityCoffeeOrder.class);
                tmpIntent.putExtra("userid", tmpUser.user_id);
                tmpIntent.putExtra("coffeeid", tmpPreference.coffee_id);
                this.startActivity(tmpIntent);
            } else
            {
                Intent tmpIntent = new Intent(this, ActivityCoffeeChoicesGridView.class);
                tmpIntent.putExtra("userid", tmpUser.user_id);
                this.startActivity(tmpIntent);
            }
        }
    }

    void newUser(String tag)
    {
        Intent tmpIntent = new Intent(this, ActivityGuestRegistration.class);
        tmpIntent.putExtra("tag", tag);
        this.startActivityForResult(tmpIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2)
    {
        super.onActivityResult(arg0, arg1, arg2);

        if (arg1 == REQUEST_CODE)
        {
            String firstname = arg2.getStringExtra("firstname");
            String lastname = arg2.getStringExtra("lastname");
            String mobile = arg2.getStringExtra("mobile");
            String tag = arg2.getStringExtra("tag");

            Date d = new Date();
            User newUser = new User(tag, firstname, lastname, mobile, d.getTime());
            User.merge(newUser);

            TypeWorkingMode workMode = WorkingMode.getWorkingMode();
            if (workMode == TypeWorkingMode.CLIENT_MODE)
                UserSyncClient.getSharedInstance().pushWithBroadcastRequest(newUser);
            else if (workMode == TypeWorkingMode.SERVER_MODE)
            {
                UserSyncServer.getSharedInstance().broadcast(newUser);
            }

            showCoffeePreferenceForUser(tag);
        }
    }

    @Override
    public void CardReaderLostConnection(String text)
    {
        DebugToast.show(this, "Card Reader lost connection!", Toast.LENGTH_LONG);
    }

    @Override
    public void CardReaderConnected(String text)
    {
        DebugToast.show(this, "Card Reader connected.", Toast.LENGTH_LONG);
    }

    @Override
    public void CardReaderTagDetected(String text, String tag)
    {
        utils.print("tag detected: " + tag);
        this.processATag(tag);
    }

    @Override
    public void CardReaderStateChanged(String state)
    {

    }

    @Override
    public void CardReaderOperationCancelled(String text)
    {

    }

    @Override
    public void CardReaderTryToConnect(String text)
    {

    }

    @Override
    public void CardReaderTryToDisconnect(String text)
    {

    }

    @Override
    public void cloudOrderDataReceived(String data)
    {
    }

    @Override
    public void cloudOrderStarted()
    {
    }

    @Override
    public void cloudOrderEnded()
    {
    }

    @Override
    public void cloudOrderNewOrder(String deviceId, String tagId)
    {
        boolean bHandle = false;

        CardReaderIdentifier.insert(deviceId, "", "", 0, new Date().getTime());

        if (CloudOrderFilter.isFilterInterested())
        {
            if (InterestedCardReaderID.isIncluded(deviceId))
                bHandle = true;
        } else
            bHandle = true;

        if (bHandle)
        {
            final String order = tagId;

            CardReaderIdentifier theCardReaderID = CardReaderIdentifier.getObject(deviceId);
            if (theCardReaderID == null || theCardReaderID.autoprint == 0)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        processATag(order);
                    }
                });
            } else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        processATagWithAutoprint(order);
                    }
                });
            }

        }
    }

    void processATag(String tag)
    {
        if (this.theLockForTag.tryLock())
        {
            if (tag.matches("[0-9a-fA-F]*"))
            {
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show(); //this line is just to restore the screen brightness when a tag is scanned.
                User tmpUser = User.getUser(tag);
                if (tmpUser == null)
                {
                    // new user
                    this.newUser(tag);
                } else
                {
                    this.showCoffeePreferenceForUser(tag);
                }
            } else
            {
                Toast.makeText(this, "Invalid: " + tag, Toast.LENGTH_LONG).show();
            }

            this.theLockForTag.unlock();
        } else
        {
            DebugToast.show(this, "A tag is already in processing!\nThe action is ignored.", Toast.LENGTH_LONG);
        }
    }

    void processATagWithAutoprint(String tag)
    {
        if (this.theLockForTag.tryLock())
        {
            if (tag.matches("[0-9a-fA-F]*"))
            {
                User tmpUser = User.getUser(tag);
                if (tmpUser == null)
                {
                    // new user
                    DebugToast.show(this, "New user: " + tag + "\nIgnored due to the autoprint settings", Toast.LENGTH_SHORT);
                } else
                {
                    DebugToast.show(this, "Welcome back: " + tmpUser.first_name + " " + tmpUser.last_name, Toast.LENGTH_SHORT);
                    Preference tmpPreference = Preference.getPreferenceForUser(tmpUser.user_id);
                    if (tmpPreference != null)
                    {
                        CoffeeType coffeeType = CoffeeType.getCoffeeType(tmpPreference.coffee_id);
                        String text = Common.getPrinterReceiptFormat(tmpUser.first_name, tmpUser.last_name, coffeeType.label,
                                Model.theInstance.sugarToString(Integer.parseInt(tmpPreference.getSugar())),
                                Model.theInstance.milkToString(Integer.parseInt(tmpPreference.getMilk())),
                                Model.theInstance.cuptypeToString(Integer.parseInt(tmpPreference.getCuptype())),
                                Model.theInstance.strengthToString(Integer.parseInt(tmpPreference.getStrength())), new Date().toString());
                        ThePrinter.getSharedInstance().print(text);
                    } else
                    {
                        DebugToast.show(this, "No coffee record for the user.\nIgnored due to the autoprint settings.", Toast.LENGTH_LONG);
                    }
                }
            } else
            {
                Toast.makeText(this, "Invalid: " + tag, Toast.LENGTH_LONG).show();
            }

            this.theLockForTag.unlock();
        } else
        {
            DebugToast.show(this, "A tag is already in processing!\nThe action is ignored.", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnTest))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Input a test tag:");

            // Set up the input
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String txt = input.getText().toString();
                    if (!txt.equals(""))
                        ActivityMain.this.processATag(txt);
                    else
                        Toast.makeText(ActivityMain.this, "Empty value", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });

            builder.show();
        } else if (v.equals(this.imgBtnIconAlt))
        {
            Intent tmpIntent = new Intent(this, ActivityPrivacy.class);
            this.startActivity(tmpIntent);
        }
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        this.finish();
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
    {

    }

    private void checkForCrashes()
    {
        if (!DeveloperMode.isDeveloperMode())
            CrashManager.register(this, Common.HOCKEY_APP_ID);
    }

    private void checkForUpdates()
    {
        if (!DeveloperMode.isDeveloperMode())
            // Remove this for store / production builds!
            UpdateManager.register(this, Common.HOCKEY_APP_ID);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Get extra data included in the Intent
            ImageView theImageView = new ImageView(ActivityMain.this);
            theImageView.setImageResource(R.drawable.thankyoudialog);
            ActivityMain.this.showTempDialog(3000, theImageView);
        }
    };
}
