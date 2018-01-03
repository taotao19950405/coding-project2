package orionhealth.app.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bill on 10/07/16.
 */
public class RingToneService extends Service {
	private boolean isRinging = false;
	private Ringtone ringtone;
	private long ringDuration = 60000;


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
		if (r != null && !isRinging){
			ringtone = r;
			ringtone.play();
			isRinging = true;
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					stopSelf();
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, ringDuration);

		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		ringtone.stop();
		isRinging = false;
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
