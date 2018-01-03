package orionhealth.app.activities.adaptors;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import orionhealth.app.fhir.FhirServices;

import java.util.ArrayList;

/**
 * Created by bill on 27/09/16.
 */
public class AutoCompleteAdaptor extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> mData;
	private ArrayList<String> trial;

	public AutoCompleteAdaptor(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mData = new ArrayList<String>();
	}

	public ArrayList<String> trialGetList() {
		trial = new ArrayList<String>();
		trial.add("cool");
		trial.add("nice");
		trial.add("parcetamol");
		return trial;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public String getItem(int index) {
		return mData.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if(constraint != null) {
					// A class that queries a web API, parses the data and returns an ArrayList<Style>
//					StyleFetcher fetcher = new StyleFetcher();
					FhirServices fhirServices = FhirServices.getsFhirServices();
					try {
//						mData = fetcher.retrieveResults(constraint.toString());
						mData = fhirServices.searchMedication(constraint);
					}
					catch(Exception e) {
						Log.e("myException", e.getMessage());
					}
					// Now assign the values and count to the FilterResults object
					filterResults.values = mData;
					filterResults.count = mData.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint, Filter.FilterResults results) {
				if(results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}
		};
		return myFilter;
	}
}
