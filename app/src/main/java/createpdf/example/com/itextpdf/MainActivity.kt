package createpdf.example.com.itextpdf

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 101
    lateinit var newFile: File
    lateinit var pdfContent: String
    lateinit var mSaveButton: Button
    lateinit var mSubjectEditText: EditText
    lateinit var mBodyEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSaveButton = this.findViewById(R.id.button_save)
        mSubjectEditText = this.findViewById(R.id.edit_text_subject)
        mBodyEditText = this.findViewById(R.id.edit_text_body)
    }

    fun savePdf(v: View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            write()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                write()
            }
        }
    }

    @Throws(DocumentException::class, IOException::class)
    private fun write() {
        val pdfFolder = File(Environment.getExternalStorageDirectory(), "pdfdemo.pdf")
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir()
            Log.i("", "Pdf Directory created")
        }
        val date = Date()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(date)
        val myFile = File("$pdfFolder$timeStamp.pdf")
        val output = FileOutputStream(myFile)
        val document = Document()
        PdfWriter.getInstance(document, output)
        document.open()
        document.add(Paragraph(mSubjectEditText.getText().toString()))
        document.add(Paragraph(mBodyEditText.getText().toString()))
        val bytes = application.assets.open("borders.png").readBytes(4000)
        val image = Image.getInstance(bytes)
        document.add(image)
        document.close()
        output.close()
        viewPdf(myFile)
    }

    private fun viewPdf(myFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
}
