package io.sker.codeeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.sker.codeeditor.components.CodeEditor
import io.sker.codeeditor.components.InteractiveScrollView
import io.sker.codeeditor.listeners.OnScrollReachedListener

/**
 * Load full code or load by chunks
 */
enum class DocumentLoad {
    /**
     * Loaded full
     */
    FULL,
    /**
     * Loaded by chunks
     */
    CHUNKS
}

/**
 * Code editor view
 */
class NCodeEditor : LinearLayout {
    /**
     * Main view
     */
    private lateinit var view: View
    /**
     * Root custom scrollview
     */
    private lateinit var scrollView: InteractiveScrollView
    /**
     * Code editor (Custom EditText)
     */
    private lateinit var codeEditor: CodeEditor
    /**
     * Already load symbols
     */
    private val loaded: StringBuilder = StringBuilder()

    /**
     * Code
     */
    var code: String? = ""
        set(value) {
            if (value.isNullOrBlank()) return
            this.loadDocument(value!!)
        }

    /**
     * @constructor
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * @constructor
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    /**
     * Init main view
     */
    @SuppressLint("InflateParams")
    private fun init (context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.view = inflater.inflate(R.layout.code_editor, null)
        this.scrollView = this.view.findViewById(R.id.scroll_view)
        this.codeEditor = this.view.findViewById(R.id.code_editor)
        this.addView(this.view)
    }

    /**
     * Load full file or load by chunks
     * *
     * @param codeText
     * *
     * @return Loaded by chunks or full load
     */
    private fun loadDocument(codeText: String): DocumentLoad {
        this.scrollView.smoothScrollTo(0, 0)

        this.codeEditor.isFocusable = false
        this.codeEditor.setOnClickListener({ this.codeEditor.isFocusableInTouchMode = true })

        return when (codeText.length > CHUNK) {
            true -> {
                this.loadInChunks(codeText)
                DocumentLoad.CHUNKS
            }
            false -> {
                this.loaded.append(codeText)
                this.codeEditor.setTextHighlighted(loaded)
                DocumentLoad.CHUNKS
            }
        }
    }

    /**
     * Load code by chunks
     * TODO: Add support chunk load
     * *
     * @param codeText
     */
    private fun loadInChunks(codeText: String) {
        this.loaded.append(codeText.substring(0, CHUNK))
        this.codeEditor.setTextHighlighted(this.loaded)
        var lastLength = this.loaded.length

        this.scrollView.setOnScrollReachedListener(object : OnScrollReachedListener {
            override fun onBottomReached(callback: () -> Unit) {
                lastLength = when {
                    (lastLength >= codeText.length) -> return@onBottomReached
                    (lastLength + CHUNK > codeText.length) -> {
                        val buffer = codeText.substring(lastLength, codeText.length)
                        loaded.replace(0, loaded.length, buffer)
                        codeText.length
                    }
                    else -> {
                        val buffer = codeText.substring(lastLength, lastLength + CHUNK)
                        loaded.replace(0, loaded.length, buffer)
                        lastLength + CHUNK
                    }
                }

                codeEditor.setTextHighlighted(loaded)
                callback()
            }

            override fun onTopReached(callback: () -> Unit) {
                // TODO: Implement top reached
                lastLength = when {
                    (lastLength <= CHUNK) -> return@onTopReached
                    (lastLength - CHUNK < CHUNK) -> {
                        val buffer = codeText.substring(0, CHUNK)
                        loaded.replace(0, loaded.length, buffer)
                        CHUNK
                    }
                    else -> {
                        val buffer = codeText.substring(lastLength - CHUNK * 2, lastLength)
                        loaded.replace(0, loaded.length, buffer)
                        lastLength - CHUNK
                    }
                }

                codeEditor.setTextHighlighted(loaded)
                callback()
            }
        })
    }

    companion object {
        /**
         * Max string length
         */
        const val CHUNK = 20000
    }
}