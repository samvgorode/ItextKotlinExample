package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.io.utils.Constants
import createpdf.example.com.itextpdf.ui.presenters.PdfPagePresenter
import createpdf.example.com.itextpdf.ui.uiall.adapter.ViewPagerAdapter
import createpdf.example.com.itextpdf.ui.uiall.editfile.Descriptor.closePdfRenderer
import createpdf.example.com.itextpdf.ui.uiinterfaces.PdfPageView
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.IOException
import java.util.*


class PdfPageActivity : MvpAppCompatActivity(), PdfPageView {

    @InjectPresenter
    internal lateinit var presenter: PdfPagePresenter

    private lateinit var path: String
    private lateinit var adapter: ViewPagerAdapter
    private var lastPage: Int = 0


    companion object {

        fun getNewIntent(activity: Activity, data: PdfFile): Intent {
            val intent = Intent(activity, PdfPageActivity::class.java)
            intent.putExtra(Constants.FILENAME_INTENT_EXTRA, data.fileName)
            intent.putExtra(Constants.FILEPATH_INTENT_EXTRA, data.absolutePath)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        (application as App).component.inject(this)
        title = intent.getStringExtra(Constants.FILENAME_INTENT_EXTRA)
        path = intent.getStringExtra(Constants.FILEPATH_INTENT_EXTRA)
        adapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        pager.offscreenPageLimit = 3
    }

    private fun fillAdapter() {
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(0, PdfPageFragment.newInstance(path, 0))
         fragmentList.add(1, PdfPageFragment.newInstance(path, 1))
         fragmentList.add(2, PdfPageFragment.newInstance(path, 2))
        adapter.setListFragment(fragmentList)
    }

    override fun onStart() {
        super.onStart()
        try {
            Descriptor.openPdfRenderer(path)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.mistake_encrypted), Toast.LENGTH_SHORT).show()
        }
        fillAdapter()
    }

    override fun onStop() {
        try {
            closePdfRenderer()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Descriptor.curPage?.close()
        super.onStop()
    }


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

    override fun onBackPressed() {
        super.onBackPressed()
        Descriptor.curPage?.close()
    }

    fun saveNewPdfFile() {
//        FileUtil.saveCurrentPage(this, imgView)
    }

//    fun setupButtons(index: Int) {
//        val pageCount = pdfRenderer.getPageCount()
//        btnPrevious.isEnabled = 0 != index
//        btnNext.isEnabled = index + 1 < pageCount
//    }
}