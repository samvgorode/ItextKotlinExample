package createpdf.example.com.itextpdf

import com.arellomobile.mvp.MvpApplication
import createpdf.example.com.itextpdf.dagger.AppComponent
import createpdf.example.com.itextpdf.dagger.AppModule
import createpdf.example.com.itextpdf.dagger.DaggerAppComponent


class App : MvpApplication() {


    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component.inject(this)
    }

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var component: AppComponent
    }

}