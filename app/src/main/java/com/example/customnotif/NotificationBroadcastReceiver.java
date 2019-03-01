package com.example.customnotif;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static com.example.customnotif.NotificationService.CHANNEL_ID;
import static com.example.customnotif.NotificationService.CHANNEL_NAME;
import static com.example.customnotif.NotificationService.REPLY_ACTION;

// Kelas tsb berfungsi untuk menerima balasan atau direct reply serta untuk memperbaharui notifikasi
public class NotificationBroadcastReceiver extends BroadcastReceiver {
	
	private static final String KEY_NOTIFICATION_ID = "key_notification_id";
	private static String KEY_MESSAGE_ID = "key_message_id";
	
	public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId){
		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
		intent.setAction(REPLY_ACTION); // set action to intent
		intent.putExtra(KEY_NOTIFICATION_ID, notificationId); // put extra info notif id to intent
		intent.putExtra(KEY_MESSAGE_ID, messageId); // put extra info message id to intent
		return intent;
	}
	
	public NotificationBroadcastReceiver(){
	
	}
	
	// Menerima input dari RemoteInput {@link getReplyMessage() method}
	@Override
	public void onReceive(Context context, Intent intent) {
		if(REPLY_ACTION.equals(intent.getAction())){ // Get Action from {@link getReplyMessageIntent() method}
			CharSequence message = NotificationService.getReplyMessage(intent); // Return remoteInput.getCharSequence() method lalu masukkan ke variable message, line tsb itu merupakan message inputnya
			int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0); // dapatin info message id dari method tsb
			
			Toast.makeText(context, "Message ID: " + messageId + "\nMessage: " + message, Toast.LENGTH_SHORT).show();
			
			int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1); // dapatin info notif id dari method tsb
			updateNotification(context, notifyId); // Call for update notification
		}
	}
	
	// Method tsb berguna utk memanggil notification item yg bru
	private void updateNotification(Context context, int notifyId) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notifications_white_48px)
				.setContentTitle(context.getString(R.string.notif_title_sent))
				.setContentText(context.getString(R.string.notif_content_sent));
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
			
			mBuilder.setChannelId(CHANNEL_ID);
			
			if(mNotificationManager != null){
				mNotificationManager.createNotificationChannel(channel);
			}
		}
		
		Notification notification = mBuilder.build();
		
		if(mNotificationManager != null){
			mNotificationManager.notify(notifyId, notification);
		}
	}
}
