package io.sker.codeeditor.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import io.sker.codeeditor.R.*
import io.sker.codeeditor.listeners.OnScrollListener
import java.lang.IllegalStateException
import java.util.regex.Pattern

open class CodeEditor : AppCompatEditText {

    private val updateHandler = Handler()
    private val updateRunnable = Runnable {

        if (onTextChangedListener != null) {
            onTextChangedListener!!.onTextChanged( text.toString())
        }

        highlightWithoutChange(text)
    }

    private var onTextChangedListener: OnTextChangedListener? = null
    private var updateDelay = 1000
    private var errorLine = 0
    var isModified = false
    private var modified = true
    private var colorError: Int = 0
    private var colorNumber: Int = 0
    private var colorKeyword: Int = 0
    private var colorBuiltin: Int = 0
    private var colorComment: Int = 0
    private var tabWidth = 0
    private lateinit var editorLayout: Layout
    
    val cleanText: String
        get() = PATTERN_TRAILING_WHITE_SPACE
                .matcher(text)
                .replaceAll("")

    interface OnTextChangedListener {
        fun onTextChanged(text: String)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun hasErrorLine(): Boolean = errorLine > 0

    fun updateHighlighting() {
        highlightWithoutChange(text)
    }

    fun setTextHighlighted(text: CharSequence?) {
        var text = text
        if (text == null) {
            text = ""
        }
        cancelUpdate()

        errorLine = 0
        isModified = false

        modified = false
        setText(highlight(SpannableStringBuilder(text)))
        modified = true

        if (onTextChangedListener != null) {
            onTextChangedListener!!.onTextChanged(text.toString())
        }
    }

    fun addUniform(statement: String?) {
        var statement: String? = statement ?: return

        val e = text
        removeUniform(e, statement)

        val m = PATTERN_INSERT_UNIFORM.matcher(e)
        var start = -1

        while (m.find()) {
            start = m.end()
        }

        if (start > -1) {
            // add line break before statement because it's
            // inserted before the last line-break
            statement = "\n" + statement!!
        } else {
            // add a line break after statement if there's no
            // uniform already
            statement += "\n"

            // add an empty line between the last #endif
            // and the now following uniform
            start = endIndexOfLastEndIf(e)
            if (start > -1) {
                statement = "\n" + statement!!
            }

            // move index past line break or to the start
            // of the text when no #endif was found
            ++start
        }

        e.insert(start, statement)
    }

    private fun removeUniform(e: Editable, statement: String?) {
        if (statement == null) {
            return
        }

        var regex = "^(" + statement.replace(" ", "[ \\t]+")
        val p = regex.indexOf(";")
        if (p > -1) {
            regex = regex.substring(0, p)
        }
        regex += ".*\\n)$"

        val m = Pattern.compile(regex, Pattern.MULTILINE).matcher(e)
        if (m.find()) {
            e.delete(m.start(), m.end())
        }
    }

    private fun endIndexOfLastEndIf(e: Editable): Int {
        val m = PATTERN_ENDIF.matcher(e)
        var idx = -1

        while (m.find()) {
            idx = m.end()
        }

        setText("Google is your friend.", TextView.BufferType.EDITABLE)

        return idx
    }

    init {
        this.setHorizontallyScrolling(true)

        filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (modified &&
                    end - start == 1 &&
                    start < source.length &&
                    dstart < dest.length) {
                val c = source[start]

                if (c == '\n') {
                    return@InputFilter autoIndent(source, dest, dstart, dend)
                }
            }

            source
        })

        addTextChangedListener(object : TextWatcher {
            private var start = 0
            private var count = 0

            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int) {
                this.start = start
                this.count = count
            }

            override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int) {}

