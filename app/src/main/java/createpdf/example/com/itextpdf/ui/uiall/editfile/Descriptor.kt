package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
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

    /**
     * currentZoomLevel causes slow work if it is big
     * */
    fun getBitmap(index: Int, activity: Activity): Bitmap {
        val currentZoomLevel = 5F
        curPage.close()
        curPage = pdfRenderer.openPage(index)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_DEFAULT / activity.resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        val bitmap = FileUtil.getBitmapFromScreen(activity, curPage!!, currentZoomLevel)
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }
}