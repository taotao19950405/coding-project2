//       Description:
//		 @author:  Lu

package orionhealth.app.activities.main;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import orionhealth.app.R;
import orionhealth.app.activities.fragments.dialogFragments.DatePicker;
import orionhealth.app.activities.fragments.dialogFragments.RemoveConditionDialogFragment;
import orionhealth.app.activities.fragments.dialogFragments.RemoveMedicationDialogFragment;
import orionhealth.app.activities.fragments.fragments.ConditionDetailsFragment;
import orionhealth.app.activities.fragments.listFragments.ConditionListFragment;

public class EditConditionActivity extends AppCompatActivity implements DatePicker.DatePickerListener, RemoveConditionDialogFragment.RemoveCondDialogListener {

    private ConditionDetailsFragment mCondDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_condition);

        Intent intent = getIntent();
        int medicationID = intent.getIntExtra(ConditionListFragment.SELECTED_COND_ID, 0);

        FragmentManager fragmentManager = getFragmentManager();
        mCondDetailsFragment =
                (ConditionDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_condition_details);
        mCondDetailsFragment.setCondition(this, medicationID);
        mCondDetailsFragment.populateFields();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_condition, menu);
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

    public void removeCondition(View view) {
        mCondDetailsFragment.removeCondition();
    }

    public void updateConditionInDatabase(View view) {
		try {
			mCondDetailsFragment.updateConditionInDatabase(this);
			returnToMainActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onRemovePositiveClick(DialogFragment dialog) {
        mCondDetailsFragment.onRemovePositiveClick(this);
		returnToMainActivity();
    }

    @Override
    public void onRemoveNegativeClick(DialogFragment dialog) {

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
