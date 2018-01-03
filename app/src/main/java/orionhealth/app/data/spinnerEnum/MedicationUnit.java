package orionhealth.app.data.spinnerEnum;

/**
 * Created by bill on 6/06/16.
 */
public enum MedicationUnit {
	MG("mg"),
	ML("ml"),
	SPRAY("spray(s)"),
	TABLET("tablet(s)");

	private final String name;

	MedicationUnit(String name) {
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
