package io.sker.codeeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import io.sker.codeeditor.components.CodeEditorLineNumbers
import io.sker.codeeditor.components.InteractiveScrollView
import io.sker.codeeditor.enums.DocumentLoad
import io.sker.codeeditor.listeners.OnScrollReachedListener
import java.util.*
import kotlin.system.measureTimeMillis

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
    private lateinit var codeEditor: CodeEditorLineNumbers
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

    var startLineNumber: Int
        set(value) {
            codeEditor.startLineNumber = value
        }
        get() = codeEditor.startLineNumber

    private var lastLinesCount: Int =  1

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
     * TODO: Fix number lines on second fragment
     * TODO: Smooth code loading
     * *
     * @param codeText
     */
    private fun loadInChunks(codeText: String) {
        val firstBuffer = codeText.substring(0, CHUNK)
        this.loaded.append(firstBuffer)
        lastLinesCount = this.getLinesCountFromString(firstBuffer)
        startLineNumber = 1

        this.codeEditor.setTextHighlighted(this.loaded)
        var lastLength = this.loaded.length
        val linesController = Stack<Int>()
        linesController.push(loaded.lines().count())

        this.scrollView.setOnScrollReachedListener(object : OnScrollReachedListener {
            override fun onBottomReached(callback: () -> Unit) {
                lastLength = when {
                    (lastLength >= codeText.length) -> return@onBottomReached
                    (lastLength + CHUNK > codeText.length) -> {
                        val buffer = codeText.substring(lastLength, codeText.length)
                        val linesCount = getLinesCountFromString(loaded.toString())
                        startLineNumber += linesCount
                        linesController.push(linesCount)
                        loaded.replace(0, CHUNK, buffer)
                        codeText.length
                    }
                    else -> {
                        val buffer = codeText.substring(lastLength, lastLength + CHUNK)
                        val linesCount = getLinesCountFromString(loaded.toString())
                        startLineNumber += linesCount
                        linesController.push(linesCount)
                        loaded.replace(0, CHUNK, buffer)
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
                        loaded.replace(0, CHUNK, buffer)
                        startLineNumber -= linesController.pop()
                        CHUNK
                    }
                    else -> {
                        val buffer = codeText.substring(lastLength - CHUNK * 2, lastLength - CHUNK)
                        loaded.replace(0, CHUNK, buffer)
                        startLineNumber -= linesController.pop()
                        lastLength - CHUNK
                    }
                }

                codeEditor.setTextHighlighted(loaded)
                callback()
            }
        })
    }

    private fun getLinesCountFromString(text: String): Int =
            StringTokenizer(text, System.getProperty("line.separator")).countTokens()

    companion object {
        /**
         * Max string length
         */
        const val CHUNK = 20000
    }
}