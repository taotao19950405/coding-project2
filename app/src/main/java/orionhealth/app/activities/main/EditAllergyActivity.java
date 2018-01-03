//       Description:
//		 @author:  ArchieKhanal

package orionhealth.app.activities.main;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import orionhealth.app.R;
import orionhealth.app.activities.fragments.dialogFragments.RemoveAllergyDialogFragment;
import orionhealth.app.activities.fragments.dialogFragments.RemoveMedicationDialogFragment;
import orionhealth.app.activities.fragments.fragments.AllergyDetailsFragment;
import orionhealth.app.activities.fragments.listFragments.AllergyListFragment;

public class EditAllergyActivity extends AppCompatActivity implements RemoveAllergyDialogFragment.RemoveAllergyDialogListener{

	private AllergyDetailsFragment aAllergyDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_allergy);

        Intent intent = getIntent();
        int allergyID = intent.getIntExtra(AllergyListFragment.SELECTED_ALLERGY_ID, 0);

        FragmentManager fragmentManager = getFragmentManager();
        aAllergyDetailsFragment =
                (AllergyDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_allergy_details);
        aAllergyDetailsFragment.setAllergy(this, allergyID);
        aAllergyDetailsFragment.populateFields();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_allergy, menu);
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

    public void removeAllergy(View view){
        aAllergyDetailsFragment.removeAllergy();
    }

    public void updateAllergyInDatabase(View view){
		try {
			aAllergyDetailsFragment.updateAllergyInDatabase(this);
			returnToMainActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    @Override
    public void onRemovePositiveClick(DialogFragment dialog) {
        aAllergyDetailsFragment.onRemovePositiveClick(this);
        returnToMainActivity();
    }

    @Override
    public void onRemoveNegativeClick(DialogFragment dialog) {

    }
}
