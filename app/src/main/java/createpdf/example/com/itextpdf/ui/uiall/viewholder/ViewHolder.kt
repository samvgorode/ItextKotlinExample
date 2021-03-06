package createpdf.example.com.itextpdf.ui.uiall.viewholder

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.ui.uibase.viewholders.BaseViewHolder
import createpdf.example.com.itextpdf.ui.uiall.editfile.PdfPageActivity

class ViewHolder(parent: ViewGroup, itemView: View?) : BaseViewHolder<PdfFile>(parent, itemView) {

    @SuppressLint("SetTextI18n")
    override fun bindData(data: PdfFile) {
        val textView = itemView.findViewById<TextView>(R.id.txtFileName)
        textView.text = data.fileName + data.absolutePath
        itemView.findViewById<TextView>(R.id.btnEditFile).setOnClickListener { editFileAction(data) }
    }

    fun editFileAction(data: PdfFile) {
        (getContext() as Activity).startActivity(PdfPageActivity.getNewIntent(getContext() as Activity, data))
    }
}