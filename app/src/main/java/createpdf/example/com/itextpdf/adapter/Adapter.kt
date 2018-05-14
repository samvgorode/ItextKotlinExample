package createpdf.example.com.itextpdf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import createpdf.example.com.itextpdf.viewholder.BaseViewHolder
import createpdf.example.com.itextpdf.PdfFile
import createpdf.example.com.itextpdf.R
import createpdf.example.com.itextpdf.viewholder.ViewHolder


class Adapter(var list: ArrayList<PdfFile>) : BaseAdapter<BaseViewHolder<PdfFile>>() {

    override fun onBindViewHolder(holder: BaseViewHolder<PdfFile>, position: Int) {
        holder.bindData(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PdfFile> {
        return ViewHolder(parent, LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateAdapter(list: ArrayList<PdfFile>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return list[position].fileName.hashCode().toLong()
    }
}