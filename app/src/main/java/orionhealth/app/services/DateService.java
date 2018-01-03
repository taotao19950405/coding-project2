package orionhealth.app.services;

import android.util.Log;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bill on 2/07/16.
 */
public class DateService {

	public static final int FLAG_SIMPLE_FORMAT = 0;
	public static final int FLAG_TIME_FORMAT = 1;

	private Format format;
	private final String simpleFormat = "EEE, MMM d, yyyy";
	private final String timeFormat = "h:mm a";

	public DateService() {
		setFormat(FLAG_SIMPLE_FORMAT);
	}

	public String formatToString(Date date) {
		if (date != null) {
			return format.format(date);
		}
		return "";
	}

	public Date parseDate(String dateString) {
		try {
			return (Date) format.parseObject(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			Log.d("ERROR", "errorInDate");
		}
		return null;
	}

	public void setFormat(int flag) {
		if (flag == FLAG_SIMPLE_FORMAT) {
			format = new SimpleDateFormat(simpleFormat);
		} else if (flag == FLAG_TIME_FORMAT) {
			format = new SimpleDateFormat(timeFormat);
		} else {
			format = new SimpleDateFormat(simpleFormat);
		}
	}
}
