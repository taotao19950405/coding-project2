package orionhealth.app.data.spinnerEnum;

/**
 * Created by luchen on 17/07/2016.
 */
public enum Severity {

    Minor("Minor"),
    Moderate("Moderate"),
    Major("Major"),
    Extreme("Extreme");

    private final String name;

    Severity (String name) {
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
