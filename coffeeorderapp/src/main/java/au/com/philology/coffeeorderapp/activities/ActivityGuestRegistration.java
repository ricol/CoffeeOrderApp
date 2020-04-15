package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import au.com.philology.coffeeorderapp.R;

public class ActivityGuestRegistration extends BasicActivity
{
    ImageButton btnOk, BtnCancel;
    EditText etFirstName, etLastName, etMobile;
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.guestregister);

        this.btnOk = (ImageButton) this.findViewById(R.id.ImageButtonGuestRegisterOK);
        this.btnOk.setOnClickListener(this);

        this.BtnCancel = (ImageButton) this.findViewById(R.id.ImageButtonGuestRegisterCancel);
        this.BtnCancel.setOnClickListener(this);

        this.etFirstName = (EditText) this.findViewById(R.id.editTextGuestRegisterFirstName);
        this.etLastName = (EditText) this.findViewById(R.id.editTextGuestRegisterLastName);
        this.etMobile = (EditText) this.findViewById(R.id.editTextGuestRegisterMobile);

        Intent tmpIntent = this.getIntent();
        this.tag = tmpIntent.getStringExtra("tag");
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnOk))
        {
            Intent tmpIntent = new Intent();
            tmpIntent.putExtra("firstname", this.etFirstName.getText().toString());
            tmpIntent.putExtra("lastname", this.etLastName.getText().toString());
            tmpIntent.putExtra("mobile", this.etMobile.getText().toString());
            tmpIntent.putExtra("tag", this.tag);
            this.setResult(ActivityMain.REQUEST_CODE, tmpIntent);
            this.finish();
        } else if (v.equals(this.BtnCancel))
        {
            this.finish();
        }
    }
}
