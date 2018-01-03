package orionhealth.app.data.spinnerEnum;

/**
 * Created by luchen on 17/07/2016.
 */
public enum Category {
    COMPLAINT("Complaint"),
    SYMPTOM("Symptom"),
    FINDING("Finding"),
    DIAGNOSIS("Diagnosis");

    private final String name;

    Category (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString(){
        return getName();
    }
}
