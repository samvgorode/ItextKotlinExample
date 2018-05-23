package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.qoppa.android.pdf.annotations.Link
import com.qoppa.android.pdfProcess.PDFDocument
import com.qoppa.android.pdfViewer.actions.Action
import com.qoppa.android.pdfViewer.actions.URLAction
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.utils.Constants
import createpdf.example.com.itextpdf.ui.uibase.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_pdf_page.*
import java.util.*


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
        /*newView.setImageResource(R.drawable.screenshot_5)
        newView.layoutParams = ViewGroup.LayoutParams(300, 300)
        newView.x = 200F
        newView.y = 300F
        frameRoot.addView(newView)*/
        path = arguments?.getString(Constants.FILE_PATH_BUNDLE) ?: ""
        currentPage = arguments?.getInt(Constants.PAGE_INDEX_BUNDLE) ?: -1
        showQoppaView()
    }

    override fun onResume() {
        super.onResume()
        if (currentPage != -1) displayPage(currentPage)
    }

    private fun showQoppaView(){
       /*
        val viewer = QPDFNotesView(context)
        viewer.activity = activity
        viewer.loadDocument(path)
        frameRoot.addView(viewer)
        replaceLink()
        */
    }

    fun displayPage(index: Int) {
        imgView.setImageBitmap(Descriptor.getBitmap(index))
    }

    private fun setOnClickTouchListeners() {
        setTouchTopImage(buttonLink)
        setTouchTopImage(buttonCamera)
        setTouchTopImage(buttonGallery)
        buttonSavePage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
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

    private fun replaceLink(){
        try {
            val pdfDoc = PDFDocument(path, null)
            val page = pdfDoc.getPage(0)
            val annotations = pdfDoc.getPage(0).annotations
            val action = URLAction("www.qoppa.com")
            val actions = Vector<Action>()
            actions.add(action)
            if (annotations == null) {
                Log.e("", "document does not contain annotations")
            } else {
                //print current values of all textfields, set the value of "field1"
                for (i in 0 until annotations.size) {
                    val field = annotations[i]
                    if (field is Link) {
                        if(field.getActions(page)[0].actionTypeDesc == "Open a web link")
                            field.setActions(actions, page)
                    }
                }
            }
            pdfDoc.saveDocument()
        } catch (t: Throwable) {
            Log.e("", Log.getStackTraceString(t))
        }
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


}