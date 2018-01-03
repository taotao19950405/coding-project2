package orionhealth.app.data.dataModels;

/**
 * Created by archanakhanal on 2/8/2016.
 */
public enum Criticality {
    HIGH_RISK("High Risk"),
    LOW_RISK("Low Risk"),
    UNABLE_TO_DETERMINE("Unknown");


    private final String name;

    Criticality (String name) {
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

