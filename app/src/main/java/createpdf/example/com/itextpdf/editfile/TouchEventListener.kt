package createpdf.example.com.itextpdf.editfile

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout


class TouchEventListener(val frame: FrameLayout) : View.OnTouchListener {
    private var dX: Float = 0f
    private var dY: Float = 0f
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                v.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start();
            }
        }
        frame.invalidate()
        return true
    }
}