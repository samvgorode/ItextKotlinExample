package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import createpdf.example.com.itextpdf.io.utils.FileUtil
import java.io.File
import java.io.IOException


object Descriptor {

    lateinit var descriptor: ParcelFileDescriptor
    lateinit var pdfRenderer: PdfRenderer
    lateinit var curPage: PdfRenderer.Page
    var descriptorCreated: Boolean = false


    fun openPdfRenderer(path: String): Boolean {
        val file = File(path)
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            pdfRenderer = PdfRenderer(descriptor)
            curPage = pdfRenderer.openPage(0)
            descriptorCreated = true
            return true
        } catch (e: Exception) {
            Log.e("", e.toString())
            descriptorCreated = false
            val source = File(path)
            source.delete()
            return false
        }
    }

    fun closePdfRenderer() {
        if (descriptorCreated) {
            try {
                curPage.close()
                pdfRenderer.close()
                descriptor.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getBitmap(index: Int): Bitmap {
        curPage.close()
        curPage = pdfRenderer.openPage(index)
        val bitmap = FileUtil.getBitmapFromScreen(curPage)
        curPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }
}