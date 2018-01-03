package orionhealth.app.data.spinnerEnum;

/**
 * Created by bill on 4/08/16.
 */
public enum TimeIntervalUnit {
	MINUTE("minutes"),
	HOUR("hour(s)");

	private final String title;

	TimeIntervalUnit(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
