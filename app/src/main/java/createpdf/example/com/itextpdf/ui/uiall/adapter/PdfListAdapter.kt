package createpdf.example.com.itextpdf.ui.uiall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import createpdf.example.com.itextpdf.ui.uibase.viewholders.BaseViewHolder
import createpdf.example.com.itextpdf.io.pojo.PdfFile
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.ui.uibase.adapters.BaseRecyclerAdapter
import createpdf.example.com.itextpdf.ui.uiall.viewholder.ViewHolder


class PdfListAdapter : BaseRecyclerAdapter<BaseViewHolder<PdfFile>>() {

     var list : ArrayList<PdfFile>? = null

    override fun onBindViewHolder(holder: BaseViewHolder<PdfFile>, position: Int) {
        list?.get(position)?.let { holder.bindData(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PdfFile> {
        return ViewHolder(parent, LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list?.size?:0
    }

    fun updateAdapter(list: ArrayList<PdfFile>){
        this.list?.clear()
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return list?.get(position)?.fileName?.hashCode()?.toLong()?:0
    }
}