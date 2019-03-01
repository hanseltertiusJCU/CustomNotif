package com.example.customnotif;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends IntentService {
	
	private static final String KEY_REPLY = "key_reply_message";
	public static String REPLY_ACTION = "com.example.notification.directreply.REPLY_ACTION";
	public static String CHANNEL_ID = "channel_01";
	public static CharSequence CHANNEL_NAME = "dicoding channel";
	
	private int mNotificationId;
	private int mMessageId;
	
	public NotificationService() {
		super("NotificationService");
	}
	
	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		if(intent != null){
			showNotification();
		}
	}
	
	// Method tsb berguna untuk menampilkan notification
	private void showNotification() {
		mNotificationId = 1;
		mMessageId = 123;
		
		// Baris code ini berguna untuk membawa info yg digunakan utk mengambil input untuk direct reply
		// (kesannya kyk membawa reply input ke app)
		String replyLabel = getString(R.string.notif_action_reply);
		RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
				.setLabel(replyLabel)
				.build();
		
		// Baris code ini brguna utk menghubungkan action dengan remote input
		NotificationCompat.Action.Builder builder = new NotificationCompat.Action.Builder(
				R.drawable.ic_reply_black_24px, replyLabel, getReplyPendingIntent()); // Parameter dari object tsb adalah icon, label dan PendingIntent
		builder.addRemoteInput(remoteInput); // Method ini berguna untuk memasukkan remote input ke action, sehingga ada relasi antara action dengan remote input
		builder.setAllowGeneratedReplies(true);
		// Return NotificationCompat.Action object dengan memanggil method build
		NotificationCompat.Action replyAction = builder.build();
		
		// Masukkan Action ke NotificationCompat.Builder
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notifications_white_48px)
				.setContentTitle(getString(R.string.notif_title))
				.setContentText(getString(R.string.notif_content))
				.setShowWhen(true)
				.addAction(replyAction);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
			
			mBuilder.setChannelId(CHANNEL_ID);
			
			if(mNotificationManager != null){
				mNotificationManager.createNotificationChannel(channel);
			}
		}
		
		Notification notification = mBuilder.build();
		
		if(mNotificationManager != null){
			mNotificationManager.notify(mNotificationId, notification);
		}
	}
	
	// Kedua method tsb berguna utk bagaimana memperoleh input dari direct reply
	private PendingIntent getReplyPendingIntent(){
		Intent intent;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){ // Cek jika versi android yg dijalankan itu dari Android N ke atas
			intent = NotificationBroadcastReceiver.getReplyMessageIntent(this, mNotificationId, mMessageId);
			return PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT); // Menampilkan langsung direct reply ke notifikasi
		} else { // Jika versi android yg dijalankan itu dibawah Android N
			intent = ReplyActivity.getReplyMessageIntent(this, mNotificationId, mMessageId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT); // Buka ReplyActivity utk menindaklanjuti notifikasi yg masuk (buka ReplyActivity -> tampilin direct reply)
		}
	}
	
	// Method tsb berguna untuk membaca input dari Direct Message (DM) {@link onReceive() method di NotificationBroadcastReceiver}
	public static CharSequence getReplyMessage(Intent intent){
		Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
		if(remoteInput != null){
			return remoteInput.getCharSequence(KEY_REPLY);
		}
		return null;
	}
}
