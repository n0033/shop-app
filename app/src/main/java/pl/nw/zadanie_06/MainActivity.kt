package pl.nw.zadanie_06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.PaymentConfiguration
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.databinding.ActivityMainBinding
import pl.nw.zadanie_06.utils.StripeUtils


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: LocalDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var stripe: Unit
    private val DEBUG_LOGOUT = false

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    val authProviders = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.GitHubBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = LocalDatabase.getInstance(applicationContext)
        auth = FirebaseAuth.getInstance()
        stripe = PaymentConfiguration.init(applicationContext, Constants.STRIPE_PUBLISHABLE_KEY)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()

        if (DEBUG_LOGOUT) {
            AuthUI.getInstance().signOut(this)
        }

        if (auth.currentUser == null) {
            signInLauncher.launch(createSignInIntent())
        }

        if (auth.currentUser != null) {
            runBlocking {
                StripeUtils.ensureCustomerExists(db, auth.currentUser!!.uid)
            }
        }
        createNotificationChannel()
        createNotificationChannelImportanceHigh()
        createNotificationHighSeverity()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createSignInIntent(): Intent {
        return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(authProviders)
            .build()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode != RESULT_OK) {
            val intent = Intent()
            startActivity(intent)
        }
        if (auth.currentUser != null) {
            runBlocking {
                StripeUtils.ensureCustomerExists(db, auth.currentUser!!.uid)
            }
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.new_products_notification_name)
        val descriptionText = getString(R.string.new_products_notification_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("channel_1", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotificationChannelImportanceHigh() {
        val name = getString(R.string.discount_notification_name)
        val descriptionText = getString(R.string.discount_notification_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("channel_2", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private fun createNotificationHighSeverity() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, "channel_1")
            .setSmallIcon(R.drawable.drink)
            .setContentTitle("SALE SALE SALE SALE")
            .setContentText("SUPER DISCOUNTS ON COLA")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setNumber(1)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(2, builder.build())
        }
    }
}
