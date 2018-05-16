package createpdf.example.com.itextpdf.editfile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class TouchEventListener(context: Context) : ScaleGestureDetector.SimpleOnScaleGestureListener(), View.OnTouchListener {
    private var dX: Float = 0f
    private var dY: Float = 0f
    private var isOneFinger: Boolean = true
    var mScaleDetector = ScaleGestureDetector(context, MyPinchListener())

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        Companion.view = v
        mScaleDetector.onTouchEvent(event)
        when (event.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isOneFinger = false
            }
            MotionEvent.ACTION_POINTER_UP -> {
                isOneFinger = false
            }
            MotionEvent.ACTION_UP -> {
                isOneFinger = true
//                (view.parent as ViewManager).removeView(view)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isOneFinger)
                    v.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
            }
            MotionEvent.ACTION_CANCEL -> {
                isOneFinger = true
            }
        }
        return true
    }

    internal class MyPinchListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            Log.e("SRRSSRR", "SCALE fACTOR " + mScaleFactor)
            view.scaleY = mScaleFactor
            view.scaleX = mScaleFactor
            view.invalidate()
            return true
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var view: View
        var mScaleFactor: Float = 1.0F
    }
}