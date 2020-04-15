package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.common.Common.TypeWorkingMode;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.database.WorkingMode;
import au.com.philology.coffeeorderapp.database.sync.client.UserSyncClient;
import au.com.philology.coffeeorderapp.database.sync.server.UserSyncServer;

public class ActivityEditCustomer extends BasicActivity
{
    ImageButton ibOk, ibCancel;
    EditText etFirstname, etLastname, etMobile;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.edit_customer);

        this.ibOk = (ImageButton) this.findViewById(R.id.ibEditCustomerOK);
        this.ibOk.setOnClickListener(this);

        this.ibCancel = (ImageButton) this.findViewById(R.id.ibEditCustomerCancel);
        this.ibCancel.setOnClickListener(this);

        this.etFirstname = (EditText) this.findViewById(R.id.etEditCustomerFirstName);
        this.etLastname = (EditText) this.findViewById(R.id.etEditCustomerLastName);
        this.etMobile = (EditText) this.findViewById(R.id.etEditCustomerMobile);

        userId = this.getIntent().getStringExtra("user_id");
        User theUser = User.getUser(userId);
        if (theUser != null)
        {
            this.etFirstname.setText(theUser.first_name);
            this.etLastname.setText(theUser.last_name);
            this.etMobile.setText(theUser.mobile);
        } else
            Toast.makeText(this, "User: " + userId + " not found!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        this.imgBtnHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.ibOk))
        {
            String firstName = this.etFirstname.getText().toString();
            String lastName = this.etLastname.getText().toString();
            String mobile = this.etMobile.getText().toString();

            User theUser = User.getUser(userId);
            if (theUser != null)
            {
                if (!firstName.equals(theUser.first_name) || !lastName.equals(theUser.last_name) || !mobile.equals(theUser.mobile))
                {
                    User newUser = new User(theUser.user_id, firstName, lastName, mobile, new Date().getTime());
                    if (User.merge(newUser))
                    {
                        Toast.makeText(this, "User details updated.", Toast.LENGTH_LONG).show();

                        TypeWorkingMode workMode = WorkingMode.getWorkingMode();
                        if (workMode == TypeWorkingMode.CLIENT_MODE)
                            UserSyncClient.getSharedInstance().pushWithBroadcastRequest(newUser);
                        else if (workMode == TypeWorkingMode.SERVER_MODE)
                        {
                            UserSyncServer.getSharedInstance().broadcast(newUser);
                        }
                    }

                    this.setResult(ActivityCoffeeOrder.REQUEST_CODE_EDIT_CUSTOMER_DETAILS);
                    this.finish();
                } else
                {
                    Toast.makeText(this, "Please make change before saving!", Toast.LENGTH_LONG).show();
                }
            } else
            {
                Toast.makeText(this, "The user: " + this.userId + " doesn't exist!", Toast.LENGTH_LONG).show();
            }
        } else if (v.equals(this.ibCancel))
        {
            this.finish();
        }
    }
}
