package com.penrosewharf.brian.weatherly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

//Dialog Fragment is the base class we want to extend
public class AlertDialogFragment extends DialogFragment {
    private String mErrorText;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //This context variable will give the context where the error occurs
        Context context = getActivity();
        //Use context rather than this as this will be reused in other activities
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(getErrorText())
                //Set buttons on Dialog - null closes the dialog
                .setPositiveButton(R.string.error_ok_button_text, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void setErrorText(String errorText){
        mErrorText = errorText;
    }

    public String getErrorText(){
        return mErrorText;
    }

}
