package io.scer.codeeditor.components

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.util.AttributeSet
import android.util.TypedValue

class CodeEditorLineNumbers : CodeEditor {
    private lateinit var editorLayout: Layout

    var startLineNumber: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        viewTreeObserver.addOnGlobalLayoutListener { editorLayout = layout }
    }

    override fun onDraw(canvas: Canvas) {
        val padding = getPixels(getDigitCount() * 10 + 10).toInt()
        setPadding(padding, 0, 0, 0)

        val firstLine = editorLayout.getLineForVertical(scrollY)
        val lastLine: Int = try {
            editorLayout.getLineForVertical(scrollY + (height - extendedPaddingTop - extendedPaddingBottom))
        } catch (e: NullPointerException) {
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

    private fun drawLineNumber(canvas: Canvas, layout: Layout, positionY: Int, line: Int) {
        val positionX = layout.getLineLeft(line).toInt()
        canvas.drawText((startLineNumber + line).toString(), (positionX + computeHorizontalScrollOffset()).toFloat(), positionY.toFloat(), paint)
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

    private fun getPixels(dp: Int): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
}