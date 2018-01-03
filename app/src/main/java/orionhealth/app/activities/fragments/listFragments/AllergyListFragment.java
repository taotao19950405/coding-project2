package orionhealth.app.activities.fragments.listFragments;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import orionhealth.app.R;
import orionhealth.app.activities.adaptors.AllergyListAdapter;
import orionhealth.app.activities.main.AddAllergyActivity;
import orionhealth.app.activities.main.EditAllergyActivity;
import orionhealth.app.data.medicationDatabase.AllergyTableOperations;

/**
 * Created by archanakhanal on 8/7/2016.
 */
public class AllergyListFragment extends ListFragment {
    public final static String SELECTED_ALLERGY_ID = "allergyListFragment.SELECTED_ALLERGY_ID";
    private ListView aAllergyList;


    public AllergyListFragment() {
    }

    public static AllergyListFragment newInstance() {
        AllergyListFragment allergyFragment = new AllergyListFragment();
        return allergyFragment;
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_allergy_list, container, false);
        FloatingActionButton addButtonAllergy = (FloatingActionButton) view.findViewById(R.id.button_add_allergy_list);
        addButtonAllergy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAllergyActivity.class);
                startActivity(intent);
            }
        });
        return view;
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor = AllergyTableOperations.getInstance().getAllRows(getContext());

        aAllergyList = getListView();
        AllergyListAdapter allergylistAdapter = new AllergyListAdapter(getContext(), cursor);
        aAllergyList.setAdapter(allergylistAdapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getContext(), EditAllergyActivity.class);
        intent.putExtra(SELECTED_ALLERGY_ID, (int) id);
        startActivity(intent);
    }
}
