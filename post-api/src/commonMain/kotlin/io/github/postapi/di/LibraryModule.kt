package io.github.postapi.di

import io.github.postapi.data.remote.PostApiClient
import io.github.postapi.data.remote.PostApiClientImpl
import io.github.postapi.data.repository.PostRepositoryImpl
import io.github.postapi.data.storage.PostStorage
import io.github.postapi.data.storage.PostStorageImpl
import io.github.postapi.domain.model.PostRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule = module {
    single<PostApiClient> { PostApiClientImpl(get()) }
    single<PostStorage> { PostStorageImpl() }
    single<PostRepository> { PostRepositoryImpl(get(), get()) }
}

expect fun platformModule(): Module