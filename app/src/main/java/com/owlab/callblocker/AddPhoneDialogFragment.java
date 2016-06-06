package com.owlab.callblocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.owlab.callblocker.content.CallBlockerContentProvider;
import com.owlab.callblocker.content.CallBlockerTbl;

/**
 * Created by ernest on 5/15/16.
 */
public class AddPhoneDialogFragment extends DialogFragment{
    private static final String TAG = AddPhoneDialogFragment.class.getSimpleName();

    //public interface AddItemDialogListener {
    //    public void onAddItemDialogAddClick(DialogFragment dialog);
    //    public void onAddItemDialogCancelClick(DialogFragment dialog);
    //}

    //AddItemDialogListener mAddItemDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //try {
        //    mAddItemDialogListener = (AddItemDialogListener) getTargetFragment();
        //} catch(ClassCastException e) {
        //    throw new ClassCastException(activity.toString() + " must implement AddItemDialogListener");
        //}
    }

    //AlertDialog addFilteredItemDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, ">>> target fragment: " + getTargetFragment() );

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AddDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //final EditText input = new EditText(getActivity());
        View diagView = inflater.inflate(R.layout.add_phone_dialog_layout, null);
        AlertDialog addPhoneNumberDialog = builder
                .setView(diagView)
                //.setParentView(input)
                .setTitle("Add new phone number")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogI, int id) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //AddItemDialogFragment.this.getDialog().cancel();
                        //mAddItemDialogListener.onAddItemDialogCancelClick(AddItemDialogFragment.this);
                        dialog.dismiss();
                    }

                }).create();

        addPhoneNumberDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final AlertDialog alertDialog = (AlertDialog)dialog;
                EditText phoneNumberEditText = (EditText) alertDialog.findViewById(R.id.add_phone_dialog_phone_number);
                phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d(TAG, ">>>>> beforeTextChanged called");
                        super.beforeTextChanged(s, start, count, after);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.d(TAG, ">>>>> onTextChanged called");
                        super.onTextChanged(s, start, before, count);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d(TAG, ">>>>> afterTextChanged called");
                        super.afterTextChanged(s);
                    }
                });

                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, ">>> clicked");
                        EditText phoneNumberText = (EditText)alertDialog.findViewById(R.id.add_phone_dialog_phone_number);
                        EditText descriptionText = (EditText)alertDialog.findViewById(R.id.add_phone_dialog_description);
                        if(phoneNumberText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "Phone number can not be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            //mAddItemDialogListener.onAddItemDialogAddClick(AddItemDialogFragment.this);
                            ContentValues values = new ContentValues();
                            values.put(CallBlockerTbl.Schema.COLUMN_NAME_PHONE_NUMBER, phoneNumberText.getText().toString().replaceAll("[^\\d]", ""));
                            values.put(CallBlockerTbl.Schema.COLUMN_NAME_DESCRIPTION, descriptionText.getText().toString());
                            Uri newUri = getTargetFragment().getActivity().getContentResolver().insert(CallBlockerContentProvider.CONTENT_URI, values);
                            Log.d(TAG, ">>> newUri: " + newUri.toString());
                            Toast.makeText(getTargetFragment().getActivity(), "Successfully added and activated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return addPhoneNumberDialog;
    }
}
