//       Description:
//			Create instance in databse
//		 @ Author:  Bill
//			@Review: Lu
//			@Reviewer: 19 Apr 2016
package orionhealth.app.data.medicationDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import orionhealth.app.data.medicationDatabase.DatabaseContract.MedTableInfo;
import orionhealth.app.data.medicationDatabase.DatabaseContract.AllergyTableInfo;
import orionhealth.app.data.medicationDatabase.DatabaseContract.CondTableInfo;
import orionhealth.app.data.medicationDatabase.DatabaseContract.MedReminderTableInfo;

/**
 * Created by bill on 8/04/16.
 */
public class DatabaseInitializer extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
	private static final String BOOLEAN_TYPE = " BOOLEAN";
	private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MedTableInfo.TABLE_NAME + " (" +
                    MedTableInfo._ID + " INTEGER PRIMARY KEY," +
                    MedTableInfo.COLUMN_NAME_JSON_STRING + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_ENTRIES2=
            "CREATE TABLE " + AllergyTableInfo.TABLE_NAME + " (" +
                    AllergyTableInfo._ID + " INTEGER PRIMARY KEY," +
                    AllergyTableInfo.COLUMN_NAME_JSON_STRING + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_ENTRIES_COND =
            "CREATE TABLE " + CondTableInfo.TABLE_NAME + " (" +
                    CondTableInfo._ID + " INTEGER PRIMARY KEY," +
                    CondTableInfo.COLUMN_NAME_JSON_STRING + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_CRITICALITY_ALLERGY =
            "ALTER TABLE " + AllergyTableInfo.TABLE_NAME + " ADD " +
                    AllergyTableInfo.COLUMN_NAME_CRITICALITY + INTEGER_TYPE;


	private static final String SQL_ADD_REMINDER_SET_COLUMN =
	  		"ALTER TABLE "+ MedTableInfo.TABLE_NAME + " ADD COLUMN " +
					MedTableInfo.COLUMN_NAME_REMINDER_SET + BOOLEAN_TYPE;

	private static final String SQL_CREATE__ENTRIES_REMINDER =
			  "CREATE TABLE " + MedReminderTableInfo.TABLE_NAME + " (" +
				MedReminderTableInfo._ID + " INTEGER PRIMARY KEY," +
				MedReminderTableInfo.COLUMN_NAME_MED_ID + INTEGER_TYPE + COMMA_SEP +
				MedReminderTableInfo.COLUMN_NAME_TIME + DATETIME_TYPE +
				" )";

	private static final String SQL_ADD_REMINDER_STATUS_COLUMN =
	  "ALTER TABLE "+ MedReminderTableInfo.TABLE_NAME + " ADD COLUMN " +
		MedReminderTableInfo.COLUMN_NAME_STATUS + INTEGER_TYPE;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MedTableInfo.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES2 =
            "DROP TABLE IF EXISTS " + AllergyTableInfo.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_COND =
            "DROP TABLE IF EXISTS " + CondTableInfo.TABLE_NAME;

	private static final String SQL_DELETE_ENTRIES_REMINDERS =
	  "DROP TABLE IF EXISTS " + MedReminderTableInfo.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "Main.db";

    private static DatabaseInitializer sInstance;

    private DatabaseInitializer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseInitializer getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseInitializer(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES2);
        db.execSQL(SQL_CREATE_ENTRIES_COND);
		db.execSQL(SQL_ADD_REMINDER_SET_COLUMN);
		db.execSQL(SQL_CREATE__ENTRIES_REMINDER);
        db.execSQL(SQL_CREATE_CRITICALITY_ALLERGY);
		db.execSQL(SQL_ADD_REMINDER_STATUS_COLUMN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
			case 2: db.execSQL(SQL_CREATE_ENTRIES);
			case 3: db.execSQL(SQL_CREATE_ENTRIES2);
			case 4: db.execSQL(SQL_CREATE_ENTRIES_COND);
			case 5: db.execSQL(SQL_ADD_REMINDER_SET_COLUMN);
			case 6: db.execSQL(SQL_CREATE__ENTRIES_REMINDER);
            case 7: db.execSQL(SQL_CREATE_CRITICALITY_ALLERGY);
			case 8: db.execSQL(SQL_ADD_REMINDER_STATUS_COLUMN);
		}
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}