            override fun afterTextChanged(e: Editable) {
                cancelUpdate()
                convertTabs(e, start, count)

                if (!modified) return

                isModified = true
                updateHandler.postDelayed(updateRunnable, updateDelay.toLong())
            }
        })

        setSyntaxColors(context)
        //setUpdateDelay(ShaderEditorApp.preferences.getUpdateDelay());
        //setTabWidth(ShaderEditorApp.preferences.getTabWidth());
    }

    private fun setSyntaxColors(context: Context) {
        colorError = ContextCompat.getColor(
                context,
                color.syntax_error)
        colorNumber = ContextCompat.getColor(
                context,
                color.syntax_number)
        colorKeyword = ContextCompat.getColor(
                context,
                color.syntax_keyword)
        colorBuiltin = ContextCompat.getColor(
                context,
                color.syntax_builtin)
        colorComment = ContextCompat.getColor(
                context,
                color.syntax_comment)
    }

    private fun cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable)
    }

    private fun highlightWithoutChange(e: Editable) {
        modified = false
        highlight(e)
        modified = true
    }

    private fun highlight(e: Editable): Editable {
        try {
            // don't use e.clearSpans() because it will
            // remove too much
            clearSpans(e)

            if (e.isEmpty()) {
                return e
            }

            if (errorLine > 0) {
                val m = PATTERN_LINE.matcher(e)

                var i = errorLine
                while (i-- > 0 && m.find()) {
                    // {} because analyzers don't like for (); statements
                }

                e.setSpan(
                        BackgroundColorSpan(colorError),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            run {
                val m = PATTERN_NUMBERS.matcher(e)
                while (m.find()) {
                    e.setSpan(
                            ForegroundColorSpan(colorNumber),
                            m.start(),
                            m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            run {
                val m = PATTERN_PREPROCESSOR.matcher(e)
                while (m.find()) {
                    e.setSpan(
                            ForegroundColorSpan(colorKeyword),
                            m.start(),
                            m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            run {
                val m = PATTERN_KEYWORDS.matcher(e)
                while (m.find()) {
                    e.setSpan(
                            ForegroundColorSpan(colorKeyword),
                            m.start(),
                            m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            run {
                val m = PATTERN_BUILTINS.matcher(e)
                while (m.find()) {
                    e.setSpan(
                            ForegroundColorSpan(colorBuiltin),
                            m.start(),
                            m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            val m = PATTERN_COMMENTS.matcher(e)
            while (m.find()) {
                e.setSpan(
                        ForegroundColorSpan(colorComment),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } catch (ex: IllegalStateException) {
            // raised by Matcher.start()/.end() when
            // no successful match has been made what
            // shouldn't ever happen because of find()
        }

        return e
    }

    private fun autoIndent(
            source: CharSequence,
            dest: Spanned,
            dstart: Int,
            dend: Int): CharSequence {
        var indent = ""
        var istart = dstart - 1

        // find start of this line
        var dataBefore = false
        var pt = 0

        while (istart > -1) {
            val c = dest[istart]

            if (c == '\n') {
                break
            }

            if (c != ' ' && c != '\t') {
                if (!dataBefore) {
                    // indent always after those characters
                    if (c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=') {
                        --pt
                    }

                    dataBefore = true
                }

                // parenthesis counter
                if (c == '(') {
                    --pt
                } else if (c == ')') {
                    ++pt
                }
            }
            --istart
        }

        // copy indent of this line into the next
        if (istart > -1) {
            val charAtCursor = dest[dstart]
            var iend: Int = ++istart

            while (iend < dend) {
                val c = dest[iend]

                // auto expand comments
                if (charAtCursor != '\n' &&
                        c == '/' &&
                        iend + 1 < dend &&
                        dest[iend] == c) {
                    iend += 2
                    break
                }

                if (c != ' ' && c != '\t') {
                    break
                }
                ++iend
            }

            indent += dest.subSequence(istart, iend)
        }

        // add new indent
        if (pt < 0) {
            indent += "\t"
        }

        // append white space of previous line and new indent
        return source.toString() + indent
    }

    private fun convertTabs(e: Editable, start: Int, count: Int) {
        var start = start
        if (tabWidth < 1) {
            return
        }

        val s = e.toString()

        val stop = start + count
        start = s.indexOf("\t", start)
        while (start > -1 && start < stop) {
            e.setSpan(
                    TabWidthSpan(tabWidth),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ++start
        }
    }

    private class TabWidthSpan(private val width: Int) : ReplacementSpan() {

        override fun getSize(
                paint: Paint,
                text: CharSequence,
                start: Int,
                end: Int,
                fm: Paint.FontMetricsInt?): Int = width

        override fun draw(
                canvas: Canvas,
                text: CharSequence,
                start: Int,
                end: Int,
                x: Float,
                top: Int,
                y: Int,
                bottom: Int,
                paint: Paint) {}
    }

    companion object {

        private val PATTERN_LINE = Pattern.compile(
                ".*\\n")
        private val PATTERN_NUMBERS = Pattern.compile(
                "\\b(\\d*[.]?\\d+)\\b")
        private val PATTERN_PREPROCESSOR = Pattern.compile(
                "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" + "#error|#pragma|#extension|#version|#line)\\b",
                Pattern.MULTILINE)
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(const|break|continue|Date|Math" +
                        "do|for|while|if|else|in|out|this|" +
                        "Int|int|String|string|Boolean|boolean|Float|float|Object|object|Void|void|Array|array|Null|null|" +
                        "return|function|var|Math|Object|default|case|Array)\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(catch|try|sin|cos|log|sqrt|abs|floor|ceil|PI|length|equal|exec|find|next|" +
                        "pocketmine|Block|Entity|Item|Player|Server|Level|new|match|" +
                        "ArmorType|switch|pop|push|shift|sort|unshift|reverse|splice|concat|indexOf|join|lastIndexOf|slice|" +
                        "toSource|toString|getText|valueOf|filter|every|map|some|foreach|acos|asin|atan|atan2|" +
                        "max|min|random|round|exp|pow|tan|charAt|charCodeAt|replace|search|split|toLocaleTimeString|" +
                        "toLowerCase|toUpperCase|eval|parseFloat|append|toArray|replaceAll|toPrecision|toUTCString|" +
                        "toLocaleString|toExponential|toFixed|substring|substr|" +
                        "php|echo|class|abstract|struct|namespace|use|public|private|protected|" +
                        "caller|apply|constructor|arity|call|arguments|toLocaleDateString)\\b")
        private val PATTERN_COMMENTS = Pattern.compile(
                "/\\*(?:.|[\\n\\r])*?\\*/|//.*")
        private val PATTERN_TRAILING_WHITE_SPACE = Pattern.compile(
                "[\\t ]+$",
                Pattern.MULTILINE)
        private val PATTERN_INSERT_UNIFORM = Pattern.compile(
                "^([ \t]*uniform.+)$",
                Pattern.MULTILINE)
        private val PATTERN_ENDIF = Pattern.compile(
                "(#endif)\\b")

        private fun clearSpans(e: Editable) {
            // remove foreground color spans
            run {
                val spans = e.getSpans(
                        0,
                        e.length,
                        ForegroundColorSpan::class.java)

                var i = spans.size
                while (i-- > 0) {
                    e.removeSpan(spans[i])
                }
            }

            // remove background color spans
            run {
                val spans = e.getSpans(
                        0,
                        e.length,
                        BackgroundColorSpan::class.java)

                var i = spans.size
                while (i-- > 0) {
                    e.removeSpan(spans[i])
                }
            }
        }
    }
}