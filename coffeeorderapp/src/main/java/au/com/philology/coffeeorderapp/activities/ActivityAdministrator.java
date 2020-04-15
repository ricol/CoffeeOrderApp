package au.com.philology.coffeeorderapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.database.CoffeeType;
import au.com.philology.coffeeorderapp.database.Order;
import au.com.philology.coffeeorderapp.database.User;
import au.com.philology.coffeeorderapp.printer.ThePrinter;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class ActivityAdministrator extends BasicActivity implements IDialogConfirmDelegate
{
    static final int TAG_CLEAR = 100;
    static final int TAG_PRINT = 101;
    ListView lvListOfOrders;
    Button btnPrint, btnReload, btnClear, btnDateFrom, btnDateTo;
    ArrayAdapter<String> mArrayAdapterOrders;
    Calendar dateFrom;
    Calendar dateTo;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.administration);

        this.lvListOfOrders = (ListView) this.findViewById(R.id.lvListOfOrders);
        this.mArrayAdapterOrders = new ArrayAdapter<String>(this, R.layout.list_view_item);
        this.lvListOfOrders.setAdapter(mArrayAdapterOrders);
        this.btnPrint = (Button) this.findViewById(R.id.btnPrint);
        this.btnPrint.setOnClickListener(this);

        this.btnReload = (Button) this.findViewById(R.id.btnReload);
        this.btnReload.setOnClickListener(this);

        this.btnClear = (Button) this.findViewById(R.id.btnClear);
        this.btnClear.setOnClickListener(this);

        this.btnDateFrom = (Button) this.findViewById(R.id.btnDateFrom);
        this.btnDateFrom.setOnClickListener(this);

        this.btnDateTo = (Button) this.findViewById(R.id.btnDateTo);
        this.btnDateTo.setOnClickListener(this);

        Calendar now = Calendar.getInstance();
        long timestamp = now.getTimeInMillis();
        Calendar yesterdayCal = new GregorianCalendar();
        yesterdayCal.setTimeInMillis(timestamp - 24 * 60 * 60 * 1000);
        this.updateDateFrom(yesterdayCal); // yesterday
        this.updateDateTo(now); // now
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();

        this.Reload();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // this.btnGoHome.setVisibility(View.INVISIBLE);
        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnBack.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        super.onClick(v);

        if (v.equals(this.btnPrint))
        {
            DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Print the orders?", false, 0, this, TAG_PRINT);
            aDialog.show();
        } else if (v.equals(this.btnClear))
        {
            DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Clear all orders from the database?", false, 0, this, TAG_CLEAR);
            aDialog.show();
        } else if (v.equals(this.btnReload))
        {
            this.Reload();
        } else if (v.equals(this.btnDateFrom))
        {
            int year = dateFrom.get(Calendar.YEAR);
            int month = dateFrom.get(Calendar.MONTH);
            int day = dateFrom.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
            {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    updateDateFrom(cal);
                }
            }, year, month, day).show();
        } else if (v.equals(this.btnDateTo))
        {
            int year = dateTo.get(Calendar.YEAR);
            int month = dateTo.get(Calendar.MONTH);
            int day = dateTo.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
            {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    updateDateTo(cal);
                }
            }, year, month, day).show();
        }
    }

    void Reload()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                final ArrayList<Order> all = Order.getOrders(dateFrom.getTime(), dateTo.getTime());

                runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        mArrayAdapterOrders.clear();

                        for (int i = 0; i < all.size(); i++)
                        {
                            Order aOrder = all.get(i);

                            Date tmpDate = new Date(aOrder.timestamp);
                            String strDate = df.format(tmpDate);

                            String firstname = "";
                            String lastname = "";

                            User tmpUser = User.getUser(aOrder.user_id);
                            if (tmpUser != null)
                            {
                                firstname = tmpUser.first_name;
                                lastname = tmpUser.last_name;
                            }

                            String coffeetypename = "";
                            CoffeeType tmpType = CoffeeType.getCoffeeType(aOrder.coffee_id);
                            if (tmpType != null)
                            {
                                coffeetypename = tmpType.label;
                            }
                            mArrayAdapterOrders.add("\tOrder id: " + (i + 1) + "\n\tUser id: " + aOrder.user_id + "\n\tUser Name: " + firstname + " "
                                    + lastname + "\n\tCoffee id: " + aOrder.coffee_id + "\n\tCoffee name: " + coffeetypename + "\n\tDevice id: "
                                    + aOrder.device_id + "\n\tTimestamp: " + strDate);
                        }

                        Log.i("DEBUG", "Total Orders: " + mArrayAdapterOrders.getCount());
                    }
                });
            }

        }).start();
    }

    void Clear()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Order.clear();

                runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        Reload();
                    }
                });
            }

        }).start();
    }

    void updateDateFrom(Calendar date)
    {
        dateFrom = date;
        this.btnDateFrom.setText(this.df.format(dateFrom.getTime()));
    }

    void updateDateTo(Calendar date)
    {
        dateTo = date;
        this.btnDateTo.setText(this.df.format(dateTo.getTime()));
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        if (theDialog.tag == TAG_CLEAR)
        {
            this.Clear();
            Toast.makeText(this, "Orders cleared!", Toast.LENGTH_LONG).show();
        } else if (theDialog.tag == TAG_PRINT)
        {
            StringBuilder AllOrders = new StringBuilder();
            AllOrders.append("List or orders:\n" + "Date: " + new Date() + "\n\n");
            for (int i = 0; i < this.mArrayAdapterOrders.getCount(); i++)
            {
                String aOrder = this.mArrayAdapterOrders.getItem(i);
                AllOrders.append(aOrder + "\n");
            }

            AllOrders.append("\nTotal: " + this.mArrayAdapterOrders.getCount());
            ThePrinter.getSharedInstance().print(AllOrders.toString());
        }
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
    {

    }

}
