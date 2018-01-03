package orionhealth.app.activities.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import orionhealth.app.R;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.services.DateService;

import java.util.Calendar;

/**
 * Created by bill on 19/09/16.
 */
public class TakenMedListAdaptor extends BaseExpandableListAdapter {
	private Context mContext;
	private Cursor mCursor;

	public TakenMedListAdaptor(Context context, Cursor mCursor) {
		this.mContext = context;
		this.mCursor = mCursor;
	}

	@Override
	public int getGroupCount() {
		return 1;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mCursor.getCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (mCursor.moveToPosition(groupPosition)) {
			String text =
			  mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
			Long l =
			  mCursor.getLong(mCursor.getColumnIndex(DatabaseContract.MedReminderTableInfo.COLUMN_NAME_TIME));
		return new Row(text,l);
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater =
		  (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.template_header, null);
		TextView textView = (TextView) result.findViewById(R.id.header_text);
		textView.setText("Taken Medication");
		return result;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater =
		  (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.fragment_med_reminder_list_item, null);

		TextView textView = (TextView) result.findViewById(R.id.list_display_reminder);

		Row row = (Row) getChild(groupPosition, childPosition);
		MedicationStatement medicationStatement =
		  (MedicationStatement) FhirServices.getsFhirServices().toResource(row.name);
		CodeableConceptDt conceptDt = (CodeableConceptDt) medicationStatement.getMedication();

		textView.setText(conceptDt.getText());

		textView = (TextView) result.findViewById(R.id.list_display_date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(row.alarmTime);
		DateService dateService = new DateService();
		dateService.setFormat(DateService.FLAG_TIME_FORMAT);
		textView.setText(dateService.formatToString(calendar.getTime()));

		return result;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private class Row {
		String name;
		long alarmTime;

		private Row(String name, long alarmTime) {
			this.name = name;
			this.alarmTime = alarmTime;
		}

	}
}
