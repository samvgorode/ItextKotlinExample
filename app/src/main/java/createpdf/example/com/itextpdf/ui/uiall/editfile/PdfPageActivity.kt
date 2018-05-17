package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.io.utils.Constants
import createpdf.example.com.itextpdf.ui.presenters.PdfPagePresenter
import createpdf.example.com.itextpdf.ui.uiall.adapter.ViewPagerAdapter
import createpdf.example.com.itextpdf.ui.uiinterfaces.PdfPageView
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File
import java.io.IOException
import java.util.*


class PdfPageActivity : MvpAppCompatActivity(), PdfPageView {

    @InjectPresenter
    internal lateinit var presenter: PdfPagePresenter

    private lateinit var path: String
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var descriptor: ParcelFileDescriptor
    private lateinit var pdfRenderer: PdfRenderer


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
        title = intent.getStringExtra(Constants.FILENAME_INTENT_EXTRA)
        path = intent.getStringExtra(Constants.FILEPATH_INTENT_EXTRA)
        adapter = ViewPagerAdapter(supportFragmentManager)
        fillAdapter()
        pager.adapter = adapter
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
            openPdfRenderer()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.mistake_encrypted), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        try {
            closePdfRenderer()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onStop()
    }

    private fun openPdfRenderer() {
        val file = File(path)
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            pdfRenderer = PdfRenderer(descriptor)

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.mistake), Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun closePdfRenderer() {
        pdfRenderer.close()
        descriptor.close()
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

    fun saveNewPdfFile() {
//        FileUtil.saveCurrentPage(this, imgView)
    }

//    fun setupButtons(index: Int) {
//        val pageCount = pdfRenderer.getPageCount()
//        btnPrevious.isEnabled = 0 != index
//        btnNext.isEnabled = index + 1 < pageCount
//    }
}