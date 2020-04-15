package au.com.philology.coffeeorderapp.dialogs;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;

import au.com.philology.coffeeorderapp.R;
import au.com.philology.coffeeorderapp.database.CardReaderIdentifier;
import au.com.philology.coffeeorderapp.database.InterestedCardReaderID;
import au.com.philology.dialogs.DialogConfirm;
import au.com.philology.dialogs.IDialogConfirmDelegate;

public class EditCardReaderDialog extends DialogConfirm implements IDialogConfirmDelegate
{
    public IEditCardReaderDialogDelegate theDelegateForEditCardReaderDialog;
    TextView tvCardReaderIdentifier;
    EditText etCardReaderName;
    EditText etCardReaderDesc;
    RadioGroup rgCardReaderPrint;
    String cardReaderID;

    public EditCardReaderDialog(Activity theActivity, IEditCardReaderDialogDelegate theDelegate, String identifier)
    {
        super(theActivity, "Edit Card Reader", "", true, R.layout.dialog_edit_card_reader, null, 0);

        this.theDelegate = this;
        this.cardReaderID = identifier;
        this.tvCardReaderIdentifier = (TextView) this.theView.findViewById(R.id.tvCardReaderIdentifierValue);
        this.etCardReaderName = (EditText) this.theView.findViewById(R.id.etCardReaderName);
        this.etCardReaderDesc = (EditText) this.theView.findViewById(R.id.etCardReaderDescription);
        this.rgCardReaderPrint = (RadioGroup) this.theView.findViewById(R.id.rgPrint);

        CardReaderIdentifier theCard = CardReaderIdentifier.getObject(identifier);
        if (theCard != null)
        {
            this.tvCardReaderIdentifier.setText(theCard.identifier);
            this.etCardReaderName.setText(theCard.name);
            this.etCardReaderDesc.setText(theCard.desc);

            this.rgCardReaderPrint.check(-1);
            int value = theCard.autoprint;
            this.rgCardReaderPrint.check(this.rgCardReaderPrint.getChildAt(value).getId());
        } else
        {
            this.tvCardReaderIdentifier.setText("Not Found!");
        }
    }

    @Override
    public void show()
    {
        super.show();

        this.dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void dialogConfirmOnOk(DialogConfirm theDialog, View theContentView)
    {
        CardReaderIdentifier theCardReader = new CardReaderIdentifier(this.cardReaderID, "", "", 0, new Date().getTime());
        theCardReader.name = this.etCardReaderName.getText().toString();
        theCardReader.desc = this.etCardReaderDesc.getText().toString();
        theCardReader.autoprint = this.rgCardReaderPrint.getCheckedRadioButtonId() == this.rgCardReaderPrint.getChildAt(0).getId() ? 0 : 1;
        theCardReader.save();

        InterestedCardReaderID theInterested = new InterestedCardReaderID(this.cardReaderID, new Date().getTime());
        theInterested.save();

        if (theDelegateForEditCardReaderDialog != null)
            theDelegateForEditCardReaderDialog.editCardReaderDialogOnOk(this.cardReaderID);
    }

    @Override
    public void dialogConfirmOnCancel(DialogConfirm theDialog, View theContentView)
    {
        if (theDelegateForEditCardReaderDialog != null)
            theDelegateForEditCardReaderDialog.editCardReaderDialogOnCancel(this.cardReaderID);
    }
}
