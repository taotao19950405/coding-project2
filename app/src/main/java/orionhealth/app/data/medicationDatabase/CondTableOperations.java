package orionhealth.app.data.medicationDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import orionhealth.app.data.medicationDatabase.DatabaseContract.CondTableInfo;
import orionhealth.app.fhir.FhirServices;

/**
 * Created by lu on 17/07/16.
 */
public final class CondTableOperations {
	private static CondTableOperations scondTableOperations;
	private FhirServices mFhirServices;

	private CondTableOperations(){
		mFhirServices = FhirServices.getsFhirServices();
	}

	public static CondTableOperations getInstance(){
		if (scondTableOperations == null){
			scondTableOperations = new CondTableOperations();
			return scondTableOperations;
		}else{
			return scondTableOperations;
		}
	}

	public int addToCondTable(Context context, Condition condition) {
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase database = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();

		String jsonStringCond = FhirServices.getsFhirServices().toJsonString(condition);
		cv.put(CondTableInfo.COLUMN_NAME_JSON_STRING, jsonStringCond);
		return (int) database.insert(CondTableInfo.TABLE_NAME, null, cv);
	}

	public Cursor getAllRows(Context context){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String[] projection = {
		  CondTableInfo._ID,
		  CondTableInfo.COLUMN_NAME_JSON_STRING
		};

		String sortOrder =
			CondTableInfo._ID + " ASC";

		Cursor cursor = db.query(
				CondTableInfo.TABLE_NAME, projection, null, null, null, null, sortOrder
		);
		return cursor;
	}

	public Condition getCondition(Context context, int id){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();

		String[] projection = {
		  CondTableInfo.COLUMN_NAME_JSON_STRING
		};

		Cursor cursor = db.query(
				CondTableInfo.TABLE_NAME, projection, CondTableInfo._ID+" = "+id, null, null, null, null
		);

		if (cursor.moveToFirst()) {
			String jsonCondString = cursor.getString(cursor.getColumnIndex(CondTableInfo.COLUMN_NAME_JSON_STRING));
			Condition condition = (Condition) mFhirServices.toResource(jsonCondString);
			return condition;
		}
		return null;
	};

	public void removeCondition(Context context, int id){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getReadableDatabase();
		String selection = CondTableInfo._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };
		db.delete(CondTableInfo.TABLE_NAME, selection, selectionArgs);
	}

	public void updateCondition(Context context, int id, Condition updatedCondition){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		String updatedJsonCondString = mFhirServices.toJsonString(updatedCondition);
		cv.put(CondTableInfo.COLUMN_NAME_JSON_STRING, updatedJsonCondString);
		String selection = CondTableInfo._ID + " = ?";
		String[] selectionArgs = new String[]{String.valueOf(id)};
		db.update(CondTableInfo.TABLE_NAME, cv, selection, selectionArgs);
	}

	public void clearCondTable(Context context){
		DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
		SQLiteDatabase db = dbo.getWritableDatabase();
		db.delete(CondTableInfo.TABLE_NAME, null, null);
	}
}
