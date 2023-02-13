package pl.nw.zadanie_06.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pl.nw.zadanie_06.MainActivity


class UserUtils {
    companion object {
        fun ensureAuth(activity: Activity): FirebaseUser? {
            // returns authenticated user, or redirects to auth activity
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            }
            return user
        }
    }
}