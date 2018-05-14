package createpdf.example.com.itextpdf.editfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import createpdf.example.com.itextpdf.PdfFile
import createpdf.example.com.itextpdf.R
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File


@SuppressLint("Registered")
class PdfActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var path: String
    lateinit var pdfRenderer: PdfRenderer
    lateinit var curPage: PdfRenderer.Page
    lateinit var descriptor: ParcelFileDescriptor
    private val currentZoomLevel = 5f
    private var currentPage = 0
    private val CURRENT_PAGE = "CURRENT_PAGE"

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        title = intent.getStringExtra(filename)
        path = intent.getStringExtra(filepath)
        currentPage = savedInstanceState?.getInt(CURRENT_PAGE, 0) ?: 0
    }

    public override fun onStart() {
        super.onStart()
        try {
            openPdfRenderer()
            displayPage(currentPage)
        } catch (e: Exception) {
            Toast.makeText(this, "PDF-файл защищен паролем.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPdfRenderer() {
        val file = File(path)
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(descriptor)
            curPage = pdfRenderer.openPage(currentPage)
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayPage(index: Int) {
        if (pdfRenderer.pageCount <= index) return
        curPage.close()
        curPage = pdfRenderer.openPage(index)
        // определяем размеры Bitmap
        val newWidth = (resources.displayMetrics.widthPixels * curPage.width / 72 * currentZoomLevel / 40).toInt()
        val newHeight = (resources.displayMetrics.heightPixels * curPage.height / 72 * currentZoomLevel / 64).toInt()
        val bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM / resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        // отображаем результат рендера
        imgView.setImageBitmap(bitmap)
        // проверяем, нужно ли делать кнопки недоступными
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

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }
}