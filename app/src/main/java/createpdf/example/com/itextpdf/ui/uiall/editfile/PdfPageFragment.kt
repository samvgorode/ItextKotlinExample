package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfImage
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.utils.Constants
import createpdf.example.com.itextpdf.io.utils.FileUtil
import createpdf.example.com.itextpdf.ui.uibase.fragment.BaseFragment
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.android.synthetic.main.fragment_pdf_page.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


class PdfPageFragment : BaseFragment(), View.OnClickListener {


    private lateinit var curPage: PdfRenderer.Page

    private var currentZoomLevel = 12f
    private var currentPage = 0

    private lateinit var bitmap: Bitmap
    private val tempPdfPath = Environment.getExternalStorageDirectory().absolutePath + "/tempPdfFile.pdf"
    private lateinit var newView: ImageView
    private lateinit var path: String

    companion object {
        fun newInstance(path: String, index: Int): PdfPageFragment {
            val fragment = PdfPageFragment()
            val bundle = Bundle()
            bundle.putString(Constants.FILE_PATH_BUNDLE, path)
            bundle.putInt(Constants.PAGE_INDEX_BUNDLE, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.fragment_pdf_page)
        currentPage = savedInstanceState?.getInt(Constants.CURRENT_PAGE, 0) ?: 0
    }

    override fun setView() {
        setOnClickTouchListeners()
        newView = ImageView(context)
        path = arguments?.getString(Constants.FILE_PATH_BUNDLE) ?: ""
    }

    override fun onStart() {
        super.onStart()
        displayPage(currentPage)
//        curPage = pdfRenderer.openPage(currentPage)
        displayPage(currentPage)
    }

    override fun onStop() {
        curPage.close()
        super.onStop()
    }

    private fun displayPage(index: Int) {
//        if (pdfRenderer.pageCount <= index) return
        curPage.close()
//        curPage = pdfRenderer.openPage(index)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_DEFAULT / resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        bitmap = FileUtil.getBitmapFromScreen(activity!!, curPage, currentZoomLevel)
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imgView.setImageBitmap(bitmap)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.CURRENT_PAGE, curPage.index)
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

    private fun addDataToPdf() {
        val reader = PdfReader(path)
        val stamper = PdfStamper(reader, FileOutputStream(tempPdfPath))
        val bitmapDrawable = newView.drawable as BitmapDrawable
        val stream1 = ByteArrayOutputStream()
        val bmp = bitmapDrawable.bitmap
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream1)
        val image = Image.getInstance(stream1.toByteArray())
        val stream = PdfImage(image, "", null)
        stream.put(PdfName("ITXT_SpecialId"), PdfName("123456789"))
        val ref = stamper.writer.addToBody(stream)
        image.directReference = ref.indirectReference
        image.setAbsolutePosition(0F, 0F)
        image.scaleAbsolute(newView.width * newView.scaleX, newView.height * newView.scaleY)
        val over = stamper.getOverContent(curPage.index)
        over.addImage(image)
        stamper.close()
        reader.close()
        FileUtil.rewriteFile(tempPdfPath, path)
    }

    private fun setOnClickTouchListeners() {
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        setTouchTopImage(buttonLink)
        setTouchTopImage(buttonCamera)
        setTouchTopImage(buttonGallery)
        buttonSavePage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPrevious -> changePage(false)
            btnNext -> changePage(true)
            buttonSavePage -> addDataToPdf()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchTopImage(image: ImageView) {
        buttonLink.post { duplicateView(buttonLink) }
        buttonCamera.post { duplicateView(buttonCamera) }
        buttonGallery.post { duplicateView(buttonGallery) }
        image.setOnTouchListener({ v, event ->
            if (event.action == MotionEvent.ACTION_MOVE)
                when (v) {
                    buttonLink -> buttonLink.post { duplicateView(buttonLink) }
                    buttonCamera -> buttonCamera.post { duplicateView(buttonCamera) }
                    buttonGallery -> buttonGallery.post { duplicateView(buttonGallery) }
                }
            true
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun duplicateView(imageView: ImageView?) {
        val image = ImageView(context)
        image.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorGray))
        image.setImageDrawable(imageView?.drawable)
        image.layoutParams = FrameLayout.LayoutParams(imageView?.width ?: 32, imageView?.height
                ?: 32)
        image.y = imageView?.y ?: 0F
        image.x = imageView?.x ?: 0F
        frameRoot.addView(image)
//        this is to scale
//        image.adjustViewBounds = true
//        image.scaleType = ImageView.ScaleType.FIT_XY
        image.setOnTouchListener(TouchEventListener(context!!, false))
    }

}