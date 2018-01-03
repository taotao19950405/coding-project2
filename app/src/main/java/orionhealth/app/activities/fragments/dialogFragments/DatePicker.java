package orionhealth.app.activities.fragments.dialogFragments;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by bill on 22/06/16.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	private String mTitle = "Set Date";
	private int year;
	private int month;
	private int day;

	DatePickerListener datePickerListener;

	public interface DatePickerListener {
		void onSetDate(int year, int monthOfYear, int dayOfMonth, String tag);
		void onCancelDate();
	}

	public void setDate() {
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
	}

	public void setDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the RemoveMedDialogListener so we can send events to the host
			datePickerListener = (DatePickerListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
			  + " must implement datePickerListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Create a new instance of DatePicker and return it
		final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);

		datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_NEGATIVE) {
					dialog.dismiss();
					datePickerListener.onCancelDate();
				}
			}
		});
		datePickerDialog.setCanceledOnTouchOutside(false);
		datePickerDialog.getDatePicker().setCalendarViewShown(false);
		datePickerDialog.getDatePicker().setSpinnersShown(true);

		datePickerDialog.setTitle(mTitle);
		return datePickerDialog;
	}

	@Override
	public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			datePickerListener.onSetDate(year, monthOfYear, dayOfMonth, getTag());

	}

}
