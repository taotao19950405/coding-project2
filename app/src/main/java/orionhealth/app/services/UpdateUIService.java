package orionhealth.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by bill on 16/09/16.
 */
public class UpdateUIService extends IntentService {

	public UpdateUIService() {
		super("MyUpdateUIService");
	}

	public UpdateUIService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
		intent = new Intent("update");
		broadcaster.sendBroadcast(intent);
	}
}
