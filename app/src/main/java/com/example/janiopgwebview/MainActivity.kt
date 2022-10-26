package com.example.janiopgwebview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONObject

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {
    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun logEventFirebase(eventName: String, params: String) {
        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        var gson = Gson()

        //val bundle = gson.fromJson(params, Bundle::class.java)
        val bundle = Bundle().apply {
          putBoolean("app", true)
          putString("category", "Web View")
          putString("label", "acesso pelo app")
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    @JavascriptInterface
    fun setUserProperty(name: String, value: String) {
        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setUserProperty(name, value)
    }

}

class MainActivity : AppCompatActivity() {

    // [START declare_analytics]
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    // [END declare_analytics]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [START shared_app_measurement]
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        // [END shared_app_measurement]

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.addJavascriptInterface(WebAppInterface(this), "AnalyticsMobile")
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        myWebView.settings.userAgentString = "WebView UserAgent"
        myWebView.loadUrl("https://janiopg.github.io/The-Prancing-Pony/")
    }
}