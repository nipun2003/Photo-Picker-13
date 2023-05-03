package com.nipunapps.photo_picker_13

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ImageCropView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var imageBitmap: Bitmap? = null
    private var cropRect: Rect? = null

    fun setImageUri(uri : Uri){
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setImageBitmap(bitmap: Bitmap) {
        imageBitmap = bitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            imageBitmap?.let { bitmap ->
                val rect = Rect(0, 0, bitmap.width, bitmap.height)
                canvas.drawBitmap(bitmap, rect, rect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                cropRect = Rect(
                    it.x.toInt(),
                    it.y.toInt(),
                    it.x.toInt() + 200,
                    it.y.toInt() + 200
                )
                invalidate()
            }
        }
        return true
    }

    fun getCroppedBitmap(): Bitmap? {
        return imageBitmap?.let { bitmap ->
            cropRect?.let { rect ->
                Bitmap.createBitmap(
                    bitmap,
                    rect.left,
                    rect.top,
                    rect.width(),
                    rect.height()
                )
            }
        }
    }
}