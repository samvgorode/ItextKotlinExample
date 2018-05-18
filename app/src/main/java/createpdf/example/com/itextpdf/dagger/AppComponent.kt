package createpdf.example.com.itextpdf.dagger

import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.ui.uiall.MainActivity
import createpdf.example.com.itextpdf.ui.uiall.editfile.PdfPageActivity
import createpdf.example.com.itextpdf.ui.uiall.editfile.PdfPageFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(activity: PdfPageActivity)
}