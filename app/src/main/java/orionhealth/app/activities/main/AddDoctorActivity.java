package orionhealth.app.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import orionhealth.app.R;
import orionhealth.app.activities.fragments.fragments.DoctorDetailsFragment;

/**
 * Created by luchen on 25/9/16.
 */

public class AddDoctorActivity extends AppCompatActivity {
    private DoctorDetailsFragment mDocDetailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mDocDetailsFragment =
                (DoctorDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_doctor_details);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_doctor, menu);
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

    public void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addMedicationToDatabase(View view) {
        try {
            mDocDetailsFragment.addMedicationToDatabase(this);
            returnToMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
