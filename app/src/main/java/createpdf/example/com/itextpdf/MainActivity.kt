package createpdf.example.com.itextpdf

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import createpdf.example.com.itextpdf.adapter.Adapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 101
    private val WRITE_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val CAMERA = android.Manifest.permission.CAMERA
    lateinit var list: ArrayList<PdfFile>
    var adapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
    }

    fun checkPermissions() {
        if (!permissionsGranted(WRITE_STORAGE) || !permissionsGranted(CAMERA) || !permissionsGranted(READ_STORAGE)) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_STORAGE, CAMERA), REQUEST_PERMISSION)
        } else {
            initRecycler()
            getAllPdfFiles()
        }
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getAllPdfFiles() {
        list = FileUtil.getList(Environment.getExternalStorageDirectory().absolutePath)
        if(adapter==null){
            adapter = Adapter(list)
            recyclerView.adapter = adapter
        } else {
            adapter?.updateAdapter(list)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private fun permissionsGranted(permisssion: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permisssion) == PackageManager.PERMISSION_GRANTED
    }
}
