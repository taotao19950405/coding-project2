//       Description:
// 			Store data in table
// 			Fetch data from readable table in the format of Sqlite database instance
//			Query the datababse
//		 @author:  Bill
//			@Reviewer: 19 Apr 2016
package orionhealth.app.data.medicationDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import orionhealth.app.broadCastReceivers.AlarmSetter;
import orionhealth.app.data.dataModels.AlarmPackage;
import orionhealth.app.data.dataModels.MyMedication;
import orionhealth.app.data.spinnerEnum.MedUptakeStatus;
import orionhealth.app.fhir.FhirServices;
import orionhealth.app.data.medicationDatabase.DatabaseContract.MedTableInfo;
import orionhealth.app.data.medicationDatabase.DatabaseContract.MedReminderTableInfo;

import java.util.Calendar;

/**
 * Created by bill on 11/04/16.
 */
public final class MedTableOperations {
	private static MedTableOperations sMedTableOperations;
	private FhirServices mFhirServices;

	private MedTableOperations(){
		mFhirServices = FhirServices.getsFhirServices();
	}

	public static MedTableOperations getInstance(){
		if (sMedTableOperations == null){
			sMedTableOperations = new MedTableOperations();
			return sMedTableOperations;
		}else{
			return sMedTableOperations;
		}
	}

	public int addToMedTable(Context context, MyMedication myMedStatement) {
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase database = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();

		String jsonStringMed =
		  		FhirServices.getsFhirServices().toJsonString(myMedStatement.getFhirMedStatement());
		cv.put(MedTableInfo.COLUMN_NAME_JSON_STRING, jsonStringMed);

		Boolean reminderSet = myMedStatement.getReminderSet();
		cv.put(MedTableInfo.COLUMN_NAME_REMINDER_SET, reminderSet);

		int medId = (int) database.insert(MedTableInfo.TABLE_NAME, null, cv);
		AlarmPackage alarmPackage = myMedStatement.getAlarmPackage();
		addMedReminder(context, medId, alarmPackage);
		return medId;
	}

	public Cursor getAllRows(Context context){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String[] projection = {
		  MedTableInfo._ID,
		  MedTableInfo.COLUMN_NAME_JSON_STRING,
		  MedTableInfo.COLUMN_NAME_REMINDER_SET
		};

		String sortOrder =
		  MedTableInfo._ID + " ASC";

		Cursor cursor = db.query(
		  MedTableInfo.TABLE_NAME, projection, null, null, null, null, sortOrder
		);

		return cursor;
	}

	public Cursor getMedReminders(Context context, int medId){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String query = 	"SELECT " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo._ID +
		  ", " + MedTableInfo.COLUMN_NAME_JSON_STRING +
		  ", " + MedReminderTableInfo.COLUMN_NAME_TIME +
		  " FROM " + MedTableInfo.TABLE_NAME + " " +
		  " JOIN " + MedReminderTableInfo.TABLE_NAME +
		  " ON " + MedTableInfo.TABLE_NAME+"."+MedTableInfo._ID +
		  " = " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo.COLUMN_NAME_MED_ID +
		  " WHERE " +  MedTableInfo.TABLE_NAME+"."+MedTableInfo._ID + " = " + medId +
		  " ORDER BY " + MedReminderTableInfo.COLUMN_NAME_TIME;

		Cursor c = db.rawQuery(query, null);

		return c;
	}

	public Cursor getMedRemindersForStatus(Context context, int status){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String query = 	"SELECT " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo._ID +
		  				", " + MedTableInfo.COLUMN_NAME_JSON_STRING +
		  				", " + MedReminderTableInfo.COLUMN_NAME_TIME +
		  				" FROM " + MedTableInfo.TABLE_NAME + " " +
		  				" JOIN " + MedReminderTableInfo.TABLE_NAME +
		  				" ON " + MedTableInfo.TABLE_NAME+"."+MedTableInfo._ID +
		  				" = " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo.COLUMN_NAME_MED_ID +
		  				" WHERE " + MedReminderTableInfo.COLUMN_NAME_STATUS + " = " + status +
		  				" ORDER BY " + MedReminderTableInfo.COLUMN_NAME_TIME;

		Cursor c = db.rawQuery(query, null);

		return c;
	}

	public Cursor getMedReminder(Context context, int id){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String query = 	"SELECT " + MedTableInfo.COLUMN_NAME_JSON_STRING +
		  ", " + MedReminderTableInfo.COLUMN_NAME_TIME +
		  " FROM " + MedTableInfo.TABLE_NAME  +
		  " JOIN " + MedReminderTableInfo.TABLE_NAME +
		  " ON " + MedTableInfo.TABLE_NAME+"."+MedTableInfo._ID +
		  " = " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo.COLUMN_NAME_MED_ID +
		  " WHERE " + MedReminderTableInfo.TABLE_NAME+"."+MedReminderTableInfo._ID + " = " + id +
		  " ORDER BY " + MedReminderTableInfo.COLUMN_NAME_TIME;

		Cursor c = db.rawQuery(query, null);

		return c;
	}

