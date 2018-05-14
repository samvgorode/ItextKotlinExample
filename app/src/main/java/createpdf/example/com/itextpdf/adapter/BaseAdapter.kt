package createpdf.example.com.itextpdf.adapter

import android.support.v7.widget.RecyclerView
import createpdf.example.com.itextpdf.viewholder.BaseViewHolder
import createpdf.example.com.itextpdf.PdfFile


abstract class BaseAdapter<T : BaseViewHolder<PdfFile>> : RecyclerView.Adapter<T>() {init {
    this.setHasStableIds(true)
}
}