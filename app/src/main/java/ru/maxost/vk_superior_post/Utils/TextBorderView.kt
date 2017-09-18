package ru.maxost.vk_superior_post.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import ru.maxost.switchlog.SwitchLog
import ru.maxost.vk_superior_post.Model.TextStyle
import ru.maxost.vk_superior_post.R

/**
 * Created by Maxim Ostrovidov on 15.09.17.
 * (c) White Soft
 */
class TextBorderView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {

    private var state: MyEditTextState? = null

    fun setState(state: MyEditTextState) {
        SwitchLog.scream(state.toString())
        this.state = state
        invalidate()
    }

    private val colorWhite = ContextCompat.getColor(context, R.color.white)
    private val colorWhiteTransparent = ContextCompat.getColor(context, R.color.whiteTransparent)
    private val colorShadow = ContextCompat.getColor(context, R.color.shadow)
    private val hTopOffset = 6.dp2px(context)
    private val hBottomOffset = 8.dp2px(context)
    private val wOffset = 12.dp2px(context)
    private val points = mutableListOf<PointF>()
    private val path = Path()
    private val shadowPath = Path()

    private val paint = Paint().apply {
        isDither = true
        strokeWidth = 10f
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(10f)
        isAntiAlias = true
    }

    private val shadowPaint = Paint().apply {
        isDither = true
        strokeWidth = 4f
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(10f)
        isAntiAlias = true
    }

    //TODO multiple enters
    //TODO multiple spaces
    //TODO smooth edges

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        SwitchLog.scream("onLayout top: $top")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        SwitchLog.scream("onSizeChanged h: $h oldh: $oldh")
    }

    override fun onDraw(canvas: Canvas?) {
        SwitchLog.scream("onDraw")
        if(state == null || state!!.text.isEmpty()) {
            super.onDraw(canvas)
            return
        }

        if(state?.textStyle == TextStyle.WHITE_WITH_BACKGROUND) drawBorder(canvas, colorWhiteTransparent)
        if(state?.textStyle == TextStyle.BLACK_WITH_BACKGROUND) drawBorder(canvas, colorWhite)

        super.onDraw(canvas)
    }

    private fun drawBorder(canvas: Canvas?, color: Int) {
        if(canvas == null) return

        paint.color = color
        val lines = state!!.linesList
        val textStartPoint = state!!.startPoint
        val borderStartPoint = IntArray(2).apply { getLocationOnScreen(this) }
        SwitchLog.scream("textStartPoint ${textStartPoint[1]} borderStartPoint: ${borderStartPoint[1]}")
        createBorderPath(path, lines.map { convertRect(it, textStartPoint, borderStartPoint) })

        if(color == colorWhite) { //TODO shadow below transparent border
            shadowPath.reset()
            shadowPath.addPath(path)
            shadowPath.offset(0f, 4f)
            shadowPath.addPath(path)
            shadowPath.fillType = Path.FillType.EVEN_ODD
            shadowPaint.color = colorShadow
            canvas.drawPath(shadowPath, shadowPaint)
        }

        canvas.drawPath(path, paint)
    }

    private fun convertRect(fromRect: RectF, fromViewStart: IntArray, toViewStart: IntArray): RectF {
        val xShift = fromViewStart[0] - toViewStart[0]
        val yShift = fromViewStart[1] - toViewStart[1]
        return RectF().apply {
            left = fromRect.left + xShift
            right = fromRect.right + xShift
            top = fromRect.top + yShift
            bottom = fromRect.bottom + yShift
        }
    }

    private fun createBorderPath(path: Path, linesList: List<RectF>): Path {
        points.clear()
        path.reset()

        for (lineIndex in 0 until linesList.size) {
            if(linesList[lineIndex].width().toInt() == 0) continue

            val rect = linesList[lineIndex]
            SwitchLog.scream("lineIndex: $lineIndex rect left: ${rect.left} right: ${rect.right} top: ${rect.top} bottom: ${rect.bottom}")

            if(linesList.size == 1 || (lineIndex == 0 && lineIndex + 1 < linesList.size && linesList[lineIndex + 1].width().toInt() == 0)) {
                SwitchLog.scream("1")
                path.moveTo(rect.left - wOffset + 20, rect.top - hTopOffset)
                points.add(PointF(rect.right + wOffset, rect.top - hTopOffset))
                points.add(PointF(rect.right + wOffset, rect.bottom + hBottomOffset))
                points.add(PointF(rect.left - wOffset, rect.bottom + hBottomOffset))
                points.add(PointF(rect.left - wOffset, rect.top - hTopOffset))
                break
            }

            if (lineIndex == 0) {
                SwitchLog.scream("2")
                path.moveTo(rect.centerX(), rect.top - hTopOffset)
                points.add(PointF(rect.centerX(), rect.top - hTopOffset))
                points.add(PointF(rect.right + wOffset, rect.top - hTopOffset))
                points.add(PointF(rect.right + wOffset, rect.bottom - hTopOffset))
                continue
            }

            if (lineIndex == linesList.size - 1 || (lineIndex + 1 < linesList.size && linesList[lineIndex + 1].width().toInt() == 0)) {
                SwitchLog.scream("3")
                if(rect.right + wOffset < points.last().x) {
                    points.add(PointF(points.last().x, points.last().y + hBottomOffset))
                }
                points.add(PointF(rect.right + wOffset, points.last().y))
                points.add(PointF(rect.right + wOffset, rect.bottom + hBottomOffset))
                points.add(PointF(rect.centerX(), rect.bottom + hBottomOffset))
                continue
            }

            if (lineIndex > 0 && lineIndex < linesList.size - 1) {
                SwitchLog.scream("4")
                if(rect.right + wOffset < points.last().x) {
                    points.add(PointF(points.last().x, points.last().y + hBottomOffset))
                }
                points.add(PointF(rect.right + wOffset, points.last().y))
                points.add(PointF(rect.right + wOffset, rect.bottom - hTopOffset))
                continue
            }
        }
        points.forEach { path.lineTo(it.x, it.y) }

        //left side
        if(linesList.size != 1 && points.isNotEmpty() && linesList[1].width().toInt() != 0) {
            val centerX = points.first().x
            points.reversed().forEach {
                path.lineTo(centerX - (it.x - centerX), it.y)
            }
        }

        path.close()
        return path
    }
}