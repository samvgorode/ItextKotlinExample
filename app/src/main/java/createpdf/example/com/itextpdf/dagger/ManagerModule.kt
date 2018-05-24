package createpdf.example.com.itextpdf.dagger

import android.content.Context
import createpdf.example.com.itextpdf.io.manager.OrientationManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ManagerModule {

    @Provides
    @Singleton
    fun provideOrientationManager(context: Context) = OrientationManager(context)
}