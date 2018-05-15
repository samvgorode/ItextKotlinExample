package createpdf.example.com.itextpdf.editfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import createpdf.example.com.itextpdf.FileUtil
import createpdf.example.com.itextpdf.PdfFile
import createpdf.example.com.itextpdf.R
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File
import java.io.IOException


class PdfActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var path: String
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var curPage: PdfRenderer.Page
    private lateinit var descriptor: ParcelFileDescriptor
    private var currentZoomLevel = 5f
    private var currentPage = 0
    private val CURRENT_PAGE = "CURRENT_PAGE"
    private lateinit var bitmap: Bitmap

    companion object {
        const val filename = "FILENAME"
        const val filepath = "FILEPATH"
        fun getNewIntent(activity: Activity, data: PdfFile): Intent {
            val intent = Intent(activity, PdfActivity::class.java)
            intent.putExtra(filename, data.fileName)
            intent.putExtra(filepath, data.absolutePath)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(filename)
        path = intent.getStringExtra(filepath)
        currentPage = savedInstanceState?.getInt(CURRENT_PAGE, 0) ?: 0
        setOnClickListeners()
    }

    private fun displayPage(index: Int) {
        if (pdfRenderer.pageCount <= index) return
        curPage.close()
        curPage = pdfRenderer.openPage(index)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM / resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        bitmap = FileUtil.getBitmapFromScreen(this, curPage, currentZoomLevel)
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imgView.setImage(ImageSource.bitmap(bitmap))
        setupButtons(index)
    }

    fun setupButtons(index: Int) {
        val pageCount = pdfRenderer.getPageCount()
        btnPrevious.isEnabled = 0 != index
        btnNext.isEnabled = index + 1 < pageCount
        zoomout.isEnabled = currentZoomLevel != 2f
        zoomin.isEnabled = currentZoomLevel != 12f
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_PAGE, curPage.getIndex())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPrevious -> changePage(false)
            btnNext -> changePage(true)
            zoomout -> changeZoom(false)
            zoomin -> changeZoom(true)
            floatingActionButton -> {
//                FileUtil.saveCurrentPage(this, imgView)
                createTempImage()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createTempImage() {
        val newView = ImageView(this)
        frameRoot.addView(newView)
        newView.layoutParams.height = 200
        newView.layoutParams.width = 200
        newView.x = 300F
        newView.y = 500F
        newView.setBackgroundColor(Color.MAGENTA)
        newView.setOnTouchListener(TouchEventListener(frameRoot))
    }

    private fun changePage(toNext : Boolean){
        val index = when {
            toNext -> curPage.index + 1
            else -> curPage.index - 1
        }
        displayPage(index)
    }

    private fun changeZoom(zoomPlus: Boolean){
        when{
            zoomPlus -> ++currentZoomLevel
            else -> --currentZoomLevel
        }
        displayPage(curPage.index)
    }

    public override fun onStart() {
        super.onStart()
        try {
            openPdfRenderer()
            displayPage(currentPage)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.mistake_encrypted), Toast.LENGTH_SHORT).show()
        }
    }


    public override fun onStop() {
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
            curPage = pdfRenderer.openPage(currentPage)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.mistake), Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun closePdfRenderer() {
        curPage.close()
        pdfRenderer.close()
        descriptor.close()
    }

    private fun setOnClickListeners() {
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        zoomout.setOnClickListener(this)
        zoomin.setOnClickListener(this)
        floatingActionButton.setOnClickListener(this)
    }
}