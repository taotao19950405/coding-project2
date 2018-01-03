package orionhealth.app.activities.adaptors;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import orionhealth.app.R;
import orionhealth.app.data.dataModels.Criticality;
import orionhealth.app.data.dataModels.MyAllergyIntolerance;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.fhir.FhirServices;

/**
 * Created by archanakhanal on 18/7/2016.
 */
public class AllergyListAdapter extends BaseAdapter {
    private Context aContext;
    private Cursor aCursor;

    public AllergyListAdapter(Context context, Cursor cursor){

        this.aContext = context;
        this.aCursor = cursor;
    }

    @Override
    public int getCount() {
        return aCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
       if (aCursor.moveToPosition(position)) {
           long localID = aCursor.getLong(aCursor.getColumnIndex(DatabaseContract.AllergyTableInfo._ID));
           String jsonAllergyString = aCursor.getString(aCursor.getColumnIndex(DatabaseContract.AllergyTableInfo.COLUMN_NAME_JSON_STRING));

           AllergyIntolerance allergyIntolerance = (AllergyIntolerance) FhirServices.getsFhirServices().toResource(jsonAllergyString);
           return new MyAllergyIntolerance( (int) localID, allergyIntolerance);
       }
        else {
           return null;
       }
    }

    @Override
    public long getItemId(int position) {
        if (aCursor.moveToPosition(position)) {
            long localID = aCursor.getLong(aCursor.getColumnIndex(DatabaseContract.AllergyTableInfo._ID));
            return localID;
        } else {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_allergy_list_item, null);
        MyAllergyIntolerance aAllergyIntolerance = (MyAllergyIntolerance) getItem(position);
        AllergyIntolerance allergyIntoleranceFhir = aAllergyIntolerance.getFhirAllergyIntolerance();

        CodeableConceptDt codeableConceptSubstance = (CodeableConceptDt) allergyIntoleranceFhir.getSubstance();
        String substanceText = codeableConceptSubstance.getText();
        TextView displayAllergyName = (TextView) view.findViewById(R.id.list_display_substance_allergy);
        displayAllergyName.setText(substanceText);

        String detailsText = allergyIntoleranceFhir.getNote().getText();
        TextView displayAllergyDetails = (TextView) view.findViewById(R.id.list_display_details_allergy);
        displayAllergyDetails.setText(detailsText);

        ArrayList<AllergyIntolerance.Reaction> listReaction = (ArrayList<AllergyIntolerance.Reaction>) allergyIntoleranceFhir.getReaction();
        CodeableConceptDt manifestationCodeableConcept = listReaction.get(0).getManifestation().get(0);
        String reaction = manifestationCodeableConcept.getText();
        TextView displayAllergyReaction = (TextView) view.findViewById(R.id.list_display_reaction_allergy);
        displayAllergyReaction.setText(reaction);

        if (allergyIntoleranceFhir.getCriticality().toString().equals("CRITL")) {
            view.setBackgroundColor(Color.rgb(255, 220, 193));
        } else if (allergyIntoleranceFhir.getCriticality().toString().equals("CRITU")) {
            view.setBackgroundColor(Color.rgb(255, 236, 222));
        } else if (allergyIntoleranceFhir.getCriticality().toString().equals("CRITH")) {
            view.setBackgroundColor(Color.rgb(255, 202, 161));
        }
        return view;
    }
}

