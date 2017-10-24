package ru.maxost.vk_superior_post.DI

import dagger.Module
import dagger.Provides
import ru.maxost.vk_superior_post.Data.Services.Api.ApiService
import ru.maxost.vk_superior_post.Services.Api.ProdApiService
import javax.inject.Singleton

/**
 * Created by Maxim Ostrovidov on 07.09.17.
 * dustlooped@yandex.ru
 */
@Singleton
@Module
class ApiModule {
    @Provides @Singleton
    fun apiModule(): ApiService = ProdApiService()
}