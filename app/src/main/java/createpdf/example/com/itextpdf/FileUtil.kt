package createpdf.example.com.itextpdf

import android.app.Activity
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileUtil {

    private val list = ArrayList<PdfFile>()
    fun getListPdfFiles(absolutePath: String): ArrayList<PdfFile> {
        try {
            val file = File(absolutePath)
            val fileList = file.listFiles()
            var fileName: String
            for (f in fileList) {
                if (f.isDirectory) {
                    getListPdfFiles(f.absolutePath)
                } else {
                    fileName = f.name.toString()
                    if (fileName.endsWith(".pdf")) {
                        list.add(PdfFile(fileName, f.absolutePath))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveCurrentPage(context: Activity, view: View) {
        val displaymetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displaymetrics)
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
        var bitmap = loadBitmapFromView(view, view.width, view.height)
        bitmap = Bitmap.createScaledBitmap(
                bitmap,
                convertWidth,
                convertHeight,
                true
        )
        paint.color = Color.BLUE
        canvas.drawBitmap(
                bitmap,
                Matrix(),
                paint
        )
        document.finishPage(page)
        val dir = File(Environment.getExternalStorageDirectory().absolutePath)
        val targetPdf = dir.absolutePath + "/test.pdf"
        val filePath = File(targetPdf)
        if (filePath.exists()) {
            filePath.delete()
        }
        filePath.mkdirs()
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(context, "PDf сохранён в " + filePath.absolutePath, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.error_saving_string) + e.toString(), Toast.LENGTH_LONG).show()
        }
        document.close()
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    fun getBitmapFromScreen(context: Activity, curPage: PdfRenderer.Page, currentZoomLevel: Float): Bitmap {
        val newWidth = (context.resources.displayMetrics.widthPixels * curPage.width / 72 * currentZoomLevel / 40).toInt()
        val newHeight = (context.resources.displayMetrics.heightPixels * curPage.height / 72 * currentZoomLevel / 64).toInt()
        return Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    }
}