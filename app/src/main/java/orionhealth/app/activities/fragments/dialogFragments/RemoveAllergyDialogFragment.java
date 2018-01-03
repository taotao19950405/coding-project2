package orionhealth.app.activities.fragments.dialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import orionhealth.app.R;

/**
 * Created by archanakhanal on 20/7/2016.
 */
public class RemoveAllergyDialogFragment extends DialogFragment {
/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */

    public interface RemoveAllergyDialogListener {
        void onRemovePositiveClick(DialogFragment dialog);
        void onRemoveNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    RemoveAllergyDialogListener aListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RemoveMedDialogListener so we can send events to the host
            aListener = (RemoveAllergyDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RemoveAllergyDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_allergy_dialog_title).setMessage(R.string.remove_allergy_dialog_message)

                .setPositiveButton(R.string.remove_allergy_dialog_positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        aListener.onRemovePositiveClick(RemoveAllergyDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.remove_allergy_dialog_negative_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        aListener.onRemoveNegativeClick(RemoveAllergyDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
