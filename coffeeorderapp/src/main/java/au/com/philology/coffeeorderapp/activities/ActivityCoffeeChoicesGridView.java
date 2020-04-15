package au.com.philology.coffeeorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.adapter.CoffeeTypeGridViewAdapter;
import au.com.philology.coffeeorderapp.database.CoffeeType;

public class ActivityCoffeeChoicesGridView extends BasicActivity implements OnItemClickListener
{
    String userid;
    CoffeeTypeGridViewAdapter theAdapter;
    ImageButton btnBack, btnCancel, btnConfirm;
    int selected = -1;
    int originalSelected = -1;
    TextView tvSelected;
    ImageView imageViewCurrentChoice;
    GridView theGridView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coffee_choices_gridview);

        this.theAdapter = new CoffeeTypeGridViewAdapter(this, this.getLayoutInflater());
        this.btnBack = (ImageButton) this.findViewById(R.id.ImageBtnChoiceBack);
        this.btnCancel = (ImageButton) this.findViewById(R.id.imageBtnChoiceCancel);
        this.btnConfirm = (ImageButton) this.findViewById(R.id.imageBtnChoiceConfirm);
        this.btnBack.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
        this.btnConfirm.setOnClickListener(this);
        this.tvSelected = (TextView) this.findViewById(R.id.tvSelected);
        this.imageViewCurrentChoice = (ImageView) this.findViewById(R.id.imageViewCurrentChoice);

        Intent tmpIntent = this.getIntent();
        this.userid = tmpIntent.getStringExtra("userid");
        this.originalSelected = tmpIntent.getIntExtra("original", -1);
        if (this.originalSelected > -1)
        {
            String coffeeId = this.theAdapter.getCoffeeId(this.originalSelected);
            CoffeeType coffee = CoffeeType.getCoffeeType(coffeeId);
            this.tvSelected.setText(coffee.label);
            this.imageViewCurrentChoice.setImageResource(this.getResources().getIdentifier(coffee.image, "drawable", this.getPackageName()));
        }

        this.theGridView = (GridView) this.findViewById(R.id.gridViewCoffeeChoices);
        this.theGridView.setAdapter(this.theAdapter);
        this.theGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);

        for (int i = 0; i < this.theAdapter.getCount(); i++)
            this.theAdapter.setUnselected(i);
        if (selected >= 0 && selected < theAdapter.getCount())
            theAdapter.setSelected(selected);
        else
        {
            if (originalSelected >= 0 && originalSelected < theAdapter.getCount())
                theAdapter.setSelected(originalSelected);
        }

        theAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        this.selected = arg2;

        String coffeeId = this.theAdapter.getCoffeeId(arg2);
        CoffeeType coffee = CoffeeType.getCoffeeType(coffeeId);
        this.tvSelected.setText(coffee.label);
        this.imageViewCurrentChoice.setImageResource(this.getResources().getIdentifier(coffee.image, "drawable", this.getPackageName()));

        for (int i = 0; i < this.theAdapter.getCount(); i++)
            this.theAdapter.setUnselected(i);
        theAdapter.setSelected(arg2);

        theAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnConfirm))
        {
            if (selected > -1)
            {
                Intent tmpIntent = new Intent(this, ActivityCoffeeOrder.class);
                tmpIntent.putExtra("coffeeid", this.theAdapter.getCoffeeId(selected));
                tmpIntent.putExtra("userid", this.userid);
                this.startActivity(tmpIntent);

                this.finish();
            } else
                this.onClick(this.btnBack);
        } else if (v.equals(this.btnCancel))
        {
            super.onClick(this.imgBtnHome);
        } else if (v.equals(this.btnBack))
        {
            if (originalSelected > -1)
            {
                Intent tmpIntent = new Intent(this, ActivityCoffeeOrder.class);
                tmpIntent.putExtra("coffeeid", this.theAdapter.getCoffeeId(originalSelected));
                tmpIntent.putExtra("userid", this.userid);
                this.startActivity(tmpIntent);

                this.finish();
            } else
            {
                Toast.makeText(this, "Please select your drink type!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
