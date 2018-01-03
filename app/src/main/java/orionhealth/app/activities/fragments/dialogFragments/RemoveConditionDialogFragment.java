package orionhealth.app.activities.fragments.dialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import orionhealth.app.R;

/**
 * Created by lu on 19/07/16.
 */
public class RemoveConditionDialogFragment extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */

	public interface RemoveCondDialogListener {
		void onRemovePositiveClick(DialogFragment dialog);
		void onRemoveNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	RemoveCondDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the RemoveCondDialogListener so we can send events to the host
			mListener = (RemoveCondDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
			  + " must implement RemoveCondDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.remove_cond_dialog_title).setMessage(R.string.remove_cond_dialog_message)
		  .setPositiveButton(R.string.remove_med_dialog_positive_button, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
				  mListener.onRemovePositiveClick(RemoveConditionDialogFragment.this);
			  }
		  })
		  .setNegativeButton(R.string.remove_med_dialog_negative_button, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
				  mListener.onRemoveNegativeClick(RemoveConditionDialogFragment.this);
			  }
		  });
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
