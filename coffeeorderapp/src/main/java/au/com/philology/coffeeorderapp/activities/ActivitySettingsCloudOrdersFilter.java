package au.com.philology.coffeeorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.database.CardReaderIdentifier;
import au.com.philology.coffeeorderapp.database.CloudOrderFilter;
import au.com.philology.coffeeorderapp.database.CloudOrderFilter.Filter_Type;
import au.com.philology.coffeeorderapp.database.InterestedCardReaderID;
import au.com.philology.coffeeorderapp.datasource.cloudorders.ICloudOrderDelegate;
import au.com.philology.coffeeorderapp.datasource.cloudorders.TheCloudOrdersService;
import au.com.philology.coffeeorderapp.dialogs.EditCardReaderDialog;
import au.com.philology.controls.AutoSelectListView;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class ActivitySettingsCloudOrdersFilter extends BasicActivity implements android.widget.RadioGroup.OnCheckedChangeListener, IDialogConfirmDelegate,
        ICloudOrderDelegate
{
    static final int TAG_ADD_CARD_READER_ID = 100;
    static final int TAG_DELETE_CARD_READER_ID = 101;

    ArrayAdapter<String> mArrayInterestedIdentifiers, mArrayAllAvailableIdentifers;
    Button btnAdd, btnRemove, btnEditTag, btnAddto, btnAddAllTo, btnRemoveFrom, btnRemoveAllFrom;
    RadioGroup rgFilters;
    AutoSelectListView lvInterestedIdentifiers, lvAllAvailableIdentifiers;
    LinearLayout theLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings_cloud_orders_filter);

        this.btnAdd = (Button) this.findViewById(R.id.btnAdd);
        this.btnAdd.setOnClickListener(this);

        this.btnRemove = (Button) this.findViewById(R.id.btnRemove);
        this.btnRemove.setOnClickListener(this);

        this.btnEditTag = (Button) this.findViewById(R.id.btnEditTag);
        this.btnEditTag.setOnClickListener(this);

        this.btnAddto = (Button) this.findViewById(R.id.btnAddTo);
        this.btnAddto.setOnClickListener(this);

        this.btnAddAllTo = (Button) this.findViewById(R.id.btnAddAllTo);
        this.btnAddAllTo.setOnClickListener(this);

        this.btnRemoveFrom = (Button) this.findViewById(R.id.btnRemoveFrom);
        this.btnRemoveFrom.setOnClickListener(this);

        this.btnRemoveAllFrom = (Button) this.findViewById(R.id.btnRemoveAllFrom);
        this.btnRemoveAllFrom.setOnClickListener(this);

        this.rgFilters = (RadioGroup) this.findViewById(R.id.rgFilter);

        this.theLinearLayout = (LinearLayout) this.findViewById(R.id.theLinearLayout);

        this.lvInterestedIdentifiers = (AutoSelectListView) this.findViewById(R.id.lvIdentifiers);
        this.mArrayInterestedIdentifiers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
        this.lvInterestedIdentifiers.setAdapter(this.mArrayInterestedIdentifiers);
        this.lvInterestedIdentifiers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        this.lvAllAvailableIdentifiers = (AutoSelectListView) this.findViewById(R.id.lvAllDetectedIdentifiers);
        this.mArrayAllAvailableIdentifers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
        this.lvAllAvailableIdentifiers.setAdapter(this.mArrayAllAvailableIdentifers);
        this.lvAllAvailableIdentifiers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        TheCloudOrdersService.getSharedInstance().addDelegate(this);

        boolean bIsFilterInterested = CloudOrderFilter.isFilterInterested();
        int checked = bIsFilterInterested ? 1 : 0;
        View theView = this.rgFilters.getChildAt(checked);
        this.rgFilters.clearCheck();
        this.rgFilters.check(theView.getId());
        this.rgFilters.setOnCheckedChangeListener(this);
        this.checkAgainstId(this.rgFilters.getCheckedRadioButtonId());

        ArrayList<CardReaderIdentifier> allCardReaderIDs = CardReaderIdentifier.getAll();
        for (CardReaderIdentifier aObject : allCardReaderIDs)
            this.addAvailableIdentifier(aObject.identifier, false);

        ArrayList<InterestedCardReaderID> all = InterestedCardReaderID.getAll();
        for (InterestedCardReaderID aObject : all)
        {
            this.removeAvailableIdentifier(aObject.interestedId, false);
            this.addInterestedIdentifier(aObject.interestedId);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.imgBtnInfor.setVisibility(View.INVISIBLE);
        this.imgBtnIcon.setVisibility(View.INVISIBLE);
        this.imgBtnHome.setVisibility(View.INVISIBLE);

        TheCloudOrdersService.getSharedInstance().addDelegate(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        TheCloudOrdersService.getSharedInstance().removeDelegate(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        TheCloudOrdersService.getSharedInstance().removeDelegate(this);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (v.equals(this.btnAdd))
        {
            DialogConfirm aDialog = new DialogConfirm(this, "Please type the identifier", "", true, R.layout.input_dialog, this, TAG_ADD_CARD_READER_ID);
            aDialog.show();
        } else if (v.equals(this.btnRemove))
        {
            int index = this.lvAllAvailableIdentifiers.getCheckedItemPosition();
            if (index > -1 && index < this.mArrayAllAvailableIdentifers.getCount())
            {
                DialogConfirm aDialog = new DialogConfirm(this, "Confirm", "Delete the card reader identifier?", false, 0, this, TAG_DELETE_CARD_READER_ID);
                aDialog.theData = index;
                aDialog.show();
            } else
                Toast.makeText(this, "Please select an item first!", Toast.LENGTH_LONG).show();
        } else if (v.equals(this.btnEditTag))
        {
            int index = this.lvAllAvailableIdentifiers.getCheckedItemPosition();
            if (index > -1 && index < this.mArrayAllAvailableIdentifers.getCount())
            {
                String identifier = this.mArrayAllAvailableIdentifers.getItem(index);
                EditCardReaderDialog aDialog = new EditCardReaderDialog(this, null, identifier);
                aDialog.show();
            } else
                Toast.makeText(this, "Please select an item first!", Toast.LENGTH_LONG).show();
        } else if (v.equals(this.btnAddto))
        {
            int index = this.lvAllAvailableIdentifiers.getCheckedItemPosition();
            if (index > -1 && index < this.mArrayAllAvailableIdentifers.getCount())
            {
                String text = this.mArrayAllAvailableIdentifers.getItem(index);
                this.addInterestedIdentifier(text);
                this.removeAvailableIdentifier(index, false);
            } else
                Toast.makeText(this, "No more available identifires to add!\nPlease scan a card on a device to add one.", Toast.LENGTH_LONG).show();
        } else if (v.equals(this.btnAddAllTo))
        {
            if (this.mArrayAllAvailableIdentifers.getCount() <= 0)
            {
                Toast.makeText(this, "No more available identifiers to add!\nPlease scan a card on a device to add one.", Toast.LENGTH_LONG).show();
                return;
            }

            while (this.mArrayAllAvailableIdentifers.getCount() > 0)
            {
                String text = this.mArrayAllAvailableIdentifers.getItem(0);
                this.removeAvailableIdentifier(0, false);
                this.addInterestedIdentifier(text);
            }
        } else if (v.equals(this.btnRemoveFrom))
        {
            int index = this.lvInterestedIdentifiers.getCheckedItemPosition();
            if (index > -1 && index < this.mArrayInterestedIdentifiers.getCount())
            {
                String text = this.mArrayInterestedIdentifiers.getItem(index);
                this.removeInterestedIdentifier(index);
                this.addAvailableIdentifier(text, false);
            } else
                Toast.makeText(this, "No more interested identifiers to move.", Toast.LENGTH_LONG).show();
        } else if (v.equals(this.btnRemoveAllFrom))
        {
            if (this.mArrayInterestedIdentifiers.getCount() <= 0)
            {
                Toast.makeText(this, "No more interested identifiers to move.", Toast.LENGTH_LONG).show();
                return;
            }

            while (this.mArrayInterestedIdentifiers.getCount() > 0)
            {
                String text = this.mArrayInterestedIdentifiers.getItem(0);
                this.removeInterestedIdentifier(0);
                this.addAvailableIdentifier(text, false);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        this.checkAgainstId(checkedId);
    }

    void checkAgainstId(int checkedId)
    {
        View object = this.rgFilters.findViewById(checkedId);
        View All = this.rgFilters.getChildAt(0);
        this.theLinearLayout.setVisibility(All.equals(object) ? View.INVISIBLE : View.VISIBLE);
        this.btnAdd.setVisibility(this.theLinearLayout.getVisibility());
        this.btnRemove.setVisibility(this.theLinearLayout.getVisibility());
        this.btnEditTag.setVisibility(this.theLinearLayout.getVisibility());

        CloudOrderFilter.updateCloudOrderFilter(All.equals(object) ? Filter_Type.FILTER_ALL : Filter_Type.FILTER_INTERESTED, new Date().getTime());
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        if (theDialog.tag == TAG_ADD_CARD_READER_ID)
        {
            EditText etContent = (EditText) theContentView.findViewById(R.id.etContent);
            if (etContent != null)
            {
                String text = etContent.getText().toString();
                this.addAvailableIdentifier(text, true);
            }
        } else if (theDialog.tag == TAG_DELETE_CARD_READER_ID)
        {
            Object data = theDialog.theData;
            if (data instanceof Integer)
            {
                int index = (Integer) data;
                this.removeAvailableIdentifier(index, true);
            }
        }
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
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
        final String id = deviceId;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                addAvailableIdentifier(id, true);
            }
        });
    }

    void addInterestedIdentifier(String text)
    {
        if (text == null)
            return;
        if (text.equalsIgnoreCase(""))
            return;

        if (this.mArrayInterestedIdentifiers.getPosition(text) <= -1)
            this.mArrayInterestedIdentifiers.add(text);

        InterestedCardReaderID.insertInterestedId(text, new Date().getTime());

        this.lvInterestedIdentifiers.autoSelect(this.mArrayInterestedIdentifiers.getPosition(text));
    }

    void removeInterestedIdentifier(String text)
    {
        int index = this.mArrayInterestedIdentifiers.getPosition(text);
        if (index > -1 && index < this.mArrayInterestedIdentifiers.getCount())
        {
            InterestedCardReaderID.removeInterestedId(text);

            this.mArrayInterestedIdentifiers.remove(text);
            this.lvInterestedIdentifiers.autoSelect(index);
        }
    }

    void removeInterestedIdentifier(int index)
    {
        if (index > -1 && index < this.mArrayInterestedIdentifiers.getCount())
        {
            String text = this.mArrayInterestedIdentifiers.getItem(index);
            if (text != null)
            {
                this.removeInterestedIdentifier(text);
            }
        }
    }

    void addAvailableIdentifier(String text, boolean addToDB)
    {
        if (text == null)
            return;
        if (text.equalsIgnoreCase(""))
            return;

        if (this.mArrayAllAvailableIdentifers.getPosition(text) <= -1)
            this.mArrayAllAvailableIdentifers.add(text);

        if (addToDB)
            CardReaderIdentifier.insert(text, "", "", 0, new Date().getTime());

        this.lvAllAvailableIdentifiers.autoSelect(this.mArrayAllAvailableIdentifers.getPosition(text));
    }

    void removeAvailableIdentifier(String text, boolean deleteFromDB)
    {
        int index = this.mArrayAllAvailableIdentifers.getPosition(text);
        if (index > -1 && index < this.mArrayAllAvailableIdentifers.getCount())
        {
            this.mArrayAllAvailableIdentifers.remove(text);
            this.lvAllAvailableIdentifiers.autoSelect(index);

            if (deleteFromDB)
            {
                CardReaderIdentifier.remove(text);
            }
        }
    }

    void removeAvailableIdentifier(int index, boolean deleteFromDB)
    {
        if (index > -1 && index < this.mArrayAllAvailableIdentifers.getCount())
        {
            String text = this.mArrayAllAvailableIdentifers.getItem(index);
            if (text != null)
            {
                this.removeAvailableIdentifier(text, deleteFromDB);
            }
        }
    }
}