	public MyMedication getMedicationStatement(Context context, int id){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String[] projection = {
		  MedTableInfo.COLUMN_NAME_JSON_STRING, MedTableInfo.COLUMN_NAME_REMINDER_SET
		};

		Cursor cursor = db.query(
		  MedTableInfo.TABLE_NAME, projection, MedTableInfo._ID+" = "+id, null, null, null, null
		);

		if (cursor.moveToFirst()) {
			String jsonMedString = cursor.getString(cursor.getColumnIndex(MedTableInfo.COLUMN_NAME_JSON_STRING));
			MedicationStatement medStatement = (MedicationStatement) mFhirServices.toResource(jsonMedString);

			Boolean reminderSet = cursor.getInt(cursor.getColumnIndex(MedTableInfo.COLUMN_NAME_REMINDER_SET)) != 0;

			MyMedication myMedication = new MyMedication();
			myMedication.setFhirMedStatement(medStatement);
			myMedication.setReminderSet(reminderSet);
			return myMedication;
		}
		return null;
	};

	public void removeMedication(Context context, int id){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();
		String selection = MedTableInfo._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };
		db.delete(MedTableInfo.TABLE_NAME, selection, selectionArgs);
	}

	public void removeMedReminder(Context context, int medId) {
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();
		String selection = MedReminderTableInfo._ID + " LIKE ?";

		Cursor cursor = getMedReminders(context, medId);
		while (cursor.moveToNext()) {
			long remId = cursor.getLong(cursor.getColumnIndex(MedReminderTableInfo._ID));
			String[] selectionArgs = { String.valueOf(remId) };
			db.delete(MedReminderTableInfo.TABLE_NAME, selection, selectionArgs);
			Intent alarmIntent = new Intent(context, AlarmSetter.class);
			alarmIntent.putExtra(AlarmSetter.REMINDER_ID_KEY, (int) remId);
			alarmIntent.putExtra(AlarmSetter.REMINDER_SET_KEY, false);
			context.sendBroadcast(alarmIntent);
		}
	}

	public void changeMedReminderStatus(Context context, int id, int newStatus) {
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(MedReminderTableInfo.COLUMN_NAME_STATUS, newStatus);
		String selection = MedReminderTableInfo._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };
		db.update(MedReminderTableInfo.TABLE_NAME, cv, selection, selectionArgs);
	}

	public void updateMedication(Context context, int id, MyMedication updatedMyMedication){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		MedicationStatement updatedMedStatement = updatedMyMedication.getFhirMedStatement();
		String updatedJsonMedString = mFhirServices.toJsonString(updatedMedStatement);
		cv.put(MedTableInfo.COLUMN_NAME_JSON_STRING, updatedJsonMedString);
		cv.put(MedTableInfo.COLUMN_NAME_REMINDER_SET, updatedMyMedication.getReminderSet());
		String selection = MedTableInfo._ID + " = ?";
		String[] selectionArgs = new String[]{String.valueOf(id)};
		db.update(MedTableInfo.TABLE_NAME, cv, selection, selectionArgs);

		if (updatedMyMedication.getReminderSet()) {
			removeMedReminder(context, id);
			addMedReminder(context, id, updatedMyMedication.getAlarmPackage());
		} else {
			removeMedReminder(context, id);
		}
	}

	public void clearMedTable(Context context){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getWritableDatabase();
		db.delete(MedTableInfo.TABLE_NAME, null, null);
		db.delete(MedReminderTableInfo.TABLE_NAME, null, null);
	}

	private void addMedReminder(Context context, int medId, AlarmPackage alarmPackage) {
		if (alarmPackage != null) {
			DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
			SQLiteDatabase database = dbo.getWritableDatabase();
			Calendar calendar = Calendar.getInstance();
			for (int i = 0; i < alarmPackage.getDailyNumOfAlarms(); i++) {
				long alarmTime = alarmPackage.getAlarmTime() + i * alarmPackage.getIntervalTimeToNextAlarm();
				if (calendar.getTimeInMillis() < alarmTime) {
					ContentValues cv2 = new ContentValues();
					cv2.put(MedReminderTableInfo.COLUMN_NAME_MED_ID, medId);
					cv2.put(MedReminderTableInfo.COLUMN_NAME_TIME, alarmTime);
						cv2.put(MedReminderTableInfo.COLUMN_NAME_STATUS, MedUptakeStatus.PENDING.ordinal());
					long remId = database.insert(MedReminderTableInfo.TABLE_NAME, null, cv2);
					int remIDInt = (int) remId;
					Intent alarmIntent = new Intent(context, AlarmSetter.class);
					alarmIntent.putExtra(AlarmSetter.REMINDER_ID_KEY, remIDInt);
					alarmIntent.putExtra(AlarmSetter.ALARM_TIME_KEY, alarmTime);
					alarmIntent.putExtra(AlarmSetter.REMINDER_SET_KEY, true);
					context.sendBroadcast(alarmIntent);
				}
			}
		}
	}

}
