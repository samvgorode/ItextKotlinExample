package createpdf.example.com.itextpdf.ui.uibase.adapters

import android.support.v7.widget.RecyclerView
import createpdf.example.com.itextpdf.ui.uibase.viewholders.BaseViewHolder
import createpdf.example.com.itextpdf.io.pojo.PdfFile


abstract class BaseRecyclerAdapter<T : BaseViewHolder<PdfFile>> : RecyclerView.Adapter<T>() {init {
    this.setHasStableIds(true)
}
}