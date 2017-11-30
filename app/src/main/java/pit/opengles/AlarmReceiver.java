package pit.opengles;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by paulh on 21.11.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static int NID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, WallpaperMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.tlw_icon).setColor(Color.rgb(230,230,230)).setContentTitle("Tiles Live Wallpaper").setContentText(context.getResources().getString(R.string.notification))
                                                        .setSound(alarmSound).setAutoCancel(true).setWhen(when)
                                                        .setContentIntent(pendingIntent).setVibrate(new long[]{500, 500, 500, 500, 500});
        notificationManager.notify(NID, mNotifyBuilder.build());
        NID++;

    }

}
