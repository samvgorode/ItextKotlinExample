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
    lateinit var curPage1: PdfRenderer.Page
    var descriptorCreated: Boolean = false


    fun openPdfRenderer(path: String): Boolean {
        val file = File(path)
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            pdfRenderer = PdfRenderer(descriptor)
//            curPage = pdfRenderer.openPage(0)
//            curPage1 = pdfRenderer.openPage(1)
            descriptorCreated = true
            return true
        } catch (e: Exception) {
            Log.e("", e.toString())
            descriptorCreated = false
            return false
        }
    }

    fun closePdfRenderer() {
        if (descriptorCreated) {
            try {
                curPage.close()
                curPage1.close()
                pdfRenderer.close()
                descriptor.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getBitmap(index: Int): Bitmap {
        curPage = pdfRenderer.openPage(index)
        val bitmap = FileUtil.getBitmapFromScreen(curPage)
        curPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        curPage.close()
        return bitmap
    }

    fun getTwoBitmaps(index: Int): List<Bitmap> {
        var list = ArrayList<Bitmap>()
        curPage = pdfRenderer.openPage(index)
        val bitmap = FileUtil.getBitmapFromScreen(curPage)
        curPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        curPage.close()
        var index1 = 0
        if(index == 0) index1 = 1
        else {
            if(index%2==0) index1 = index - 1
            else index1 = index + 1
        }
        curPage1 = pdfRenderer.openPage(index1)
        val bitmap1 = FileUtil.getBitmapFromScreen(curPage1)
        curPage1.render(bitmap1, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        curPage1.close()
        list.add(0, bitmap)
        list.add(1, bitmap1)
        return list
    }
}