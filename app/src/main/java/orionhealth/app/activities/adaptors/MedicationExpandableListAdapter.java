package orionhealth.app.activities.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import orionhealth.app.R;
import orionhealth.app.data.dataModels.MyMedication;
import orionhealth.app.data.spinnerEnum.MedicationUnit;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.activities.external.AnimatedExpandableListView;

import java.util.List;

/**
 * Created by bill on 4/05/16.
 */
public class MedicationExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
	private Context mContext;
	private Cursor mCursor;

	public MedicationExpandableListAdapter(Context context) {
		this.mContext = context;
	}
	public MedicationExpandableListAdapter(Context context, Cursor cursor){
		this.mContext = context;
		this.mCursor = cursor;
	}

	public void OnEditButtonClick(int medicationLocalId){

	}


	@Override
	public int getGroupCount() {
		return mCursor.getCount();
	}


	@Override
	public Object getGroup(int groupPosition) {
		if (mCursor.moveToPosition(groupPosition)) {
			long localId = mCursor.getLong(mCursor.getColumnIndex(DatabaseContract.MedTableInfo._ID));
			String jsonMedString =
			  		mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
			MedicationStatement medStatement =
			  		(MedicationStatement)FhirServices.getsFhirServices().toResource(jsonMedString);
			Boolean reminderSet =
			  		mCursor.getInt(mCursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_REMINDER_SET)) != 0;
			return new MyMedication((int) localId, medStatement, reminderSet);
		}else {
			return null;
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, final boolean isExpanded, View convertView,
							 ViewGroup parent) {
		LayoutInflater inflater =
		  		(LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.fragment_medication_list_group, null);
		MyMedication myMedication = (MyMedication) getGroup(groupPosition);
		MedicationStatement medStatementFhir = myMedication.getFhirMedStatement();
		CodeableConceptDt codeableConcept = (CodeableConceptDt)medStatementFhir.getMedication();
		String name = codeableConcept.getText();
		TextView textView = (TextView) result.findViewById(R.id.list_display_name);
		textView.setText(name);

		textView = (TextView) result.findViewById(R.id.list_display_dosage);
		List<MedicationStatement.Dosage> listDosage = medStatementFhir.getDosage();
		MedicationStatement.Dosage dosage = listDosage.get(0);
		SimpleQuantityDt simpleQuantityDt = (SimpleQuantityDt) dosage.getQuantity();
		textView.setText(simpleQuantityDt.getValueElement().getValueAsInteger()+"");

		String unitIdString = simpleQuantityDt.getCode();
		textView = (TextView) result.findViewById(R.id.list_display_dosage_unit);
		ImageView imageView = (ImageView) result.findViewById(R.id.medication_icon);
		if (unitIdString != null) {
			int unitId = Integer.parseInt(unitIdString);
			MedicationUnit medicationUnit = MedicationUnit.values()[unitId];
			textView.setText(medicationUnit.getName());
			if (unitId == MedicationUnit.MG.ordinal()) {
				imageView.setImageResource(R.drawable.two_color_pill);
			} else if (unitId == MedicationUnit.ML.ordinal()) {
				imageView.setImageResource(R.drawable.medicine);
			} else if (unitId == MedicationUnit.SPRAY.ordinal()) {
				imageView.setImageResource(R.drawable.spray_can);
			} else if (unitId == MedicationUnit.TABLET.ordinal()) {
				imageView.setImageResource(R.drawable.pill);
			} else {
				imageView.setImageResource(R.drawable.warning);
			}

		} else {
			textView.setText(simpleQuantityDt.getUnit());
			imageView.setImageResource(R.drawable.warning);
		}

		final RelativeLayout alarmIndicator = (RelativeLayout) result.findViewById(R.id.alarm_indicator);
		imageView = (ImageView) alarmIndicator.findViewById(R.id.alarm_indicator_image);

		if (myMedication.getReminderSet()) {
			imageView.setVisibility(View.VISIBLE);
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}

		final RelativeLayout indicator = (RelativeLayout) result.findViewById(R.id.indicator);
		imageView = (ImageView) indicator.findViewById(R.id.indicator_image);

		if (isExpanded) {
			imageView.setImageResource(R.drawable.up_arrow);
		} else {
			imageView.setImageResource(R.drawable.arrow_down);
		}

		return result;
	}

	@Override
	public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild,
								 View convertView, ViewGroup parent) {
		LayoutInflater inflater =
		  		(LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.fragment_medication_list_child, null);
		final MyMedication myMedication = (MyMedication) getGroup(groupPosition);
		final MedicationStatement medicationStatement = myMedication.getFhirMedStatement();
		CodeableConceptDt codeableConceptDt = (CodeableConceptDt) medicationStatement.getReasonForUse();

		if (codeableConceptDt == null) {
			LinearLayout linearLayout = (LinearLayout) result.findViewById(R.id.display_reasonForUse);
			linearLayout.setVisibility(View.GONE);
		} else {
			TextView textView = (TextView) result.findViewById(R.id.list_reasonForUse);
			textView.setText(codeableConceptDt.getText());
		}

		if (medicationStatement.getNote() == null) {
			LinearLayout linearLayout = (LinearLayout) result.findViewById(R.id.display_Note);
			linearLayout.setVisibility(View.GONE);
		} else {
			TextView textView = (TextView) result.findViewById(R.id.list_Note);
			textView.setText(medicationStatement.getNote());
		}

		Button editButton = (Button) result.findViewById(R.id.button_edit);
		editButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				OnEditButtonClick(myMedication.getLocalId());
			}
		});

		return result;
	}

	@Override
	public int getRealChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		super.unregisterDataSetObserver(observer);
	}
}
