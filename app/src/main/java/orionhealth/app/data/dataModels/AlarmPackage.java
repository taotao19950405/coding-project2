package orionhealth.app.data.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bill on 11/07/16.
 */
public class AlarmPackage implements Parcelable{
	private long alarmTime;
	private int timeToNextAlarm;
	private int dailyNumOfAlarms;


	public AlarmPackage() {
	}


	public int getIntervalTimeToNextAlarm() {
		return timeToNextAlarm;
	}

	public long getAlarmTime() {
		return this.alarmTime;
	}

	public long getDailyNumOfAlarms() {
		return this.dailyNumOfAlarms;
	}

//	public long[] getAlarmTimes() { return this.alarmTimes; }


	public void setTimeIntervalToNextAlarm(int timeInterval) {
		this.timeToNextAlarm = timeInterval;
	}

	public void setAlarmTime(long alarmTime) {
		this.alarmTime = alarmTime;
	}

	public void setDailyNumOfAlarmsTime(int dailyNumOfAlarms) {
		this.dailyNumOfAlarms = dailyNumOfAlarms;
	}

	protected AlarmPackage(Parcel in) {
		alarmTime = in.readLong();
		timeToNextAlarm = in.readInt();
//		alarmTimes = in.createLongArray();
	}

	public static final Creator<AlarmPackage> CREATOR = new Creator<AlarmPackage>() {
		@Override
		public AlarmPackage createFromParcel(Parcel in) {
			return new AlarmPackage(in);
		}

		@Override
		public AlarmPackage[] newArray(int size) {
			return new AlarmPackage[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(alarmTime);
		dest.writeInt(timeToNextAlarm);
//		dest.writeLongArray(alarmTimes);
	}

//	public void setAlarmTimes() {
//		alarmTimes = new long[dailyNumOfAlarms];
//		for (int i = 0; i < alarmTimes.length; i++) {
//			alarmTimes[i] = alarmTime + i * timeToNextAlarm;
//		}
//
//	}
}
