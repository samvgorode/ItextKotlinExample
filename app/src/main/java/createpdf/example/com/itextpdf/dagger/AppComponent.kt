package createpdf.example.com.itextpdf.dagger

import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.ui.uiall.MainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(app: App?)
    fun inject(activity: MainActivity?)
}