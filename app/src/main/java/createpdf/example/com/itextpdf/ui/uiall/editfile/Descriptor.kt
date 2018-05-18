package createpdf.example.com.itextpdf.ui.uiall.editfile

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.dagger.ForApplication
import createpdf.example.com.itextpdf.io.utils.FileUtil
import java.io.File
import java.io.IOException
import javax.inject.Inject


object Descriptor {

    lateinit var descriptor: ParcelFileDescriptor
    lateinit var pdfRenderer: PdfRenderer
    var curPage: PdfRenderer.Page? = null

    fun openPdfRenderer(path : String) {
        val file = File(path)
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            pdfRenderer = PdfRenderer(descriptor)
        } catch (e: Exception) {
        }
    }

    fun closePdfRenderer() {
        try {
            pdfRenderer.close()
            descriptor.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun getBitmap(index : Int, activity: Activity) : Bitmap{
        val currentZoomLevel = 12F
        curPage = pdfRenderer.openPage(index)
        val matrix = Matrix()
        val dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_DEFAULT / activity.resources.displayMetrics.densityDpi
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel)
        val bitmap = FileUtil.getBitmapFromScreen(activity, curPage!!, currentZoomLevel)
        curPage?.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }
}