package orionhealth.app.broadCastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import orionhealth.app.R;
import orionhealth.app.activities.main.TakeMedicationActivity;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.services.MedResponseService;
import orionhealth.app.services.RingToneService;
import orionhealth.app.services.UpdateUIService;

/**
 * Created by bill on 4/07/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
	public static int FLAG_UPDATE = 1;
	public static String NOTIFICATION_ID_KEY = "notification_id";
	public static String BUNDLE_KEY = "medication";
	public static String PARCEL_KEY = "parcel";
	public static int UPDATE_FLAG = 1;

	// activity handle ringtone stop
	// intent send information about medication
	@Override
	public void onReceive(Context context, Intent intent) {
		int medId = intent.getIntExtra(AlarmSetter.REMINDER_ID_KEY, -1);
		long alarmTime = intent.getLongExtra(AlarmSetter.ALARM_TIME_KEY, -1);

		MedTableOperations.getInstance().
		  		changeMedReminderStatus(context, medId, MedUptakeStatus.OVERDUE.ordinal());

		Cursor cursor = MedTableOperations.getInstance().getMedReminder(context, medId);
		cursor.moveToFirst();
		String jsonString =
				cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));


		intent = new Intent(context, UpdateUIService.class);
		context.startService(intent);

		Intent startRingToneIntent = new Intent(context, RingToneService.class);
		context.startService(startRingToneIntent);

		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (!powerManager.isInteractive()) {
			Intent activityIntent = new Intent(context, TakeMedicationActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(activityIntent);
		} else {
		MedicationStatement medicationStatement =
		  (MedicationStatement) FhirServices.getsFhirServices().toResource(jsonString);

		CodeableConceptDt conceptDt = (CodeableConceptDt) medicationStatement.getMedication();
		String medName = conceptDt.getText();

		Intent takeMedicationIntent = new Intent(context, MedResponseService.class);
		takeMedicationIntent.putExtra(AlarmSetter.REMINDER_ID_KEY, medId);
		takeMedicationIntent.putExtra(AlarmSetter.ALARM_TIME_KEY, alarmTime);
		PendingIntent pendingIntentCancel = PendingIntent.getService(context, medId, takeMedicationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


		Notification notification = new NotificationCompat.Builder(context)
		  .setContentTitle("Take " + medName)
		  .setContentText("Medication Reminder")
		  .setSmallIcon(R.drawable.medicine)
		  .setPriority(Notification.PRIORITY_HIGH)
		  .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
		  .addAction(R.mipmap.ic_done_all_black_18dp, "take", pendingIntentCancel)
		  .addAction(R.mipmap.ic_clear_black_18dp, "dismiss", pendingIntentCancel)
		  .setDeleteIntent(pendingIntentCancel)
		  .setFullScreenIntent(pendingIntentCancel, true)
		  .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, TakeMedicationActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
		  .build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(medId, notification);

		}
	}

// int alarmIndex = intent.getIntExtra("here", -1);
//		Bundle bundle = intent.getBundleExtra(BUNDLE_KEY);
//		AlarmPackage alarmPackage = bundle.getParcelable(PARCEL_KEY);
//		notificationId = alarmPackage.getId();
//
//		if (alarmIndex == -2){
//			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//			PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			alarmManager.cancel(alarmPendingIntent);
//			return;
//		}
//
//		MedicationStatement medStatement =
//		  (MedicationStatement) FhirServices.getsFhirServices().toResource(alarmPackage.getJsonMedString());
//		CodeableConceptDt codeableConcept = (CodeableConceptDt) medStatement.getMedication();
//		this.title = codeableConcept.getText();
//
////		long[] alarmTimes = alarmPackage.getAlarmTimes();
//
//		if (alarmIndex >= 0) {
//			Intent startRingToneIntent = new Intent(context, RingToneService.class);
//			context.startService(startRingToneIntent);
//			createNotification(context);
//		}
//
//		alarmIndex ++;
//
//		if (3 != alarmIndex) {
//			intent.putExtra("here", alarmIndex);
//			PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//			Calendar calendar = Calendar.getInstance();
//			alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmPackage.getAlarmTime() + alarmIndex * alarmPackage.getIntervalTimeToNextAlarm(), alarmPendingIntent);
//		}



//	private void createNotification(Context context) {
//
//		Intent takeMedicationIntent = new Intent(context, MedResponseService.class);
//		takeMedicationIntent.putExtra(NOTIFICATION_ID_KEY, notificationId);
//		PendingIntent pendingIntentCancel = PendingIntent.getService(context, notificationId, takeMedicationIntent, 0);
//
//		int drawable;
//		if (icon == MedicationUnit.MG.ordinal()) {
//			drawable = R.drawable.two_color_pill;
//		} else if (icon == MedicationUnit.ML.ordinal()) {
//			drawable = R.drawable.medicine;
//		} else if (icon == MedicationUnit.SPRAY.ordinal()) {
//			drawable = R.drawable.spray_can;
//		} else if (icon == MedicationUnit.TABLET.ordinal()) {
//			drawable= R.drawable.pill;
//		} else {
//			drawable = R.drawable.warning;
//		}
//
//		Notification notification = new NotificationCompat.Builder(context)
//		  .setContentTitle("Take "+title)
//		  .setContentText("Medication Reminder")
//		  .setSmallIcon(drawable)
//		  .setPriority(Notification.PRIORITY_MAX)
//		  .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//		  .addAction(R.mipmap.ic_done_all_black_18dp, "take", pendingIntentCancel)
//		  .addAction(R.mipmap.ic_clear_black_18dp, "dismiss", pendingIntentCancel)
//		  .setDeleteIntent(pendingIntentCancel)
//		  .setFullScreenIntent(pendingIntentCancel, true)
////		  .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
//		  .build();
//	}
}
