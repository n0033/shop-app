package pl.nw.zadanie_06.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.nw.zadanie_06.MainActivity

class NotificationUtils {


    companion object {
        fun createNotification(context: Context, title: String, content: String) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, "channel_1")
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(androidx.appcompat.R.drawable.abc_btn_colored_material)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent)
            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        }
    }
}