package orionhealth.app.activities.fragments.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.os.Bundle;

import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCriticalityEnum;
import ca.uhn.fhir.model.dstu2.valueset.SystemRestfulInteractionEnum;
import orionhealth.app.R;
import android.content.Context;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import orionhealth.app.activities.fragments.dialogFragments.RemoveAllergyDialogFragment;
import orionhealth.app.activities.main.MainActivity;
import orionhealth.app.data.dataModels.Criticality;
import orionhealth.app.data.dataModels.MyAllergyIntolerance;
import orionhealth.app.data.dataModels.MyAllergyIntolerance;
import orionhealth.app.data.medicationDatabase.AllergyTableOperations;
import orionhealth.app.fhir.FhirServices;

/**
 * Created by archanakhanal on 16/7/2016.
 */
public class AllergyDetailsFragment extends Fragment {

    private int aAllergyId;
    private AllergyIntolerance aAllergy;

    private Criticality[] criticalities;
    private EditText aNameTextField;
    private EditText aDetailsTextField;
    private EditText aReactionTextField;
    private Spinner aCriticalitySelector;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailsFragment = inflater.inflate(R.layout.fragment_allergy_details, container, false);

        aNameTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_substance_allergy);

        aReactionTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_reaction_allergy);

        criticalities = Criticality.values();
        aCriticalitySelector = (Spinner) detailsFragment.findViewById(R.id.criticality_spinner);
        setUpSelector(criticalities, aCriticalitySelector);

        aDetailsTextField = (EditText) detailsFragment.findViewById(R.id.edit_text_details_allergy);
        return detailsFragment;
    }

    public void populateFields() {
        if (aAllergy != null) {
            EditText nameEditTextFieldAllergy = (EditText) getActivity().findViewById(R.id.edit_text_substance_allergy);
            CodeableConceptDt codeableConceptAllergy = (CodeableConceptDt) aAllergy.getSubstance();
            nameEditTextFieldAllergy.setText(codeableConceptAllergy.getText());

            EditText detailsEditTextFieldAllergy = (EditText) getActivity().findViewById(R.id.edit_text_details_allergy);
            AnnotationDt annotationNotesAllergy = (AnnotationDt) aAllergy.getNote();
            detailsEditTextFieldAllergy.setText(annotationNotesAllergy.getText());

            EditText reactionEditTextFieldAllergy = (EditText) getActivity().findViewById(R.id.edit_text_reaction_allergy);
            List<AllergyIntolerance.Reaction> listReaction = aAllergy.getReaction();
            if (!listReaction.isEmpty()) {
                AllergyIntolerance.Reaction reaction = listReaction.get(0);
                CodeableConceptDt reactionCodeableConcept = reaction.getManifestation().get(0);
                reactionEditTextFieldAllergy.setText(reactionCodeableConcept.getText());
            }

            String criticality = aAllergy.getCriticality();
            Log.d("ASDF", ""+criticality);
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.criticality_spinner);
            if (aAllergy.getCriticality().toString().equals("CRITL")){
                criticality = "Low Risk";
            } else if (aAllergy.getCriticality().toString().equals("CRITU")){
                criticality = "Unknown";
            } else if (aAllergy.getCriticality().toString().equals("CRITH")){
                criticality = "High Risk";
            }

            if (!criticality.isEmpty()) {
                int index = 0;
                for (int i = 0; i < spinner.getCount(); i++) {
                    if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(criticality)) {
                        index = i;
                        break;
                    }
                }
                spinner.setSelection(index);
            }
        }
    }


    public void addAllergyToDatabase(Context context) throws Exception{
        String name = aNameTextField.getText().toString();
        String details = aDetailsTextField.getText().toString();
        String reaction = aReactionTextField.getText().toString();
        Criticality criticality = (Criticality) aCriticalitySelector.getSelectedItem();

        AllergyIntolerance allergyIntolerance;
        try{
            allergyIntolerance = createAllergyIntolerance(name, details, reaction, criticality.toString());
            System.out.println("Crit" + criticality.toString());
            int localID = AllergyTableOperations.getInstance().addToAllergyTable(context, allergyIntolerance);
            MyAllergyIntolerance myAllergyIntolerance = new MyAllergyIntolerance(localID, allergyIntolerance);
            FhirServices.getsFhirServices().sendAllergyToServer(myAllergyIntolerance, context);
        } catch (NoSubstanceException e) {
                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
				throw e;
		} catch (NoReactionException e) {
			Toast.makeText(context, "Please enter reaction", Toast.LENGTH_SHORT).show();
				throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void updateAllergyInDatabase(Context context) throws Exception{
        String name = aNameTextField.getText().toString();
        String details = aDetailsTextField.getText().toString();
        String reaction = aReactionTextField.getText().toString();
        Criticality criticality = (Criticality) aCriticalitySelector.getSelectedItem();

        try {
            aAllergy =
                    createAllergyIntolerance(name, details, reaction, criticality.toString());
            MyAllergyIntolerance myAllergyIntolerance = new MyAllergyIntolerance(aAllergyId, aAllergy);

            AllergyTableOperations.getInstance().updateAllergy(context, aAllergyId, aAllergy);
            FhirServices.getsFhirServices().updateAllergyServer(myAllergyIntolerance, context);

            Intent intentAllergy = new Intent(context, MainActivity.class);
            startActivity(intentAllergy);
        } catch (NoSubstanceException e){
            Toast.makeText(context, "Please enter a substance", Toast.LENGTH_SHORT).show();
			throw e;
		} catch (NoReactionException e) {
			Toast.makeText(context, "Please enter reaction", Toast.LENGTH_SHORT).show();
			throw e;
        } catch (Exception e) {
            e.printStackTrace();
			throw e;
        }
    }


    private AllergyIntolerance createAllergyIntolerance(String name, String details, String reaction, String criticality)
            throws Exception {

        checkValidAllergy(name, reaction);
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();

        //		set up ID from existed condition if available
        if(aAllergy != null) {
            allergyIntolerance.setId(aAllergy.getId());
            System.out.println();
        }

        allergyIntolerance.setSubstance(new CodeableConceptDt().setText(name));
        ResourceReferenceDt patientRef = new ResourceReferenceDt().setDisplay("LOCAL");
        allergyIntolerance.setPatient(patientRef);

        allergyIntolerance.setNote(new AnnotationDt().setText(details));

        CodeableConceptDt codeableConceptManifestation = new CodeableConceptDt();
        codeableConceptManifestation.setText(reaction);

        AllergyIntolerance.Reaction reactionFhir = new AllergyIntolerance.Reaction();
        List<CodeableConceptDt> listManifestation = new LinkedList<CodeableConceptDt>();
        listManifestation.add(codeableConceptManifestation);
        reactionFhir.setManifestation(listManifestation);

        List<AllergyIntolerance.Reaction> listReaction = new LinkedList<AllergyIntolerance.Reaction>();
        listReaction.add(reactionFhir);
        allergyIntolerance.setReaction(listReaction);

        if (criticality.toString().equals("Low Risk")){
            criticality = "CRITL";
        } else if (criticality.toString().equals("Unknown")){
            criticality = "CRITU";
        } else if (criticality.toString().equals("High Risk")) {
            criticality = "CRITH";
        }

        allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.forCode(criticality));

        return allergyIntolerance;
    }


    public void removeAllergy(){
        DialogFragment removeAllergyDialog = new RemoveAllergyDialogFragment();
        removeAllergyDialog.show(getFragmentManager(), "removeAllergy");
        //AllergyTableOperations.getInstance().removeAllergy(getContext(), aAllergyId);
    }

    public void onRemovePositiveClick(Context context) {
        AllergyTableOperations.getInstance().removeAllergy(context, aAllergyId);
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }


    public void setAllergy(Context context, int allergyLocalId){
        aAllergyId = allergyLocalId;
        aAllergy = AllergyTableOperations.getInstance().getAllergyIntolerance(context, allergyLocalId);
    }

    private void checkValidAllergy(String name, String reaction) throws Exception {
        if (name.equals("")){
            throw new NoSubstanceException();
        } else if (reaction.isEmpty()){
            throw  new NoReactionException();
        }

    }

    private void setUpSelector(Object[] cs, Spinner selector) {
        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, cs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(adapter);
    }



    private void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  // hide the soft keyboard
        }
    }

    private class hideKeyBoardTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyBoard(v);
            return false;
        }
    }


    private class NoSubstanceException extends Exception {

    }

    private class NoReactionException extends Exception{

    }
}
