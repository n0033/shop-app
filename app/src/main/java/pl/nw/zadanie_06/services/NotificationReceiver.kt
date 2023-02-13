package pl.nw.zadanie_06.services

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.models.data.Message
import pl.nw.zadanie_06.utils.NotificationUtils

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        if (remoteInput != null) {
            val message = remoteInput.getCharSequence("key_text_reply").toString()
            runBlocking {
                val client = HttpClient(CIO) {
                    install(Logging) {
                        level = LogLevel.INFO
                    }
                }
                val bodyJson = """{"message": "$message"}"""

                // Configure request URL
                val response: HttpResponse = client.post(Constants.MOCK_MESSAGE_RESPONSE_URL) {
                    setBody(bodyJson)
                }
                val respMessage = response.body<String>()
                val messageObj = Gson().fromJson(respMessage, Message::class.java)
                NotificationUtils.createNotification(context, "Received message", messageObj.response)

            }

        }

    }
}