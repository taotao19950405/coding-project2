//       Description:
//		 @author:  Bill
package orionhealth.app.activities.main;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import orionhealth.app.R;
import orionhealth.app.activities.fragments.dialogFragments.DatePicker;
import orionhealth.app.activities.fragments.fragments.MedicationDetailsFragment;

public class AddMedicationActivity extends AppCompatActivity implements DatePicker.DatePickerListener {

	private MedicationDetailsFragment mMedDetailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_medication);

		FragmentManager fragmentManager = getFragmentManager();
		mMedDetailsFragment =
		  (MedicationDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_medication_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_add_medication, menu);
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

	@Override
	public void onBackPressed() {
		returnToMainActivity();
		return;
	}

	public void addMedicationToDatabase(View view) {
		try {
			mMedDetailsFragment.addMedicationToDatabase(this);
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
	public void onSetDate(int year, int monthOfYear, int dayOfMonth, String tag) {
		mMedDetailsFragment.onSetDate(year, monthOfYear, dayOfMonth, tag);
	}

	@Override
	public void onCancelDate() {
		mMedDetailsFragment.onCancelDate();
	}
}
