package createpdf.example.com.itextpdf.io.utils

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import java.io.*
import java.util.*
import android.opengl.ETC1.getHeight
import java.nio.file.Files.size
import android.graphics.Bitmap




object FileUtil {

    private val list = ArrayList<PdfFile>()

    fun clearList(){
        list.clear()
    }

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

    fun getBitmapFromScreen(curPage: PdfRenderer.Page): Bitmap {
        return Bitmap.createBitmap(curPage.width, curPage.height, Bitmap.Config.ARGB_8888)
    }

    fun rewriteFile(sourceStr: String, destStr: String) {
        val source = File(sourceStr)
        val dest = File(destStr)
        val fis = FileInputStream(source)
        dest.copyInputStreamToFile(fis)
        source.delete()
        fis.close()
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    fun combineImageIntoOne(bitmap: ArrayList<Bitmap>): Bitmap {
        var w = 0
        var h = 0
        for (i in 0 until bitmap.size) {
            w += bitmap[i].width
            h = bitmap[i].height
        }
        val temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(temp)
        var top = 0f
        var left = 0f
        for (i in 0 until bitmap.size) {
            canvas.drawBitmap(bitmap[i], left, top, null)
            left += bitmap[i].width
        }
        return temp
    }
}