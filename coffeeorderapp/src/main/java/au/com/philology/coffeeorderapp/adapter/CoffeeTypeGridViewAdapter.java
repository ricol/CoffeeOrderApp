package au.com.philology.coffeeorderapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.database.CoffeeType;

public class CoffeeTypeGridViewAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater mInflater;
    ArrayList<CoffeeType> mArrayList;
    ArrayList<String> mArrayListCoffeeIds;
    ArrayList<String> mArrayListCoffeeLabels;
    ArrayList<String> mArrayListCoffeeDescriptions;
    ArrayList<String> mArrayListCoffeeImages;
    ArrayList<Float> mArrayListCoffeePrices;
    ArrayList<Boolean> mArrayListSelected;

    public CoffeeTypeGridViewAdapter(Context context, LayoutInflater inflater)
    {
        mContext = context;
        mInflater = inflater;
        mArrayList = CoffeeType.getAllCoffeeTypes();
        mArrayListCoffeeIds = new ArrayList<String>();
        mArrayListCoffeeLabels = new ArrayList<String>();
        mArrayListCoffeeDescriptions = new ArrayList<String>();
        mArrayListCoffeeImages = new ArrayList<String>();
        mArrayListCoffeePrices = new ArrayList<Float>();
        mArrayListSelected = new ArrayList<Boolean>();

        for (int i = 0; i < mArrayList.size(); i++)
        {
            CoffeeType tmpCoffee = mArrayList.get(i);

            mArrayListCoffeeIds.add(tmpCoffee.id);
            mArrayListCoffeeLabels.add(tmpCoffee.label);
            mArrayListCoffeeDescriptions.add(tmpCoffee.description);
            mArrayListCoffeeImages.add(tmpCoffee.image);
            mArrayListCoffeePrices.add(tmpCoffee.price);
            mArrayListSelected.add(false);
        }
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mArrayListCoffeeIds.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mArrayListCoffeeLabels.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CoffeeTypeViewHolderGrid holder;

        // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null)
        {
            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_coffee_type_gridview, null);

            // create a new "Holder" with subviews
            holder = new CoffeeTypeViewHolderGrid();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewCoffee);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.imageViewTick = (ImageView) convertView.findViewById(R.id.imageViewTick);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else
        {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (CoffeeTypeViewHolderGrid) convertView.getTag();
        }

        holder.tvTitle.setText(mArrayListCoffeeLabels.get(position));
        holder.tvDescription.setText(mContext.getResources().getIdentifier(mArrayListCoffeeDescriptions.get(position), "string", mContext.getPackageName()));
        holder.imageView.setImageResource(mContext.getResources().getIdentifier(mArrayListCoffeeImages.get(position), "drawable", mContext.getPackageName()));
        holder.imageViewTick.setVisibility(mArrayListSelected.get(position) ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    public String getCoffeeId(int position)
    {
        return this.mArrayListCoffeeIds.get(position);
    }

    public int getCoffeeOrderIdFromIdentifier(String identifier)
    {
        for (int i = 0; i < this.mArrayListCoffeeIds.size(); i++)
        {
            String value = this.mArrayListCoffeeIds.get(i);
            if (value.equals(identifier))
                return i;
        }

        return -1;
    }

    public void setSelected(int position)
    {
        mArrayListSelected.set(position, true);
    }

    public void setUnselected(int position)
    {
        mArrayListSelected.set(position, false);
    }

}