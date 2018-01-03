package orionhealth.app.activities.fragments.listFragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import orionhealth.app.R;
import orionhealth.app.activities.adaptors.ConditionListAdapter;
import orionhealth.app.activities.main.AddConditionActivity;
import orionhealth.app.activities.main.EditConditionActivity;
import orionhealth.app.data.medicationDatabase.CondTableOperations;

/**
 * Created by Lu on 13/07/16.
 */
public class ConditionListFragment extends ListFragment {


	public final static String SELECTED_COND_ID = "conditionListFragment.SELECTED_COND_ID";
	private ListView mSimpleListView;

	public ConditionListFragment() {
	}

	public static ConditionListFragment newInstance() {
		ConditionListFragment fragment = new ConditionListFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_condition_list, container, false);
		FloatingActionButton addButton =  (FloatingActionButton) view.findViewById(R.id.button_add_condition);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), AddConditionActivity.class);
				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Cursor cursor = CondTableOperations.getInstance().getAllRows(getContext());

		mSimpleListView = getListView();
		ConditionListAdapter listAdapter = new ConditionListAdapter(getContext(), cursor);
		mSimpleListView.setAdapter(listAdapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getContext(), EditConditionActivity.class);
		intent.putExtra(SELECTED_COND_ID, (int) id);
		startActivity(intent);
	};
}
