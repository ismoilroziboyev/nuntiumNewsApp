package uz.ismoilroziboyev.nuntium.di.components

import dagger.Component
import uz.ismoilroziboyev.nuntium.di.modules.DatabaseModule
import uz.ismoilroziboyev.nuntium.di.modules.NetworkModule
import uz.ismoilroziboyev.nuntium.fragments.*
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface AppComponent {

    fun injectSelectFragment(fragment: SelectTopicFragment)

    fun injectHomeFragment(fragment: HomeFragment)

    fun injectSplashFragment(fragment: SplashFragment)

    fun injectSeeAllFragment(fragment: SeeAllFragment)

    fun injectSearchFragment(fragment: SearchFragment)

    fun injectNewsViewFragment(fragment: NewsViewFragment)

    fun injectCategoriesFragment(fragment: CategoriesFragment)

    fun injectSavedFragment(fragment: SavedFragment)

    fun injectLanguageFragment(languageFragment: LanguageFragment)
}