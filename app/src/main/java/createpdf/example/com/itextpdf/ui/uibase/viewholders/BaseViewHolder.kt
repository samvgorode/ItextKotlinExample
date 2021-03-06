package createpdf.example.com.itextpdf.ui.uibase.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class BaseViewHolder<T>(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    private var context: Context? = null
    lateinit var recyclerView: RecyclerView

    constructor(parent: ViewGroup, itemView: View?) : this(itemView){
        recyclerView = parent as RecyclerView
        context = itemView?.context
    }

    fun notifyDataSetChanged() {
        if (recyclerView.adapter != null) {
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <B : RecyclerView.Adapter<*>> getAdapter(type: Class<B>): B? {
        return if (recyclerView.adapter != null && type.isInstance(recyclerView.adapter)) {
            recyclerView.adapter as B
        } else {
            null
        }
    }


    protected fun getContext(): Context? {
        return context
    }

    abstract fun bindData(data: T)
}