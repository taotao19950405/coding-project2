package orionhealth.app.activities.adaptors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import orionhealth.app.R;
import orionhealth.app.activities.main.MainActivity;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.services.DateService;
import orionhealth.app.services.RingToneService;

import java.util.Calendar;

/**
 * Created by bill on 19/09/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

	private Context context;
	private Cursor cursor;
	private int numOfElements;

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView title, time;
		public NumberPicker dosagePicker;
		public Button takeButton;

		public ViewHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.med_title_text_view);
			time = (TextView) view.findViewById(R.id.med_time_text_view);
			dosagePicker = (NumberPicker) view.findViewById(R.id.reminder_number_picker);
			takeButton = (Button) view.findViewById((R.id.button_take));
		}
	}

	public MyAdapter(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = cursor;
		this.numOfElements = cursor.getCount();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
		  .inflate(R.layout.card_med_reminder, parent, false);

		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		if (cursor.moveToPosition(cursor.getCount() - 1 - position)) {
			final long remId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.MedReminderTableInfo._ID));
			String text =
			  cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
			Long l =
			  cursor.getLong(cursor.getColumnIndex(DatabaseContract.MedReminderTableInfo.COLUMN_NAME_TIME));

			MedicationStatement medicationStatement =
			  (MedicationStatement) FhirServices.getsFhirServices().toResource(text);
			CodeableConceptDt conceptDt = (CodeableConceptDt) medicationStatement.getMedication();

			holder.title.setText(conceptDt.getText());

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(l);
			DateService dateService = new DateService();
			dateService.setFormat(DateService.FLAG_TIME_FORMAT);
			holder.time.setText(dateService.formatToString(calendar.getTime()));

			Button takeButton = holder.takeButton;

			takeButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					setCursor();
					onTakeButtonClick(remId, position);
				}
			});

			holder.dosagePicker.setMinValue(0);
			holder.dosagePicker.setMaxValue(10);
		}
	}

	public void onTakeButtonClick(long remId, int position) {

	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}

	public void setCursor() {
		cursor =
		  MedTableOperations.getInstance().
			getMedRemindersForStatus(context, MedUptakeStatus.OVERDUE.ordinal());
	}
}
