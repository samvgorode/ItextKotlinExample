package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.ui.presenters.PdfPagePresenter
import createpdf.example.com.itextpdf.ui.uiall.adapter.ViewPagerAdapter
import createpdf.example.com.itextpdf.ui.uiinterfaces.PdfPageView
import kotlinx.android.synthetic.main.activity_pdf.*


class PdfPageActivity : MvpAppCompatActivity(), PdfPageView {

    @InjectPresenter
    internal lateinit var presenter: PdfPagePresenter

    private lateinit var path: String
    private lateinit var adapter: ViewPagerAdapter


    companion object {
        const val filename = "FILENAME"
        const val filepath = "FILEPATH"
        fun getNewIntent(activity: Activity, data: PdfFile): Intent {
            val intent = Intent(activity, PdfPageActivity::class.java)
            intent.putExtra(filename, data.fileName)
            intent.putExtra(filepath, data.absolutePath)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        title = intent.getStringExtra(filename)
        path = intent.getStringExtra(filepath)

    }

/*    @SuppressLint("ClickableViewAccessibility")
    private fun createTempView() {
        newView.setImageResource(R.drawable.screenshot_5)
        frameRoot.addView(newView)
        val param = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        newView.layoutParams = param
        newView.x = 300F
        newView.y = 500F
        newView.adjustViewBounds = true
        newView.scaleType = ImageView.ScaleType.FIT_XY
        newView.setOnTouchListener(TouchEventListener(this, false))
    }*/

    fun getTouchMotionEvent(): MotionEvent {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis() + 50
        val x = 0.0f
        val y = 0.0f
        val metaState = 0
        return MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        )
    }

    fun saveNewPdfFile() {
//        FileUtil.saveCurrentPage(this, imgView)
    }
}