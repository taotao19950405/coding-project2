package orionhealth.app.data.dataModels;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;

/**
 * Created by archanakhanal on 20/7/2016.
 */
public class MyAllergyIntolerance {
    private int aLocalId;
    private AllergyIntolerance aFhirAllergyIntolerance;

    public MyAllergyIntolerance(int localId, AllergyIntolerance fhirAllergyIntolerance) {
        this.aLocalId = localId;
        this.aFhirAllergyIntolerance = fhirAllergyIntolerance;
    }

    @Override
    public boolean equals (Object obj) {
        return super.equals(obj);
    }

    public int getLocalId() {

        return aLocalId;
    }

    public AllergyIntolerance getFhirAllergyIntolerance() {
        return aFhirAllergyIntolerance;
    }

}
