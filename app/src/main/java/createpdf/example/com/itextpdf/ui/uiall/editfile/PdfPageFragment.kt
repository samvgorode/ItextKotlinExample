package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import createpdf.example.com.itextpdf.ui.uiall.editfile.Descriptor.pdfRenderer
import createpdf.example.com.itextpdf.ui.uibase.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_pdf_page.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


class PdfPageFragment : BaseFragment(), View.OnClickListener {

    private var currentPage = 0
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
    }

    override fun setView() {
        setOnClickTouchListeners()
        newView = ImageView(context)
        newView.setImageResource(R.drawable.screenshot_5)
        newView.layoutParams = ViewGroup.LayoutParams(300, 300)
        newView.x = 200F
        newView.y = 300F
        frameRoot.addView(newView)
        path = arguments?.getString(Constants.FILE_PATH_BUNDLE) ?: ""
        currentPage = arguments?.getInt(Constants.PAGE_INDEX_BUNDLE) ?: -1

    }

    override fun onResume() {
        super.onResume()
       if(currentPage!=-1) displayPage(currentPage)
    }

    fun displayPage(index: Int) {
        imgView.setImageBitmap(Descriptor.getBitmap(index, activity as Activity))
    }

    private fun addDataToPdf() {
       /* val reader = PdfReader(path)
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
        val over = stamper.getOverContent(currentPage)
        over.addImage(image)
        stamper.close()
        reader.close()
        FileUtil.rewriteFile(tempPdfPath, path)*/
    }

    private fun setOnClickTouchListeners() {
        setTouchTopImage(buttonLink)
        setTouchTopImage(buttonCamera)
        setTouchTopImage(buttonGallery)
        buttonSavePage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
//            btnPrevious -> changePage(false)
//            btnNext -> changePage(true)
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
        image.id = View.generateViewId()
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