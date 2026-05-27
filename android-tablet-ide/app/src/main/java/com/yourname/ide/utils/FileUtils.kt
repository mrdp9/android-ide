package com.yourname.ide.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.*
import java.nio.charset.StandardCharsets

class FileUtils(private val activity: Activity) {

    // For accessing files
    private val openFileLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { openFileCallback?.onFileUri(uri) }
    }

    // For saving files
    private val saveFileLauncher = activity.registerForActivityResult(
        ActivityResultContracts.CreateDocument()
    ) { uri: Uri? ->
        uri?.let { saveFileCallback?.onFileUri(uri) }
    }

    // Callbacks
    var openFileCallback: OpenFileCallback? = null
    var saveFileCallback: SaveFileCallback? = null

    interface OpenFileCallback {
        fun onFileUri(uri: Uri)
    }

    interface SaveFileCallback {
        fun onFileUri(uri: Uri)
    }

    fun openFile() {
        openFileLauncher.launch("*/*")
    }

    fun saveFile(fileName: String = "untitled.txt") {
        saveFileLauncher.launch(fileName)
    }

    fun readTextFromUri(uri: Uri, contentResolver: ContentResolver): String {
        return contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: ""
    }

    fun writeTextToUri(uri: Uri, contentResolver: ContentResolver, text: String) {
        contentResolver.openOutputStream(uri)?.use {
            it.write(text.toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.DISPLAY_NAME)
                if (nameIndex != -1) {
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } else {
                    "unknown"
                }
            } ?: "unknown"
        } else {
            uri.lastPathSegment ?: "unknown"
        }
    }

    fun getExtensionFromUri(uri: Uri, contentResolver: ContentResolver): String {
        val fileName = getFileNameFromUri(uri, contentResolver)
        return fileName.substringAfterLast(".").lowercase()
    }

    fun getLanguageFromExtension(extension: String): String {
        return when (extension.lowercase()) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "js" -> "javascript"
            "ts" -> "typescript"
            "html", "htm" -> "html"
            "css" -> "css"
            "json" -> "json"
            "xml" -> "xml"
            "md" -> "markdown"
            "py" -> "python"
            "rb" -> "ruby"
            "go" -> "go"
            "rs" -> "rust"
            "cpp", "cc", "cxx", "c++" -> "cpp"
            "c" -> "c"
            "h", "hpp" -> "cpp"
            "sh", "bash" -> "shellscript"
            "yml", "yaml" -> "yaml"
            "sql" -> "sql"
            "php" -> "php"
            "cs" -> "csharp"
            "swift" -> "swift"
            "dart" -> "dart"
            "lua" -> "lua"
            "pl", "pm" -> "perl"
            "r" -> "r"
            "scala" -> "scala"
            "gradle", "gradle.kts" -> "gradle"
            else -> "plaintext"
        }
    }
}
</task_progress>
</write_to_file>
</execute_command>