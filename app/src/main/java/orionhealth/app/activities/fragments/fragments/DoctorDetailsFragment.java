package orionhealth.app.activities.fragments.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.valueset.MedicationStatementStatusEnum;
import orionhealth.app.R;
import orionhealth.app.activities.main.AddConditionActivity;
import orionhealth.app.activities.main.MainActivity;
import orionhealth.app.data.dataModels.MyMedication;
import orionhealth.app.data.spinnerEnum.MedicationUnit;
import orionhealth.app.fhir.FhirServices;

/**
 * Created by bill on 25/04/16.
 */
public class DoctorDetailsFragment extends Fragment {


	private EditText mNameTextField;
	private EditText mDosageTextField;
	private Spinner mDosageUnitSelector;
	private EditText mReasonTextField;

	public DoctorDetailsFragment() {
	}

	public static DoctorDetailsFragment newInstance() {
		DoctorDetailsFragment fragment = new DoctorDetailsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View detailsFragment = inflater.inflate(R.layout.fragment_doctor_details, container, false);

		mNameTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_name);
		mDosageTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_dosage);

		mDosageUnitSelector = (Spinner) detailsFragment.findViewById(R.id.unit_spinner);
		setUpUnitSelector();

		mReasonTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_reasonForUse);

		Button sendButton = (Button) detailsFragment.findViewById(R.id.button_send);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					addMedicationToDatabase(getContext());
					Intent intent = new Intent(getActivity(), MainActivity.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return detailsFragment;
	}

	public void addMedicationToDatabase(Context context) throws Exception {
		//Do something in response to clicking add button
		try {
			MedicationStatement medStatement = createMedStatement();

			FhirServices.getsFhirServices().doctorSendMedicationToServer(medStatement, context);

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

	private MedicationStatement createMedStatement() throws Exception {

		String name = mNameTextField.getText().toString();
		String dosage = mDosageTextField.getText().toString();
		MedicationUnit medicationUnit = (MedicationUnit) mDosageUnitSelector.getSelectedItem();
		String reasonForUse = mReasonTextField.getText().toString();

		checkValidMedication(name, dosage);
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		Long dosageLong = Long.parseLong(dosage);
		MedicationStatement medicationStatement = new MedicationStatement();


		medicationStatement.setMedication(new CodeableConceptDt().setText(name));
		medicationStatement.setStatus(MedicationStatementStatusEnum.ACTIVE);

		ResourceReferenceDt patientRef = new ResourceReferenceDt().setDisplay("LOCAL");
		medicationStatement.setPatient(patientRef);

		medicationStatement.setReasonForUse(new CodeableConceptDt().setText(reasonForUse));

		MedicationStatement.Dosage dosageFhir = new MedicationStatement.Dosage();
		SimpleQuantityDt simpleQuantityDt = new SimpleQuantityDt(dosageLong);
		simpleQuantityDt.setUnit(medicationUnit.toString());
		simpleQuantityDt.setCode(medicationUnit.ordinal()+"");
		dosageFhir.setQuantity(simpleQuantityDt);

		List<MedicationStatement.Dosage> listDosage = new LinkedList<MedicationStatement.Dosage>();
		listDosage.add(dosageFhir);
		medicationStatement.setDosage(listDosage);

		return medicationStatement;
	}

	private void checkValidMedication(String name, String dosage) throws Exception {
		if (name.equals("")) {
			throw new NoNameException();
		} else if (dosage.equals("")) {
			throw new NoDosageException();
		}
		Long.parseLong(dosage);
	}

	private void clearForm(ViewGroup group)
	{
		for (int i = 0, count = group.getChildCount(); i < count; ++i) {
			View view = group.getChildAt(i);
			if (view instanceof EditText) {
				((EditText)view).setText("");
			}

			if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
				clearForm((ViewGroup)view);
		}
	}

	private void setUpUnitSelector() {
		MedicationUnit[] medicationUnits;
		medicationUnits = MedicationUnit.values();
		ArrayAdapter<CharSequence> adapter =
				new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, medicationUnits);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDosageUnitSelector.setAdapter(adapter);
	}

// --------------------------   Exception classes ---------------------------------------------- //

	private class NoNameException extends Exception {

	}

	private class NoDosageException extends Exception {

	}

}
