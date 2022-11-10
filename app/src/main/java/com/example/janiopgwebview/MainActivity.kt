package com.example.janiopgwebview

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

// Gson - o Json chega como String, converter para Json

data class ParamsScreenview(
  var screen_name: String? = null,
  var screen_class: String? = null,
) {}

data class ParamsLoadedInApp(
  var app: String? = null,
  var category: String? = null,
  var label: String? = null
) {}

data class Items (
    var item_id: String? = null,
    var item_name: String? = null,
    var item_category: String? = null,
    var item_variant: String? = null,
    var item_brand: String? = null,
    var price: Double? = null
        ) {}

data class ParamsAddToCart (
    var currency: String? = null,
    var value: Double = 0.0,
    var items: Items? = null
        ) {}

// Fim - Gson

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
        val bundle = getBundle(eventName, params)
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    @JavascriptInterface
    fun setUserProperty(name: String, value: String) {
        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setUserProperty(name, value)
    }

    fun getBundle (eventName: String, params: String): Bundle {
        val gson = Gson()
        val bundle = Bundle()

        if (eventName == "screen_view") {
            val myJson = gson.fromJson(params, ParamsScreenview::class.java)
            bundle.apply {
                putString("screen_name", myJson.screen_name)
                putString("screen_class", myJson.screen_class)
            }

        } else if (eventName == "page_loaded_in_app") {
            val myJson = gson.fromJson(params, ParamsLoadedInApp::class.java)
            bundle.apply {
                putString("app", myJson.app)
                putString("category", myJson.category)
                putString("label", myJson.label)
            }
        } else if (eventName == "add_to_cart") {
            val myJson = gson.fromJson(params, ParamsAddToCart::class.java)
            val product = arrayListOf<Parcelable>()
            product.add(Bundle().apply {
                putString("item_id", myJson.items?.item_id)
                putString("item_name", myJson.items?.item_name)
                putString("item_category", myJson.items?.item_category)
                putString("item_variant", myJson.items?.item_variant)
                putString("item_brand", myJson.items?.item_brand)
                putDouble("price", myJson.items?.price?: 0.0)
            })

            bundle.apply {
                putString("currency", myJson.currency)
                putDouble("value", myJson.value)
                putParcelableArrayList("items", product)
            }
        }

        return bundle
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
        //myWebView.loadUrl("https://janiopg.github.io/tool-test/")
        //myWebView.loadUrl("https://www.leroymerlin.com.br/")
        myWebView.loadUrl("https://janiopg.github.io/The-Prancing-Pony/")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}