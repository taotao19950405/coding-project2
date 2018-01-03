package orionhealth.app.data.medicationDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import orionhealth.app.data.dataModels.Criticality;
import orionhealth.app.data.medicationDatabase.DatabaseContract.AllergyTableInfo;
import orionhealth.app.fhir.FhirServices;

/**
 * Created by archanakhanal on 12/7/2016.
 */
public final class AllergyTableOperations {
    private static AllergyTableOperations sAllergyTableOperations;
    private FhirServices aFhirServices;

    private AllergyTableOperations() {
        aFhirServices = FhirServices.getsFhirServices();
    }

    public static AllergyTableOperations getInstance(){
        if (sAllergyTableOperations == null) {
            sAllergyTableOperations = new AllergyTableOperations();
            return sAllergyTableOperations;
        } else {
            return sAllergyTableOperations;
        }
    }

    public int addToAllergyTable(Context context, AllergyIntolerance allergyIntolerance) {
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase database = dbo.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String jsonStringAllergy = FhirServices.getsFhirServices().toJsonString(allergyIntolerance);
        cv.put(AllergyTableInfo.COLUMN_NAME_JSON_STRING, jsonStringAllergy);


        int criticalityInt = Criticality.UNABLE_TO_DETERMINE.ordinal();

        if (allergyIntolerance.getCriticality().toString().equals("CRITL")){
            criticalityInt = Criticality.LOW_RISK.ordinal();
        } else if (allergyIntolerance.getCriticality().toString().equals("CRITU")){
            criticalityInt = Criticality.UNABLE_TO_DETERMINE.ordinal();
        } else if (allergyIntolerance.getCriticality().toString().equals("CRITH")){
            criticalityInt = Criticality.HIGH_RISK.ordinal();
        }

        cv.put(AllergyTableInfo.COLUMN_NAME_CRITICALITY, criticalityInt);
		return (int) database.insert(AllergyTableInfo.TABLE_NAME, null, cv);
    }

    public Cursor getAllRows(Context context){
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase db = dbo.getReadableDatabase();

        String[] projection = {
                AllergyTableInfo._ID,
                AllergyTableInfo.COLUMN_NAME_JSON_STRING,
                AllergyTableInfo.COLUMN_NAME_CRITICALITY
        };

        String sortOrder =
                AllergyTableInfo.COLUMN_NAME_CRITICALITY + " ASC";

        Cursor cursor = db.query(
                AllergyTableInfo.TABLE_NAME, projection, null, null, null, null, sortOrder
        );


        return cursor;
    }

    public AllergyIntolerance getAllergyIntolerance(Context context, int id){
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase db = dbo.getReadableDatabase();

        String[] projection = {
                AllergyTableInfo.COLUMN_NAME_JSON_STRING
        };

        Cursor cursor = db.query(
                AllergyTableInfo.TABLE_NAME, projection, AllergyTableInfo._ID+" = "+id, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            String jsonAllergyString = cursor.getString(cursor.getColumnIndex(AllergyTableInfo.COLUMN_NAME_JSON_STRING));
            AllergyIntolerance allergyIntolerance = (AllergyIntolerance) aFhirServices.toResource(jsonAllergyString);
            return allergyIntolerance;
        }
        return null;
    };

    public void removeAllergy(Context context, int id){
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase db = dbo.getReadableDatabase();
        String selection = AllergyTableInfo._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(AllergyTableInfo.TABLE_NAME, selection, selectionArgs);
    }

    public void updateAllergy(Context context, int id, AllergyIntolerance updatedAllergyIntolerance){
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase db = dbo.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String updatedJsonAllergyString = aFhirServices.toJsonString(updatedAllergyIntolerance);
        String selection = AllergyTableInfo._ID + " = ?";

        String[] selectionArgs = new String[]{String.valueOf(id)};

        int criticalityInt = Criticality.UNABLE_TO_DETERMINE.ordinal();

        if (updatedAllergyIntolerance.getCriticality().toString().equals("CRITL")){
            criticalityInt = Criticality.LOW_RISK.ordinal();
        } else if (updatedAllergyIntolerance.getCriticality().toString().equals("CRITU")){
            criticalityInt = Criticality.UNABLE_TO_DETERMINE.ordinal();
        } else if (updatedAllergyIntolerance.getCriticality().toString().equals("CRITH")){
            criticalityInt = Criticality.HIGH_RISK.ordinal();
        }

        cv.put(AllergyTableInfo.COLUMN_NAME_CRITICALITY, criticalityInt);
        cv.put(AllergyTableInfo.COLUMN_NAME_JSON_STRING, updatedJsonAllergyString);
        db.update(AllergyTableInfo.TABLE_NAME, cv, selection, selectionArgs);
    }

    public void clearAllergyTable(Context context){
        DatabaseInitializer dbo = DatabaseInitializer.getInstance(context);
        SQLiteDatabase db = dbo.getWritableDatabase();
        db.delete(AllergyTableInfo.TABLE_NAME, null, null);
    }
}
