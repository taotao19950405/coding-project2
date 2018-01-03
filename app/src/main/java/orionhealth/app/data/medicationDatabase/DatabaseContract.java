//		 @author:  Bill
package orionhealth.app.data.medicationDatabase;

import android.provider.BaseColumns;

/**
 * Created by bill on 8/04/16.
 */
public final class DatabaseContract {

    public DatabaseContract() {
    }

	public static  abstract class MedTableInfo implements BaseColumns {
		public static final String TABLE_NAME = "medication";
		public static final String COLUMN_NAME_JSON_STRING = "json_string";
		public static final String COLUMN_NAME_REMINDER_SET = "reminder_set";
	}

	public static abstract class MedReminderTableInfo implements BaseColumns {
		public static final String TABLE_NAME = "medication_reminders";
		public static final String COLUMN_NAME_MED_ID = "medication_id";
		public static final String COLUMN_NAME_TIME = "time";
		public static final String COLUMN_NAME_STATUS = "status";
	}

	public static abstract class AllergyTableInfo implements BaseColumns{
		public static final String TABLE_NAME = "allergy";
		public static final String COLUMN_NAME_JSON_STRING = "json_string_allergy";
		public static final String COLUMN_NAME_CRITICALITY = "json_string_criticality";
	}

	public static  abstract class CondTableInfo implements BaseColumns {
		public static final String TABLE_NAME = "condition";
		public static final String COLUMN_NAME_JSON_STRING = "json_string";
	}
}
