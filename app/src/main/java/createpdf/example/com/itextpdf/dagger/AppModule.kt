package createpdf.example.com.itextpdf.dagger

import createpdf.example.com.itextpdf.App
import createpdf.example.com.itextpdf.io.manager.OrientationManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    @ForApplication
    fun provideApp(): App = app

    @Provides
    @Singleton
    fun provideOrientationManager() = OrientationManager(app.applicationContext)
}