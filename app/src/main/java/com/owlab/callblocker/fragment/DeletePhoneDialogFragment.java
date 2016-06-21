package com.owlab.callblocker.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.owlab.callblocker.R;
import com.owlab.callblocker.Utils;
import com.owlab.callblocker.content.CallBlockerProvider;

/**
 * Created by ernest on 5/15/16.
 */
public class DeletePhoneDialogFragment extends DialogFragment {
    private static final String TAG = DeletePhoneDialogFragment.class.getSimpleName();

    //public interface DeleteItemDialogListener {
    //    public void onDeleteItemCancelClick();
    //    public void onDeleteItemConirmClick(int _id);
    //}

    //DeleteItemDialogListener mDeleteItemDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //try {
        //    mDeleteItemDialogListener = (DeleteItemDialogListener) getTargetFragment();
        //} catch (ClassCastException e) {
        //    throw new ClassCastException(activity.toString() + " must implement DeleteItemDialogListener");
        //}
    }

    AlertDialog deleteFilteredItemDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, ">>> target fragment: " + getTargetFragment());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DeleteDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //final EditTextAddNumberDialogFragment input = new EditText(getActivity());
        Bundle arguments = getArguments();
        final int _id = arguments.getInt("_id");
        final String phoneNumber = Utils.formatPhoneNumber(arguments.getString("phoneNumber"));
        final String description = arguments.getString("description");

        View diagView = inflater.inflate(R.layout.delete_phone_dialog_layout, null);
        //TextView phoneNumberTextView = (TextView) diagView.findViewById(R.id.deleteDialog_textView_phoneNumber);
        //TextView descriptionTextView = (TextView) diagView.findViewById(R.id.deleteDialog_textView_description);

        //phoneNumberTextView.setText(Utils.formatPhoneNumber(phoneNumber));
        //phoneNumberTextView.setEnabled(false);
        //descriptionTextView.setText(description);
        //descriptionTextView.setEnabled(false);

        deleteFilteredItemDialog = builder
                //.setView(diagView)
                //.setParentView(input)
                .setIcon(R.drawable.ic_warning_48)
                .setTitle("You are deleting!")
                .setMessage(phoneNumber + "\n" + description)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mDeleteItemDialogListener.onDeleteItemConirmClick(_id);
                        dialog.dismiss();
                        //TODO not need uri encoding of _id?
                        Uri deleteUri = ContentUris.withAppendedId(CallBlockerProvider.BLOCKED_NUMBER_URI, _id);
                        int deleteCount = getTargetFragment().getActivity().getContentResolver().delete(deleteUri, null, null);
                        if(deleteCount > 0)
                        Snackbar.make(getTargetFragment().getView(), phoneNumber + " is deleted", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mDeleteItemDialogListener.onDeleteItemCancelClick();
                        dialog.dismiss();
                    }
                }).create();

        return deleteFilteredItemDialog;
    }
}
