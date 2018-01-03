package orionhealth.app.activities.fragments.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import orionhealth.app.R;

/**
 * Created by bill on 25/04/16.
 */
public class UnderConstructionFragment extends Fragment {

	public UnderConstructionFragment() {
	}

	public static UnderConstructionFragment newInstance() {
		UnderConstructionFragment fragment = new UnderConstructionFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_construction, container, false);
		return rootView;
	}
}
