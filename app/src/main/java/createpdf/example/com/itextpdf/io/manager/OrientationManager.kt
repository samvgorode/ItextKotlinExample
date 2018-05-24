package createpdf.example.com.itextpdf.io.manager

import android.content.Context
import android.view.OrientationEventListener


class OrientationManager(context : Context) : OrientationEventListener(context) {

    private var isVertical: Boolean = false
    private  var listener: VerticalListener? = null
    private var isActive: Boolean = false

    override fun onOrientationChanged(orientation: Int) {
        if (orientation < 30 || orientation > 330) {
            isVertical = true
            listener?.changed(true)
        } else if (orientation in 30..330) {
            isVertical = false
            listener?.changed(false)
        }
    }

    fun setListener(listener: VerticalListener) {
        this.listener = listener
    }

    override fun enable() {
        if (canDetectOrientation()) {
            super.enable()
            isActive = true
        }
    }

    override fun disable() {
        isActive = false
        super.disable()
    }

    fun isActive(): Boolean {
        return isActive
    }

    fun isVertical(): Boolean {
        return isVertical
    }

    interface VerticalListener {
        fun changed(isVertical: Boolean) = Unit
    }
}