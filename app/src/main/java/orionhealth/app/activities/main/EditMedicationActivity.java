//       Description:
//		 @author:  Bill

package orionhealth.app.activities.main;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import orionhealth.app.R;
import orionhealth.app.activities.fragments.dialogFragments.DatePicker;
import orionhealth.app.activities.fragments.dialogFragments.RemoveMedicationDialogFragment;
import orionhealth.app.activities.fragments.fragments.MedicationDetailsFragment;
import orionhealth.app.activities.fragments.listFragments.MedicationListFragment;

public class EditMedicationActivity extends AppCompatActivity implements RemoveMedicationDialogFragment.RemoveMedDialogListener,
                                                                         DatePicker.DatePickerListener {
	private MedicationDetailsFragment mMedDetailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_medication);

		Intent intent = getIntent();
		int medicationID = intent.getIntExtra(MedicationListFragment.SELECTED_MED_ID, 0);

		FragmentManager fragmentManager = getFragmentManager();
		mMedDetailsFragment =
		  		(MedicationDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_medication_details);
		mMedDetailsFragment.setMedication(this, medicationID);
		mMedDetailsFragment.populateFields();

	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_medication, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void removeMedication(View view){
		mMedDetailsFragment.removeMedication();
	}

	public void updateMedicationInDatabase(View view){
		try {
			mMedDetailsFragment.updateMedicationInDatabase(this);
			returnToMainActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void returnToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		returnToMainActivity();
		return;
	}

	@Override
	public void onRemovePositiveClick(DialogFragment dialog) {
		mMedDetailsFragment.onRemovePositiveClick(this);
		returnToMainActivity();
	}

	@Override
	public void onRemoveNegativeClick(DialogFragment dialog) {

	}

	@Override
	public void onSetDate(int year, int monthOfYear, int dayOfMonth, String tag) {
		mMedDetailsFragment.onSetDate(year, monthOfYear, dayOfMonth, tag);
	}

	@Override
	public void onCancelDate() {
		mMedDetailsFragment.onCancelDate();
	}

}
