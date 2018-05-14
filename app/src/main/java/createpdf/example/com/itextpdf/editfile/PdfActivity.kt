package createpdf.example.com.itextpdf.editfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.ImageSource
import createpdf.example.com.itextpdf.PdfFile
import createpdf.example.com.itextpdf.R
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@SuppressLint("Registered")
class PdfActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var path: String
    lateinit var pdfRenderer: PdfRenderer
    lateinit var curPage: PdfRenderer.Page
    lateinit var descriptor: ParcelFileDescriptor
    private var currentZoomLevel = 5f
    private var currentPage = 0
    private val CURRENT_PAGE = "CURRENT_PAGE"
    lateinit var bitmap: Bitmap

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

    private fun setOnClickListeners() {
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        zoomout.setOnClickListener(this)
        zoomin.setOnClickListener(this)
        floatingActionButton.setOnClickListener(this)
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

    private fun displayPage(index: Int) {
        if (pdfRenderer.pageCount <= index) return
        curPage.close()
        curPage = pdfRenderer.openPage(index)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM / resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        setBitmap()
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

    fun setBitmap() {
        val newWidth = (resources.displayMetrics.widthPixels * curPage.width / 72 * currentZoomLevel / 40).toInt()
        val newHeight = (resources.displayMetrics.heightPixels * curPage.height / 72 * currentZoomLevel / 64).toInt()
        bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
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
            btnPrevious -> {
                val index = curPage.index - 1
                displayPage(index)
            }
            btnNext -> {
                val index = curPage.index + 1
                displayPage(index)
            }
            zoomout -> {
                --currentZoomLevel;
                displayPage(curPage.getIndex());
            }
            zoomin -> {
                ++currentZoomLevel;
                displayPage(curPage.getIndex());
            }
            floatingActionButton -> {
                editCurrentPage()
            }
        }
    }

    private fun editCurrentPage() {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()
        val convertHeight = height.toInt()
        val convertWidth = width.toInt()
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        var bitmap = loadBitmapFromView(imgView, imgView.getWidth(), imgView.getHeight())
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true)
        paint.setColor(Color.BLUE)
        canvas.drawBitmap(bitmap, Matrix(), paint)
        document.finishPage(page)
        val dir = File(Environment.getExternalStorageDirectory().absolutePath)
        if (dir.exists()) {
            dir.delete()
        }
        dir.mkdirs()
        val targetPdf = dir.absolutePath + "/test.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(applicationContext, "PDf сохранён в " + filePath.absolutePath,
                    Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Что-то пошло не так: " + e.toString(), Toast.LENGTH_LONG).show()
        }
        document.close()
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
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
}