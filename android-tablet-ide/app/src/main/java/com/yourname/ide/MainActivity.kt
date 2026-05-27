package com.yourname.ide

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yourname.ide.utils.FileUtils

// The Bridge class to handle messages from JavaScript
class WebAppInterface(private val mainActivity: MainActivity) {
    @JavascriptInterface
    fun onTextChanged(newText: String) {
        // This prints to the Android Logcat when the user types!
        println("IDE Code Updated: \n$newText")
        // Update the editor content in Kotlin when JS sends changes
        mainActivity.updateEditorContent(newText)
    }
    
    @JavascriptInterface
    fun openFileDialog() {
        mainActivity.openFile()
    }
    
    @JavascriptInterface
    fun saveFileDialog() {
        mainActivity.saveFile()
    }
    
    @JavascriptInterface
    fun getEditorContent(): String {
        return mainActivity.currentContent
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var fileUtils: FileUtils
    private var currentFileUri: Uri? = null
    private var currentFileName: String = "untitled.txt"
    private var currentContent: String = ""

    // For receiving content from JS
    private val jsContentCallback = ActivityResultCallback<String> { content ->
        currentContent = content
        // Update the editor with the content from JS
        webView.evaluateJavascript(
            "setEditorCode(${
                org.json.JSONObject(currentContent).toString()
            })",
            null
        )
    }

    // For sending file content to JS
    private val fileContentCallback = ActivityResultCallback<Uri> { uri ->
        currentFileUri = uri
        if (uri != null) {
            val contentResolver = contentResolver
            currentFileName = fileUtils.getFileNameFromUri(uri, contentResolver)
            val extension = fileUtils.getExtensionFromUri(uri, contentResolver)
            val language = fileUtils.getLanguageFromExtension(extension)
            
            // Read file content
            val content = fileUtils.readTextFromUri(uri, contentResolver)
            currentContent = content
            
            // Update editor with file content and language
            webView.evaluateJavascript(
                "setEditorCode(${
                    org.json.JSONObject(content).toString()
                });monaco.editor.getModels()[0]?.updateOptions({language: '$language'});",
                null
            )
            
            // Update activity title
            title = "$currentFileName - Tablet IDE"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a WebView programmatically to fill the screen
        webView = WebView(this)
        setContentView(webView)

        // Enable JavaScript and Dom Storage (Critical for Monaco)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        
        // Prevent opening links in the external browser
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Initialize file utils
        fileUtils = FileUtils(this)
        fileUtils.openFileCallback = { uri -> fileContentCallback.onResult(uri) }
        fileUtils.saveFileCallback = { uri -> 
            currentFileUri = uri
            val contentResolver = contentResolver
            currentFileName = fileUtils.getFileNameFromUri(uri, contentResolver)
            title = "$currentFileName - Tablet IDE"
            fileUtils.writeTextToUri(uri, contentResolver, currentContent)
        }

        // Inject our Kotlin bridge into the JavaScript environment
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidBridge")

        // Load the local HTML file from the assets folder
        webView.loadUrl("file:///android_asset/editor.html")
    }
    
    fun updateEditorContent(content: String) {
        currentContent = content
    }
    
    fun openFile() {
        fileUtils.openFile()
    }
    
    fun saveFile() {
        if (currentFileUri != null) {
            // Save to existing file
            val contentResolver = contentResolver
            fileUtils.writeTextToUri(currentFileUri!!, contentResolver, currentContent)
        } else {
            // Save as new file
            fileUtils.saveFile(currentFileName)
        }
    }
}
