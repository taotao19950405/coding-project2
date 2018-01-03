package orionhealth.app.data.spinnerEnum;

/**
 * Created by bill on 14/09/16.
 */
public enum MedUptakeStatus {
	OVERDUE("overdue"),
	PENDING("pending"),
	TAKEN("taken");

	private final String name;

	MedUptakeStatus(String name) {
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
