package createpdf.example.com.itextpdf.ui.uiall

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatActivity
import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.dagger.ForApplication
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.io.utils.FileUtil
import createpdf.example.com.itextpdf.ui.uiall.adapter.PdfListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MvpAppCompatActivity() {

    @Inject
    @field:ForApplication
    lateinit var app: App

    private val REQUEST_PERMISSION = 101
    private val WRITE_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val CAMERA = android.Manifest.permission.CAMERA
    var list: ArrayList<PdfFile>? = null
    var pdfListAdapter: PdfListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        App.component.inject(this)
    }

    private fun checkPermissions() {
        if (!permissionsGranted(WRITE_STORAGE) || !permissionsGranted(CAMERA) || !permissionsGranted(READ_STORAGE)) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_STORAGE, CAMERA), REQUEST_PERMISSION)
        } else {
            fillRecyclerView()
        }
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
    }

    private fun getAllPdfFiles() {
        list?.clear()
        FileUtil.clearList()
        list = FileUtil.getListPdfFiles(Environment.getExternalStorageDirectory().absolutePath)
        if (pdfListAdapter == null) {
            pdfListAdapter = PdfListAdapter()
            pdfListAdapter?.updateAdapter(list!!)
            recyclerView.adapter = pdfListAdapter
        } else {
            pdfListAdapter?.updateAdapter(list!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fillRecyclerView()
            }
        }
    }

    private fun fillRecyclerView() {
        initRecycler()
        getAllPdfFiles()
    }

    private fun permissionsGranted(permisssion: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permisssion) == PackageManager.PERMISSION_GRANTED
    }
}
