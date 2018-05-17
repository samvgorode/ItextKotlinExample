package createpdf.example.com.itextpdf.dagger

import android.content.Context
import createpdf.example.com.itextpdf.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(val app: App) {
    @Provides
    @Singleton
    @ForApplication
    fun provideApp(): Context = app
}