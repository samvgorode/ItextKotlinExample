package createpdf.example.com.itextpdf.dagger

import android.os.ParcelFileDescriptor
import createpdf.example.com.itextpdf.App
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named
import javax.inject.Singleton


@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    @ForApplication
    fun provideApp(): App = app
}