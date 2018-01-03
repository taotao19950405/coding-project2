package orionhealth.app.broadCastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bill on 20/08/16.
 */
public class AlarmSetter extends BroadcastReceiver {
	public static final String REMINDER_ID_KEY = "med_id_key";
	public static final String JSON_STRING_KEY = "json_string_key";
	public static final String ALARM_TIME_KEY = "alarm_time_key";
	public static final String REMINDER_SET_KEY = "reminder_set_key";

	@Override
	public void onReceive(Context context, Intent intent) {
		Boolean isReminderSet = intent.getBooleanExtra(REMINDER_SET_KEY, false);
		int medId = intent.getIntExtra(REMINDER_ID_KEY, -1);
		long alarmTime = intent.getLongExtra(ALARM_TIME_KEY, -1);
		if (isReminderSet) {
			intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra(REMINDER_ID_KEY, medId);
			intent.putExtra(ALARM_TIME_KEY, alarmTime);
			PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, medId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmPendingIntent);
			return;
		} else {
			intent = new Intent(context, AlarmReceiver.class);
			PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, medId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(alarmPendingIntent);
		}

	}
}
