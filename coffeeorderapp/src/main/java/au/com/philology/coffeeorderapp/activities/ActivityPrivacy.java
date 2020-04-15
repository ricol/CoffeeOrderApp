package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import au.com.philology.coffeeorderapp.R;

public class ActivityPrivacy extends BasicActivity
{
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.privacy);
        this.tvContent = (TextView) this.findViewById(R.id.tvContent);
        String txt = "<html><body><br />Virgin Australia is committed to protecting the privacy of your personal information. This commitment is demonstrated in our Privacy Policy which tells you how we manage your personal information and how to contact us if you have any privacy concerns. <br /> <br />" +
                "You have a right to request access to the personal information that we hold about you and to ask us to correct it. You also have a right to make a complaint about how we have managed your personal information. <br /><br />" +
                "If you have a complaint about how we collect, hold, use or disclose your personal information or how we have dealt with a request by you to access or correct your personal information, you can make a complaint to us by: <br /><br />" +
                "1. Calling us<br />" +
                "2. Writing to us at the below address: <br /><br />" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Privacy Officer<br />" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Legal Department <br />" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PO Box 1034 <br />" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Spring Hill QLD 4004 <br /><br />" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Email: privacy@virginaustralia.com<br /></body></html>";
        this.tvContent.setText(Html.fromHtml(txt));
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);
    }

}
