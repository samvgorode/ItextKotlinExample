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
import createpdf.example.com.itextpdf.ui.uibase.fragment.BaseFragment.FragmentLifecycle
import android.support.v4.view.ViewPager.OnPageChangeListener




class PdfPageActivity : MvpAppCompatActivity(), PdfPageView {

    @InjectPresenter
    internal lateinit var presenter: PdfPagePresenter

    private lateinit var path: String
    private lateinit var adapter: ViewPagerAdapter
    private var pageCount: Int = 0


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
        App.component.inject(this)
        title = intent.getStringExtra(Constants.FILENAME_INTENT_EXTRA)
        path = intent.getStringExtra(Constants.FILEPATH_INTENT_EXTRA)
        adapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        pager.offscreenPageLimit = 0
        pager.addOnPageChangeListener(pageChangeListener)
    }

    private fun fillAdapter() {
        val fragmentList = ArrayList<Fragment>()
        // there will be null
           for (i in 0 until  pageCount) {
               fragmentList.add(i, PdfPageFragment.newInstance(path, i))
       }

        adapter.setListFragment(fragmentList)
    }

    override fun onStart() {
        super.onStart()
        try {
            if(Descriptor.openPdfRenderer(path)){
                pageCount = Descriptor.pdfRenderer.pageCount
                fillAdapter()
            }
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

    override fun onBackPressed() {
        finish()
    }

    private val pageChangeListener = object : OnPageChangeListener {

        internal var currentPosition = 0

        override fun onPageSelected(newPosition: Int) {

            val fragmentToShow = adapter.getItem(newPosition) as PdfPageFragment
            fragmentToShow.onResumeFragment()

            val fragmentToHide = adapter.getItem(currentPosition) as PdfPageFragment
            fragmentToHide.onPauseFragment()

            currentPosition = newPosition
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }
}