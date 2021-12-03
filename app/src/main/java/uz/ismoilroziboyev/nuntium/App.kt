package uz.ismoilroziboyev.nuntium

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yariksoffice.lingver.Lingver
import uz.ismoilroziboyev.nuntium.di.components.AppComponent
import uz.ismoilroziboyev.nuntium.di.components.DaggerAppComponent
import uz.ismoilroziboyev.nuntium.di.modules.DatabaseModule
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }


    private lateinit var mySharedPreferences: MySharedPreferences

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().databaseModule(DatabaseModule(this)).build()
        mySharedPreferences = MySharedPreferences.getInstence(this)

        if (mySharedPreferences.isLightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        Lingver.init(this, "en")

    }
}