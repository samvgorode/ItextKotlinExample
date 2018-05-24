package createpdf.example.com.itextpdf.ui.uibase.fragment

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.io.manager.OrientationManager
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @LayoutRes
    private var layout = 0

    @Inject
    lateinit var orientationManager: OrientationManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        setView()
    }

    abstract fun setView()

    fun setLayout(layout: Int) { this.layout = layout }

}