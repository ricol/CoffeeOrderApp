package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.adapter.CoffeeTypeGridViewAdapter;
import au.com.philology.coffeeorderapp.common.Common;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.common.Model;
import au.com.philology.coffeeorderapp.database.CoffeeType;
import au.com.philology.coffeeorderapp.database.Order;
import au.com.philology.coffeeorderapp.database.Preference;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.database.sync.client.OrderSyncClient;
import au.com.philology.coffeeorderapp.database.sync.client.PreferenceSyncClient;
import au.com.philology.coffeeorderapp.database.sync.server.OrderSyncServer;
import au.com.philology.coffeeorderapp.database.sync.server.PreferenceSyncServer;
import au.com.philology.coffeeorderapp.printer.ThePrinter;
import au.com.philology.tcpudp.IPAddress;

public class ActivityCoffeeOrder extends BasicActivity
{
    TextView tvTitle, tvMsg;
    ImageView imageView;
    ImageButton btnReselect, btnConfirm;
    String userid;
    String coffeeid;
    RadioGroup rgSugar, rgMilk, rgStrength, rgCuptype;
    CheckBox cbPrint;
    Button btnEditCustomerDetails;

    public static int REQUEST_CODE_EDIT_CUSTOMER_DETAILS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.coffee_order);

        this.tvTitle = (TextView) this.findViewById(R.id.tvOrderTitle);
        this.tvMsg = (TextView) this.findViewById(R.id.tvCurrentSelection);
        String txt = "<html><font color='#E1223C'><strong>personalise</strong></font> your order</html>";
        this.tvMsg.setText(Html.fromHtml(txt));
        this.imageView = (ImageView) this.findViewById(R.id.imaveViewOrder);
        this.btnReselect = (ImageButton) this.findViewById(R.id.imageBtnReselect);
        this.btnConfirm = (ImageButton) this.findViewById(R.id.imageBtnConfirm);
        this.btnEditCustomerDetails = (Button) this.findViewById(R.id.btnEditCustomerDetails);
        this.btnEditCustomerDetails.setTransformationMethod(null);
        this.rgSugar = (RadioGroup) this.findViewById(R.id.rgSugars);
        this.rgMilk = (RadioGroup) this.findViewById(R.id.rgMilk);
        this.rgStrength = (RadioGroup) this.findViewById(R.id.rgStrength);
        this.rgCuptype = (RadioGroup) this.findViewById(R.id.rgCupType);
        this.cbPrint = (CheckBox) this.findViewById(R.id.cbPrint);
        this.cbPrint.setVisibility(Model.isPrintButtonVisible(this) ? View.VISIBLE : View.INVISIBLE);

        this.btnReselect.setOnClickListener(this);
        this.btnConfirm.setOnClickListener(this);
        this.btnEditCustomerDetails.setOnClickListener(this);

        Intent tmpIntent = this.getIntent();

        this.coffeeid = tmpIntent.getStringExtra("coffeeid");
        this.userid = tmpIntent.getStringExtra("userid");

        CoffeeType tmpCoffeeType = CoffeeType.getCoffeeType(coffeeid);

        if (tmpCoffeeType != null)
        {
            this.tvTitle.setText(tmpCoffeeType.label);
            this.imageView.setImageResource(this.getResources().getIdentifier(tmpCoffeeType.image, "drawable", this.getPackageName()));
        }

        Preference tmpPreference = Preference.getPreferenceForUser(this.userid);
        if (tmpPreference != null)
        {
            View button = this.rgSugar.getChildAt(Integer.parseInt(tmpPreference.getSugar()));
            this.rgSugar.check(button.getId());

            button = this.rgMilk.getChildAt(Integer.parseInt(tmpPreference.getMilk()));
            this.rgMilk.check(button.getId());

            button = this.rgCuptype.getChildAt(Integer.parseInt(tmpPreference.getCuptype()));
            this.rgCuptype.check(button.getId());

            button = this.rgStrength.getChildAt(Integer.parseInt(tmpPreference.getStrength()));
            this.rgStrength.check(button.getId());
        }

        int num = this.rgSugar.getChildCount();
        for (int i = 0; i < num; i++)
        {
            String text = Model.theInstance.sugarToString(i);
            RadioButton rb = (RadioButton) this.rgSugar.getChildAt(i);
            rb.setText(text);
        }

        num = this.rgMilk.getChildCount();
        for (int i = 0; i < num; i++)
        {
            String text = Model.theInstance.milkToString(i);
            RadioButton rb = (RadioButton) this.rgMilk.getChildAt(i);
            rb.setText(text);
        }

        num = this.rgCuptype.getChildCount();
        for (int i = 0; i < num; i++)
        {
            String text = Model.theInstance.cuptypeToString(i);
            RadioButton rb = (RadioButton) this.rgCuptype.getChildAt(i);
            rb.setText(text);
        }

        num = this.rgStrength.getChildCount();
        for (int i = 0; i < num; i++)
        {
            String text = Model.theInstance.strengthToString(i);
            RadioButton rb = (RadioButton) this.rgStrength.getChildAt(i);
            rb.setText(text);
        }

        User tmpUser = User.getUser(userid);
        if (tmpUser != null)
        {
            this.btnEditCustomerDetails.setText(Common.getCapitalizeStr(tmpUser.first_name) + " " + Common.getCapitalizeStr(tmpUser.last_name));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnConfirm))
        {
            int Sugar = this.rgSugar.getCheckedRadioButtonId();
            View button = this.rgSugar.findViewById(Sugar);
            Sugar = this.rgSugar.indexOfChild(button);

            int Milk = this.rgMilk.getCheckedRadioButtonId();
            button = this.rgMilk.findViewById(Milk);
            Milk = this.rgMilk.indexOfChild(button);

            int Cuptype = this.rgCuptype.getCheckedRadioButtonId();
            button = this.rgCuptype.findViewById(Cuptype);
            Cuptype = this.rgCuptype.indexOfChild(button);

            int Strength = this.rgStrength.getCheckedRadioButtonId();
            button = this.rgStrength.findViewById(Strength);
            Strength = this.rgStrength.indexOfChild(button);

            Preference tmpOldPref = Preference.getPreferenceForUser(this.userid);

            boolean bShouldcreate = true;

            if (tmpOldPref != null)
            {
                if (tmpOldPref.coffee_id.equals(this.coffeeid) && (Integer.parseInt(tmpOldPref.getSugar()) == Sugar)
                        && (Integer.parseInt(tmpOldPref.getMilk()) == Milk) && (Integer.parseInt(tmpOldPref.getCuptype()) == Cuptype)
                        && (Integer.parseInt(tmpOldPref.getStrength()) == Strength))
                    bShouldcreate = false;
            }

            if (bShouldcreate)
            {
                String jsonPre = Preference.getPreferenceJsonRepresentation(Integer.toString(Sugar), Integer.toString(Milk), Integer.toString(Cuptype),
                        Integer.toString(Strength));
                Preference tmpPreference = new Preference(this.userid, this.coffeeid, jsonPre, (new Date()).getTime());
                Preference.merge(tmpPreference);
                TypeWorkingMode workMode = WorkingMode.getWorkingMode();
                if (workMode == TypeWorkingMode.CLIENT_MODE)
                    PreferenceSyncClient.getSharedInstance().pushWithBroadcastRequest(tmpPreference);
                else if (workMode == TypeWorkingMode.SERVER_MODE)
                {
                    PreferenceSyncServer.getSharedInstance().broadcast(tmpPreference);
                }
            }

            Order aOrder = new Order(this.userid, this.coffeeid, IPAddress.getLocalAddress(), new Date().getTime());
            Order.merge(aOrder);

            TypeWorkingMode workMode = WorkingMode.getWorkingMode();
            if (workMode == TypeWorkingMode.CLIENT_MODE)
                OrderSyncClient.getSharedInstance().pushWithBroadcastRequest(aOrder);
            else if (workMode == TypeWorkingMode.SERVER_MODE)
            {
                OrderSyncServer.getSharedInstance().broadcast(aOrder);
            }

            if (this.cbPrint.isChecked())
            {
                User tmpUser = User.getUser(this.userid);
                CoffeeType coffeeType = CoffeeType.getCoffeeType(this.coffeeid);
                String text = Common.getPrinterReceiptFormat(tmpUser.first_name, tmpUser.last_name, coffeeType.label, Model.theInstance.sugarToString(Sugar),
                        Model.theInstance.milkToString(Milk), Model.theInstance.cuptypeToString(Cuptype), Model.theInstance.strengthToString(Strength),
                        new Date().toString());
                ThePrinter.getSharedInstance().print(text);
            }

            Intent intent = new Intent(Common.NAME_INTENT_ORDER_PLACED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            this.finish();
        } else if (v.equals(this.btnReselect))
        {
            Intent tmpIntent = new Intent(this, ActivityCoffeeChoicesGridView.class);
            tmpIntent.putExtra("userid", this.userid);

            CoffeeTypeGridViewAdapter tmpAdapter = new CoffeeTypeGridViewAdapter(this, this.getLayoutInflater());
            tmpIntent.putExtra("original", tmpAdapter.getCoffeeOrderIdFromIdentifier(this.coffeeid));
            this.startActivity(tmpIntent);
            this.finish();
        } else if (v.equals(this.btnEditCustomerDetails))
        {
            Intent tmpIntent = new Intent(this, ActivityEditCustomer.class);
            tmpIntent.putExtra("user_id", this.userid);
            this.startActivityForResult(tmpIntent, ActivityCoffeeOrder.REQUEST_CODE_EDIT_CUSTOMER_DETAILS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityCoffeeOrder.REQUEST_CODE_EDIT_CUSTOMER_DETAILS)
        {
            User tmpUser = User.getUser(userid);
            if (tmpUser != null)
            {
                this.btnEditCustomerDetails.setText(Common.getCapitalizeStr(tmpUser.first_name) + " " + Common.getCapitalizeStr(tmpUser.last_name));
            }
        }
    }
}
