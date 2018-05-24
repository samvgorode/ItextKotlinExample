package createpdf.example.com.itextpdf

import com.arellomobile.mvp.MvpApplication
import createpdf.example.com.itextpdf.dagger.AppComponent
import createpdf.example.com.itextpdf.dagger.AppModule
import createpdf.example.com.itextpdf.dagger.DaggerAppComponent
import javax.inject.Inject

class App : MvpApplication() {

    companion object {
        @JvmStatic lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component.inject(this)
    }

}