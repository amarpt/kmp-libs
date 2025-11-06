package io.github.postapi.di

import io.github.postapi.domain.model.PostRepository
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import kotlin.reflect.KClass

object KoinManager : KoinComponent {
    lateinit var koinInstance: Koin
    fun getPostRepository(): PostRepository = get()
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    val koinApplication = startKoin {
        appDeclaration()
        modules(commonModule + platformModule())
    }
    KoinManager.koinInstance = koinApplication.koin
}

// Call it when main application is not using koin in android and ios Use as default
fun initDefaultKoin() = initKoin()

//  Extension for access koin
inline fun <reified T : Any> inject() = KoinManager.koinInstance.get<T>()

fun <T : Any> Koin.getDependency(clazz: KClass<T>): T {
    return get(clazz)
}