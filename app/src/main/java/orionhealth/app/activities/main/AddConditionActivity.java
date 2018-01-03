//       Description:
//		 @author:  Lu
package orionhealth.app.activities.main;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

import orionhealth.app.R;
import orionhealth.app.activities.fragments.dialogFragments.DatePicker;
import orionhealth.app.activities.fragments.fragments.ConditionDetailsFragment;


import android.content.Context;
import android.widget.RadioButton;

public class AddConditionActivity extends AppCompatActivity  implements DatePicker.DatePickerListener{

	private ConditionDetailsFragment mCondDetailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_condition);

		FragmentManager fragmentManager = getFragmentManager();
		mCondDetailsFragment =
		  (ConditionDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_condition_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_add_condition, menu);
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

	public void returnToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void addConditionToDatabase(View view) {
		try {
			mCondDetailsFragment.addConditionToDatabase(this);
			returnToMainActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSetDate(int year, int monthOfYear, int dayOfMonth, String tag) {
		mCondDetailsFragment.onSetDate(year, monthOfYear, dayOfMonth, tag);
	}

	@Override
	public void onCancelDate() {
		mCondDetailsFragment.onCancelDate();
	}
}
