package com.logoocc.multhreaddownload.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import com.logoocc.multhreaddownload.R;
import com.logoocc.multhreaddownload.download.DownloadActivity;


public class DownloadReceiver extends BroadcastReceiver {
	private NotificationManager mNotificationManager;
	private RemoteViews mRemoteViews;
	private Intent mIntent;
	private PendingIntent mPendingIntent;
	public Handler mHandler;
	@Override
	public void onReceive(Context context, Intent intent) {
		int progress=intent.getIntExtra("pro", 0);
		int id=intent.getIntExtra("id", 0);
		final Notification notification = new Notification();
		mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.download);
		notification.icon = R.drawable.face;
		notification.when=id;
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		mRemoteViews.setImageViewResource(R.id.image_download,
				R.drawable.download);
		/**
		 * 单击Notification时发出的Intent消息�?
		 */

		mIntent = new Intent(context, DownloadActivity.class);
		mPendingIntent = PendingIntent.getActivity(context, 0,
				mIntent, 0);
				mRemoteViews.setProgressBar(R.id.progress_down, 100,
						progress, false);
				mRemoteViews.setTextViewText(R.id.text_download,
						progress + "%");
				notification.contentView = mRemoteViews;
				notification.contentIntent = mPendingIntent;
				mNotificationManager.notify(id, notification);

	}
}
