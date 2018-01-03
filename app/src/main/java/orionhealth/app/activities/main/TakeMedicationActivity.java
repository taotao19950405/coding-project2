package orionhealth.app.activities.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import orionhealth.app.R;
import orionhealth.app.activities.adaptors.MyAdapter;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;
import orionhealth.app.services.RingToneService;

public class TakeMedicationActivity extends AppCompatActivity {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trial);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		getSupportActionBar().setElevation(0);
		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		final Cursor cursor =
		  MedTableOperations.getInstance().
			getMedRemindersForStatus(this, MedUptakeStatus.OVERDUE.ordinal());

		mAdapter = new MyAdapter(this, cursor) {
			@Override
			public void onTakeButtonClick(long remId, int position) {
				Intent service = new Intent(getApplicationContext(), RingToneService.class);
				getApplicationContext().stopService(service);
				MedTableOperations.getInstance().changeMedReminderStatus(getApplicationContext(), (int) remId, MedUptakeStatus.TAKEN.ordinal());
				setCursor();
				notifyItemRemoved(position);
				notifyItemRangeChanged(position, getItemCount());
				NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel((int) remId);
				if (getItemCount() == 0) {
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				}
			}

		};
		mRecyclerView.setAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trial, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		return super.onOptionsItemSelected(item);
	}

}
