package orionhealth.app.activities.fragments.listFragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.commonsware.cwac.merge.MergeAdapter;
import orionhealth.app.R;
import orionhealth.app.activities.adaptors.MedReminderListAdaptor;
import orionhealth.app.activities.main.TakeMedicationActivity;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;

/**
 * Created by bill on 5/09/16.
 */
public class MedReminderListFragment extends ListFragment {
	private int medStatus;
	private View headerView;
	private View headerView2;
	private View headerView3;
	private View emptyListMessage;
	private int overdueMedNum = 0;

	private MedReminderListAdaptor cursorAdapter;

	public MedReminderListFragment() {
	}

	public static MedReminderListFragment newInstance() {
		return newInstance(MedUptakeStatus.PENDING.ordinal());
	}

	public static MedReminderListFragment newInstance(int status) {
		MedReminderListFragment fragment = new MedReminderListFragment();
		fragment.medStatus = status;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View resultView = inflater.inflate(R.layout.fragment_med_reminder_list, container, false);
		headerView = inflater.inflate(R.layout.template_header, container, false);
		TextView textView = (TextView) headerView.findViewById(R.id.header_text);
		textView.setText("Overdue Medication");
		textView.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red));
		View divider = headerView.findViewById(R.id.header_divider);
		divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_red));
		headerView2 = inflater.inflate(R.layout.template_header, container, false);
		textView = (TextView) headerView2.findViewById(R.id.header_text);
		textView.setText("Pending Medication");
		headerView3 = inflater.inflate(R.layout.template_header, container, false);
		textView = (TextView) headerView3.findViewById(R.id.header_text);
		textView.setTextColor(ContextCompat.getColor(getContext(), R.color.light_Green));
		divider = headerView3.findViewById(R.id.header_divider);
		divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_Green));
		textView.setText("Taken Medication");
		emptyListMessage = inflater.inflate(R.layout.template_empty_list_message, container, false);
		return resultView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MergeAdapter mergeAdapter = new MergeAdapter();
		Cursor cursor =
		  MedTableOperations.getInstance().
			getMedRemindersForStatus((getContext()), MedUptakeStatus.OVERDUE.ordinal());
		if (cursor.getCount() != 0) {
			mergeAdapter.addView(headerView);
			overdueMedNum = cursor.getCount();
		}
		MedReminderListAdaptor listAdapter = new MedReminderListAdaptor(getContext(), cursor, R.drawable.cancel);
		mergeAdapter.addAdapter(listAdapter);
		mergeAdapter.addView(headerView2);

		cursor = MedTableOperations.getInstance().
		  getMedRemindersForStatus(getContext(), MedUptakeStatus.PENDING.ordinal());

		if (cursor.getCount() == 0){
			mergeAdapter.addView(emptyListMessage);
		}

		listAdapter = new MedReminderListAdaptor(getContext(), cursor, R.drawable.clock);

		mergeAdapter.addAdapter(listAdapter);

		cursor = MedTableOperations.getInstance().
		  getMedRemindersForStatus(getContext(), MedUptakeStatus.TAKEN.ordinal());

		if (cursor.getCount() != 0) {
			mergeAdapter.addView(headerView3);
			listAdapter = new MedReminderListAdaptor(getContext(), cursor, R.drawable.checked);
			mergeAdapter.addAdapter(listAdapter);
		}


		ListView listView = getListView();
		listView.setAdapter(mergeAdapter);
		listView.addFooterView(new View(getContext()), null, true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		if (position <= overdueMedNum) {
			Intent activityIntent = new Intent(getContext(), TakeMedicationActivity.class);
			getContext().startActivity(activityIntent);
		}

	};

	public MedReminderListAdaptor getCursorAdapter() {
		return cursorAdapter;
	}
}
