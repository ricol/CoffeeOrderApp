package au.com.philology.coffeeorderapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

@SuppressLint("NewApi")
public class MyPrintDocumentAdapter extends PrintDocumentAdapter
{
    Context theContext;

    public MyPrintDocumentAdapter(Context context)
    {
        this.theContext = context;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback)
    {
        // TODO Auto-generated method stub

    }

}
