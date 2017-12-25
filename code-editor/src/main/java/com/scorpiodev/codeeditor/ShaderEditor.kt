package com.scorpiodev.codeeditor

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
import android.util.TypedValue
import android.widget.AbsListView
import android.widget.TextView
import com.scorpiodev.codeeditor.listeners.OnBottomReachedListener
import com.scorpiodev.codeeditor.listeners.OnScrollListener
import java.lang.IllegalStateException
import java.util.regex.Pattern

open class ShaderEditor : AppCompatEditText {

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

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    fun setOnTextChangedListener(listener: OnTextChangedListener) {
        onTextChangedListener = listener
    }

    fun setUpdateDelay(ms: Int) {
        updateDelay = ms
    }

    fun hasErrorLine(): Boolean = errorLine > 0

    fun setErrorLine(line: Int) {
        errorLine = line
    }

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

    private fun getDigitCount(): Int {
        var count = 0
        var len = lineCount
        while (len > 0) {
            count++
            len /= 10
        }
        return count
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

    private val CHUNK = 20000
    private var FILE_CONTENT: String? = null
    private var currentBuffer: String? = null
    private lateinit var loaded: StringBuilder

    private var onBottomReachedListener: OnBottomReachedListener? = null
    private var onScrollListener: OnScrollListener? = null

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        // TODO Add support chank load
    }

    fun setText(text: String?) {
        if (text.isNullOrBlank()) return
        // TODO Add support chank load

    }

    // TODO Add support chank load
    private fun loadInChunks(bigString: String) {
        loaded.append(bigString.substring(0, CHUNK))
        setTextHighlighted(loaded)

        this.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached() {
                when {
                    loaded.length >= bigString.length -> return
                    loaded.length + CHUNK > bigString.length -> {
                        //String buffer = bigString.substring(loaded.length(), bigString.length());
                        val buffer = bigString.substring(loaded.length, bigString.length)
                        loaded.replace(0, loaded.length, buffer)
                    }
                    else -> {
                        val buffer = bigString.substring(loaded.length, loaded.length + CHUNK)
                        loaded.replace(0, loaded.length, buffer)
                    }
                }

                setTextHighlighted(loaded)
            }
        })
    }

    override fun onScrollChanged(l:Int, t:Int, oldl:Int, oldt:Int) {
        if (onScrollListener == null || onBottomReachedListener == null) return
        onScrollListener!!.onScrolled()
        if (t > oldt)
            onScrollListener!!.onScrolledDown()
        else
            onScrollListener!!.onScrolledUp()
        val diff = (this.bottom - (height + scrollY))
        if (diff <= 20)
        {
            onBottomReachedListener!!.onBottomReached()
        }
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun getOnBottomReachedListener(): OnBottomReachedListener = onBottomReachedListener!!

    private fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener
    }

    fun getOnScrollListener(): OnScrollListener = onScrollListener!!

    fun setOnScrollListener(onScrollListener: OnScrollListener) {
        this.onScrollListener = onScrollListener
    }

    private fun init(context: Context) {
        setHorizontallyScrolling(true)

        viewTreeObserver.addOnGlobalLayoutListener { editorLayout = layout }

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
                R.color.syntax_error)
        colorNumber = ContextCompat.getColor(
                context,
                R.color.syntax_number)
        colorKeyword = ContextCompat.getColor(
                context,
                R.color.syntax_keyword)
        colorBuiltin = ContextCompat.getColor(
                context,
                R.color.syntax_builtin)
        colorComment = ContextCompat.getColor(
                context,
                R.color.syntax_comment)
    }

    override fun onDraw(canvas: Canvas) {
        val padding = getPixels(getDigitCount() * 10 + 10).toInt()
        setPadding(padding, 0, 0, 0)

        val firstLine = editorLayout.getLineForVertical(scrollY)
        val lastLine: Int = try {
            editorLayout.getLineForVertical(scrollY + (height - extendedPaddingTop - extendedPaddingBottom))
        } catch (npe: NullPointerException) {
            editorLayout.getLineForVertical(scrollY + (height - paddingTop - paddingBottom))
        }

        //the y position starts at the baseline of the first line
        var positionY = baseline + (editorLayout.getLineBaseline(firstLine) - editorLayout.getLineBaseline(0))
        drawLineNumber(canvas, editorLayout, positionY, firstLine)
        for (i in firstLine + 1..lastLine) {
            //get the next y position using the difference between the current and last baseline
            positionY += editorLayout.getLineBaseline(i) - editorLayout.getLineBaseline(i - 1)
            drawLineNumber(canvas, editorLayout, positionY, i)
        }

        super.onDraw(canvas)
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

    private fun drawLineNumber(canvas: Canvas, layout: Layout, positionY: Int, line: Int) {
        val positionX = layout.getLineLeft(line).toInt()
        canvas.drawText((line + 1).toString(), (positionX + computeHorizontalScrollOffset()).toFloat(), positionY.toFloat(), paint)
    }

    private fun getPixels(dp: Int): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)

    companion object {

        private val PATTERN_LINE = Pattern.compile(
                ".*\\n")
        private val PATTERN_NUMBERS = Pattern.compile(
                "\\b(\\d*[.]?\\d+)\\b")
        private val PATTERN_PREPROCESSOR = Pattern.compile(
                "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" + "#error|#pragma|#extension|#version|#line)\\b",
                Pattern.MULTILINE)
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(attribute|const|uniform|varying|break|continue|" +
                        "do|for|while|if|else|in|out|inout|float|int|void|bool|true|false|" +
                        "lowp|mediump|highp|precision|invariant|discard|return|mat2|mat3|" +
                        "mat4|vec2|vec3|vec4|ivec2|ivec3|ivec4|bvec2|bvec3|bvec4|sampler2D|" +
                        "samplerCube|struct|gl_Vertex|gl_FragCoord|gl_FragColor)\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(radians|degrees|sin|cos|tan|asin|acos|atan|pow|" +
                        "exp|log|exp2|log2|sqrt|inversesqrt|abs|sign|floor|ceil|fract|mod|" +
                        "min|max|clamp|mix|step|smoothstep|length|distance|dot|cross|" +
                        "normalize|faceforward|reflect|refract|matrixCompMult|lessThan|" +
                        "lessThanEqual|greaterThan|greaterThanEqual|equal|notEqual|any|all|" +
                        "not|dFdx|dFdy|fwidth|texture2D|texture2DProj|texture2DLod|" +
                        "texture2DProjLod|textureCube|textureCubeLod)\\b")
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