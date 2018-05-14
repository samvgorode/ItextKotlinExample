package createpdf.example.com.itextpdf

import java.io.File


object FileUtil {

    fun getList(absolutePath: String): ArrayList<PdfFile> {
        val list = ArrayList<PdfFile>()
        try {
            val file = File(absolutePath)
            val fileList = file.listFiles()
            var fileName: String
            for (f in fileList) {
                if (f.isDirectory()) {
                    getList(f.absolutePath)
                } else {
                    fileName = f.getName().toString()
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
}