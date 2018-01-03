package orionhealth.app.activities.fragments.fragments;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.valueset.MedicationStatementStatusEnum;

import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import orionhealth.app.R;
import orionhealth.app.activities.adaptors.AutoCompleteAdaptor;
import orionhealth.app.activities.fragments.dialogFragments.DatePicker;
import orionhealth.app.activities.fragments.dialogFragments.RemoveMedicationDialogFragment;
import orionhealth.app.data.dataModels.AlarmPackage;
import orionhealth.app.data.dataModels.MyMedication;
import orionhealth.app.data.spinnerEnum.MedicationUnit;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.TimeIntervalUnit;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.broadCastReceivers.AlarmSetter;
import orionhealth.app.services.DateService;

/**
 * Created by bill on 25/04/16.
 */
public class MedicationDetailsFragment extends Fragment {

	private int mMedicationID;
	private MyMedication mMyMedication;

	private AutoCompleteTextView mNameTextField;
	private EditText mDosageTextField;
	private Spinner mDosageUnitSelector;
	private EditText mReasonTextField;

	private ToggleButton mOngoingToggleButton;
	private EditText mStartDateTextField;
	private LinearLayout mEndDateLinearLayout;
	private EditText mEndDateTextFeild;
	private EditText mNotesTextField;

	private LinearLayout mAlarmSetterLinearLayout;
	private TimePicker mTimePicker;
	private Spinner mTimeIntervalUnitSelector;
	private NumberPicker mTimeIntervalValueSelector;
	private NumberPicker mFrequencySelector;
	private Switch mReminderSwitch;

	private Calendar calendar;
	private Boolean mReminderSwitchState = false;
	private DateService dateService;
	private AlarmManager alarmManager;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailsFragment = inflater.inflate(R.layout.fragment_medication_details, container, false);

		mNameTextField = (AutoCompleteTextView) detailsFragment.findViewById(R.id.edit_text_name);
		AutoCompleteAdaptor adapter = new AutoCompleteAdaptor(getActivity(), android.R.layout.simple_dropdown_item_1line);
		mNameTextField.setAdapter(adapter);

		mDosageTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_dosage);

        mDosageUnitSelector = (Spinner) detailsFragment.findViewById(R.id.unit_spinner);
		setUpUnitSelector();

		mReasonTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_reasonForUse);

		mOngoingToggleButton = (ToggleButton) detailsFragment.findViewById(R.id.ongoing_toggle_button);
		setUpToggleButton();
		mStartDateTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_effectiveStart);
		mEndDateLinearLayout = (LinearLayout) detailsFragment.findViewById(R.id.linear_layout_effectiveEnd);
		mEndDateTextFeild = (EditText) detailsFragment.findViewById(R.id.edit_text_effectiveEnd);
		setUpDateEditTextFields();

		mReminderSwitch = (Switch) detailsFragment.findViewById(R.id.switch_reminder);
		setUpReminderSwitch();

		mNotesTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_notes);

		mAlarmSetterLinearLayout = (LinearLayout) detailsFragment.findViewById(R.id.alarm_setter_linear_layout);
		mAlarmSetterLinearLayout.setVisibility(View.GONE);
		mTimePicker = (TimePicker) mAlarmSetterLinearLayout.findViewById(R.id.time_picker);

		mTimeIntervalValueSelector = (NumberPicker) mAlarmSetterLinearLayout.findViewById(R.id.time_interval_value_spinner);
		String[] array = getResources().getStringArray(R.array.minute_interval_array);
		mTimeIntervalValueSelector.setDisplayedValues(array);
		mTimeIntervalValueSelector.setMinValue(1);
		mTimeIntervalValueSelector.setMaxValue(12);

		mFrequencySelector = (NumberPicker) mAlarmSetterLinearLayout.findViewById(R.id.daily_frequency_value_spinner);
		mFrequencySelector.setMinValue(1);
		mFrequencySelector.setMaxValue(5);
		mFrequencySelector.setWrapSelectorWheel(false);

		mTimeIntervalUnitSelector = (Spinner) mAlarmSetterLinearLayout.findViewById(R.id.time_interval_unit_spinner);
		setUpTimeIntervalUnitSelector();

		dateService = new DateService();

        return detailsFragment;
    }


    public void populateFields() {
		if (mMyMedication != null) {
			MedicationStatement medStatement = mMyMedication.getFhirMedStatement();
			EditText nameEditTextField = (EditText) getActivity().findViewById(R.id.edit_text_name);
			CodeableConceptDt codeableConcept = (CodeableConceptDt) medStatement.getMedication();
			nameEditTextField.setText(codeableConcept.getText());

			EditText dosageEditTextField = (EditText) getActivity().findViewById(R.id.edit_text_dosage);
			List<MedicationStatement.Dosage> listDosage = medStatement.getDosage();
			MedicationStatement.Dosage dosage = listDosage.get(0);
			SimpleQuantityDt simpleQuantityDt = (SimpleQuantityDt) dosage.getQuantity();
			dosageEditTextField.setText(simpleQuantityDt.getValueElement().getValueAsInteger() + "");

			TimingDt timingDt = dosage.getTiming();
			if (!timingDt.getEvent().isEmpty() && mMyMedication.getReminderSet()) {
				DateTimeDt dateTimeDt = timingDt.getEvent().get(0);
				TimingDt.Repeat repeat = timingDt.getRepeat();
				if (Build.VERSION.SDK_INT >= 23) {
					mTimePicker.setHour(dateTimeDt.getHour());
					mTimePicker.setMinute(dateTimeDt.getMinute());
				} else {
					mTimePicker.setCurrentHour(dateTimeDt.getHour());
					mTimePicker.setCurrentMinute(dateTimeDt.getMinute());
				}
				mFrequencySelector.setValue(repeat.getFrequency());
				mTimeIntervalValueSelector.setValue(repeat.getPeriod().intValue() / 5);
			}

			String unitIdString = simpleQuantityDt.getCode();
			Spinner spinner = (Spinner) getActivity().findViewById(R.id.unit_spinner);
			if (unitIdString == null) {
				String myString = simpleQuantityDt.getUnit();
				int index = 0;
				for (int i = 0; i < spinner.getCount(); i++) {
					if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
						index = i;
						break;
					}
				}
				spinner.setSelection(index);
			} else {
				int unitId = Integer.parseInt(unitIdString);
				spinner.setSelection(unitId);
			}
			EditText reasonForUseEditTextField =
			  (EditText) getActivity().findViewById(R.id.edit_text_reasonForUse);
			codeableConcept = (CodeableConceptDt) medStatement.getReasonForUse();

			if (codeableConcept != null) {
				reasonForUseEditTextField.setText(codeableConcept.getText());
			}

			PeriodDt p = (PeriodDt) medStatement.getEffective();

			if (p != null) {
				Date d = p.getStart();
				String dateString = dateService.formatToString(d);
				mStartDateTextField.setText(dateString);

				d = p.getEnd();
				dateString = dateService.formatToString(d);
				mEndDateTextFeild.setText(dateString);
			}

			mReminderSwitch.setChecked(mMyMedication.getReminderSet());

			EditText instructionsEditTextField = (EditText) getActivity().findViewById(R.id.edit_text_notes);
			instructionsEditTextField.setText(medStatement.getNote());

		}

    }

	public void addMedicationToDatabase(Context context) throws Exception {
		//Do something in response to clicking add button
		try {
			MedicationStatement medStatement = createMedStatement();
			MyMedication myMedication = new MyMedication();
			myMedication.setFhirMedStatement(medStatement);
			myMedication.setReminderSet(mReminderSwitchState);
			AlarmPackage alarmPackage = createAlarmPackage();
			myMedication.setAlarmPackage(alarmPackage);
			mMedicationID = MedTableOperations.getInstance().addToMedTable(context, myMedication);
			myMedication.setLocalId(mMedicationID);
			FhirServices.getsFhirServices().sendMedicationToServer(myMedication, context);

		} catch (NoNameException e) {
			Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (NoDosageException e) {
			Toast.makeText(context, "Please enter a dosage", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (NumberFormatException e) {
			Toast.makeText(context, "Please enter a valid dosage", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (Exception e){
			Toast.makeText(context, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			throw e;
		}
	}

	public void updateMedicationInDatabase(Context context) throws Exception{
		try {
			MedicationStatement medStatement = createMedStatement();
			mMyMedication.setFhirMedStatement(medStatement);
			mMyMedication.setReminderSet(mReminderSwitchState);
			AlarmPackage alarmPackage = createAlarmPackage();
			mMyMedication.setAlarmPackage(alarmPackage);
			MedTableOperations.getInstance().updateMedication(context, mMedicationID, mMyMedication);
			FhirServices.getsFhirServices().updateMedicationServer(mMyMedication, context);
			mMyMedication.setLocalId(mMedicationID);
		} catch (NoNameException e){
			Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (NoDosageException e){
			Toast.makeText(context, "Please enter a dosage", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (NumberFormatException e){
			Toast.makeText(context, "Please enter a valid dosage", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	private MedicationStatement createMedStatement() throws Exception {
		setCalendar();
		String name = mNameTextField.getText().toString();
		String dosage = mDosageTextField.getText().toString();
		MedicationUnit medicationUnit = (MedicationUnit) mDosageUnitSelector.getSelectedItem();
		String reasonForUse = mReasonTextField.getText().toString();
		String startDate = mStartDateTextField.getText().toString();
		String endDate = mEndDateTextFeild.getText().toString();
		String note = mNotesTextField.getText().toString();

		checkValidMedication(name, dosage);
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		Long dosageLong = Long.parseLong(dosage);
		MedicationStatement medicationStatement = new MedicationStatement();

//		set up ID from existed medication statement if available
		if(mMyMedication != null) {
			medicationStatement.setId(mMyMedication.getFhirMedStatement().getId());
			System.out.println();
		}

		medicationStatement.setMedication(new CodeableConceptDt().setText(name));
		medicationStatement.setStatus(MedicationStatementStatusEnum.ACTIVE);
		ResourceReferenceDt patientRef = new ResourceReferenceDt().setDisplay("LOCAL");
		medicationStatement.setPatient(patientRef);
		medicationStatement.setReasonForUse(new CodeableConceptDt().setText(reasonForUse));
		Date d = dateService.parseDate(startDate);
		PeriodDt period = new PeriodDt();
		period.setStart(d, TemporalPrecisionEnum.DAY );
		d = dateService.parseDate(endDate);
		period.setEnd(d, TemporalPrecisionEnum.DAY);
		medicationStatement.setEffective(period);

		medicationStatement.setNote(note);
		MedicationStatement.Dosage dosageFhir = new MedicationStatement.Dosage();
		SimpleQuantityDt simpleQuantityDt = new SimpleQuantityDt(dosageLong);
		simpleQuantityDt.setUnit(medicationUnit.toString());
		simpleQuantityDt.setCode(medicationUnit.ordinal()+"");
		dosageFhir.setQuantity(simpleQuantityDt);


		TimingDt timingDt = new TimingDt();
		timingDt.addEvent(calendar.getTime());
		TimingDt.Repeat repeat = new TimingDt.Repeat();
		repeat.setFrequency(mFrequencySelector.getValue());
		repeat.setPeriodUnits(UnitsOfTimeEnum.MIN);
		repeat.setPeriod(mTimeIntervalValueSelector.getValue()*5);
		timingDt.setRepeat(repeat);

		dosageFhir.setTiming(timingDt);
		List<MedicationStatement.Dosage> listDosage = new LinkedList<MedicationStatement.Dosage>();
		listDosage.add(dosageFhir);
		medicationStatement.setDosage(listDosage);
		return medicationStatement;
	}

	public AlarmPackage createAlarmPackage() {
		if (mReminderSwitchState) {
			AlarmPackage alarmPackage = new AlarmPackage();
			alarmPackage.setAlarmTime(calendar.getTimeInMillis());
			alarmPackage.setTimeIntervalToNextAlarm(mTimeIntervalValueSelector.getValue() * 5 * 60 * 1000);
			alarmPackage.setDailyNumOfAlarmsTime(mFrequencySelector.getValue());
			return alarmPackage;
		} else {
			return null;
		}
	}

	public void removeMedication(){
		DialogFragment removeMedDialogue = new RemoveMedicationDialogFragment();
		removeMedDialogue.show(getFragmentManager(), "removeMedication");
	}

	public void onRemovePositiveClick(Context context) {
		FhirServices.getsFhirServices().inactiveMedication(mMyMedication.getFhirMedStatement(), context);
		MedTableOperations.getInstance().removeMedReminder(context, mMedicationID);
		MedTableOperations.getInstance().removeMedication(context, mMedicationID);
	}

	private void checkValidMedication(String name, String dosage) throws Exception {
		if (name.equals("")) {
			throw new NoNameException();
		} else if (dosage.equals("")) {
			throw new NoDosageException();
		}
		Long.parseLong(dosage);
	}

	public void onSetDate(int year, int monthOfYear, int dayOfMonth, String tag){
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		Date d = c.getTime();
		String dateString = dateService.formatToString(d);
		if (tag.equals(mStartDateTextField.getId()+"")) {
			mStartDateTextField.setText(dateString);
		} else {
			mEndDateTextFeild.setText(dateString);
		}
	}

	public void onCancelDate() {
		mNotesTextField.requestFocus();
	}

	public void setUpReminder(Context context) {
		Intent alarmIntent = new Intent(context, AlarmSetter.class);
		alarmIntent.putExtra(AlarmSetter.REMINDER_ID_KEY, mMedicationID);
		alarmIntent.putExtra(AlarmSetter.REMINDER_SET_KEY, mReminderSwitchState);
		context.sendBroadcast(alarmIntent);
	}


	public void setMedication(Context context, int medLocalId){
		mMedicationID = medLocalId;
		mMyMedication = MedTableOperations.getInstance().getMedicationStatement(context, medLocalId);
	}

	public void setCalendar() {
		if (calendar == null){
			calendar = Calendar.getInstance();
		}

		if (Build.VERSION.SDK_INT >= 23) {
			calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
			calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
			calendar.set(Calendar.SECOND, 0);
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
			calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
			calendar.set(Calendar.SECOND, 0);
		}
	}


	private void setUpReminderSwitch() {
		mReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mReminderSwitchState = isChecked;
					mAlarmSetterLinearLayout.setVisibility(View.VISIBLE);
				} else {
					mReminderSwitchState = isChecked;
					mAlarmSetterLinearLayout.setVisibility(View.GONE);
				}
			}
		});
	}

	private void setUpToggleButton() {
		mOngoingToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mEndDateLinearLayout.setVisibility(View.GONE);
				} else {
					mEndDateLinearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void setUpDateEditTextFields() {
		mStartDateTextField.setOnFocusChangeListener(new showDatePickerFocusChangeListener());
		mStartDateTextField.setOnClickListener(new showDatePickerClickListener());
		mStartDateTextField.setShowSoftInputOnFocus(false);
		mStartDateTextField.setOnTouchListener(new hideKeyBoardTouchListener());

		mEndDateTextFeild.setOnFocusChangeListener(new showDatePickerFocusChangeListener());
		mEndDateTextFeild.setOnClickListener(new showDatePickerClickListener());
		mEndDateTextFeild.setShowSoftInputOnFocus(false);
		mEndDateTextFeild.setOnTouchListener(new hideKeyBoardTouchListener());
	}

	private void setUpUnitSelector() {
		MedicationUnit[] medicationUnits;
		medicationUnits = MedicationUnit.values();
		ArrayAdapter<CharSequence> adapter =
		  new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, medicationUnits);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDosageUnitSelector.setAdapter(adapter);
	}

	private void setUpTimeIntervalUnitSelector() {
		TimeIntervalUnit[] timeIntervalUnits;
		timeIntervalUnits = TimeIntervalUnit.values();
		ArrayAdapter<CharSequence> adapter =
		  new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, timeIntervalUnits);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTimeIntervalUnitSelector.setAdapter(adapter);
	}


	private void hideKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  // hide the soft keyboard
		}
	}

// ------------------------------	Listener classes   --------------------------------------------------//

	private class hideKeyBoardTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			hideKeyBoard(v);
			return false;
		}
	}

	private class showDatePickerFocusChangeListener implements View.OnFocusChangeListener {

		public showDatePickerFocusChangeListener() {
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				DatePicker datePicker = new DatePicker();
				EditText editText = (EditText) v;
				Date date = dateService.parseDate(editText.getText().toString());
				if (date != null) {
					final Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					datePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				} else {
					datePicker.setDate();
				}
				datePicker.show(getFragmentManager(), v.getId()+"");
			}
		}
	}

	private class showDatePickerClickListener implements View.OnClickListener {

		public showDatePickerClickListener() {
		}

		@Override
		public void onClick(View v) {
			DatePicker datePicker = new DatePicker();
			EditText editText = (EditText) v;
			Date date = dateService.parseDate(editText.getText().toString());
			if (date != null) {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				datePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			} else {
				datePicker.setDate();
			}
			datePicker.show(getFragmentManager(), v.getId()+"");
		}
	}

// --------------------------   Exception classes ---------------------------------------------- //

	private class NoNameException extends Exception {

	}

	private class NoDosageException extends Exception {

	}

}


