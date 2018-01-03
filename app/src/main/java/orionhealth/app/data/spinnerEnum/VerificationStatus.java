package orionhealth.app.data.spinnerEnum;

/**
 * Created by luchen on 16/07/2016.
 */
public enum VerificationStatus {
    PROVISIONAL("Provisional"),
    DIFFERENTIAL("Differential"),
    CONFIRMED("Confirmed"),
    REFUTED("Refuted"),
    ENTERED_IN_ERROR("Entered-in-error"),
    UNKNOWN("Unknown");

    private final String name;

    VerificationStatus (String name) {
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
