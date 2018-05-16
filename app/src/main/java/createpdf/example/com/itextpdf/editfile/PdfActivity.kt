package createpdf.example.com.itextpdf.editfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.itextpdf.text.Image
import com.itextpdf.text.html.HtmlTags.IMG
import com.itextpdf.text.pdf.*
import createpdf.example.com.itextpdf.FileUtil
import createpdf.example.com.itextpdf.PdfFile
import createpdf.example.com.itextpdf.R
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.*


class PdfActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var path: String
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var curPage: PdfRenderer.Page
    private lateinit var descriptor: ParcelFileDescriptor
    private var currentZoomLevel = 12f
    private var currentPage = 0
    private val CURRENT_PAGE = "CURRENT_PAGE"
    private lateinit var bitmap: Bitmap
    private lateinit var newView: ImageView

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
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_DEFAULT / resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        bitmap = FileUtil.getBitmapFromScreen(this, curPage, currentZoomLevel)
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imgView.setImageBitmap(bitmap)
        setupButtons(index)
    }

    fun setupButtons(index: Int) {
        val pageCount = pdfRenderer.getPageCount()
        btnPrevious.isEnabled = 0 != index
        btnNext.isEnabled = index + 1 < pageCount
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_PAGE, curPage.getIndex())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createTempView() {
        newView = ImageView(this)
        newView.setImageResource(R.drawable.ic_zoom_in)
        frameRoot.addView(newView)
        val param = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        newView.layoutParams = param
        newView.x = 300F
        newView.y = 500F
        newView.adjustViewBounds = true
        newView.scaleType = ImageView.ScaleType.FIT_XY
        newView.setOnTouchListener(TouchEventListener(this))
        newView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
    }

    private fun changePage(toNext: Boolean) {
        val index = when {
            toNext -> curPage.index + 1
            else -> curPage.index - 1
        }
        displayPage(index)
    }

    private fun changeZoom(zoomPlus: Boolean) {
        when {
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
        buttonLink.setOnClickListener(this)
        buttonCamera.setOnClickListener(this)
        buttonGallery.setOnClickListener(this)
        buttonSavePage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPrevious -> changePage(false)
            btnNext -> changePage(true)
            buttonLink -> createTempView()
            buttonCamera -> createTempView()
            buttonGallery -> createTempView()
            buttonSavePage -> savePage()
        }
    }

    private fun savePage() {
        val reader = PdfReader(path)
        val readerCopy = PdfReader(path)
        val stamper = PdfStamper(reader, FileOutputStream(Environment.getExternalStorageDirectory().absolutePath +"/PDFFFFF.pdf"))

        val ims = assets.open("borders.png")
        val bmp = BitmapFactory.decodeStream(ims)
        val stream1 = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream1)

        val image = Image.getInstance(stream1.toByteArray())
        val stream = PdfImage(image, "", null)
        stream.put(PdfName("ITXT_SpecialId"), PdfName("123456789"))
        val ref = stamper.getWriter().addToBody(stream)
        image.directReference = ref.indirectReference
        image.setAbsolutePosition(newView.x, newView.x)
        val over = stamper.getOverContent(1)
        over.addImage(image)
        stamper.close()
        reader.close()
        rewriteFile()
    }

    fun rewriteFile(){
		val source = File(Environment.getExternalStorageDirectory().absolutePath +"/PDFFFFF.pdf")
		val dest = File(path)
        val fis = FileInputStream(source)
        dest.copyInputStreamToFile(fis)
        fis.close()
	}

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    fun getBytesFromDrawable(d: Drawable): ByteArray {
        val bitmap : Bitmap= Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun saveNewPdfFile() {
        FileUtil.saveCurrentPage(this, imgView)
    }
}