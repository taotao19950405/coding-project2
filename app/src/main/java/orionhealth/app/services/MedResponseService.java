package orionhealth.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import orionhealth.app.broadCastReceivers.AlarmSetter;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;

/**
 * Created by bill on 11/07/16.
 */
public class MedResponseService extends IntentService {

	public MedResponseService() {
		super("MED_RESPONSE_SERVICE");
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */

	public MedResponseService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int notificationId = intent.getIntExtra(AlarmSetter.REMINDER_ID_KEY, -1);
		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
		Intent service = new Intent(getApplicationContext(), RingToneService.class);
		getApplicationContext().stopService(service);
		WakeLockService.release();

		long time = intent.getLongExtra(AlarmSetter.ALARM_TIME_KEY, -1);
		MedTableOperations.getInstance().changeMedReminderStatus(getApplicationContext(), notificationId, MedUptakeStatus.TAKEN.ordinal());

		intent = new Intent(this, UpdateUIService.class);
		startService(intent);
	}
}
