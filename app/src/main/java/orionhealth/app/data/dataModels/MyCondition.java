//       Description: Instance of database
//		 @author:  Bill
package orionhealth.app.data.dataModels;

import ca.uhn.fhir.model.dstu2.resource.Condition;

/**
 * Created by Lu on 14/07/16.
 */
public class MyCondition {
	private int mLocalId;
	private Condition mFhirCondition;

	public MyCondition(int localId, Condition fhirCondition) {
		this.mLocalId = localId;
		this.mFhirCondition = fhirCondition;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	};

	public int getLocalId(){
		return mLocalId;
	}

	public Condition getFhirCondition() {
		return mFhirCondition;
	}
}
