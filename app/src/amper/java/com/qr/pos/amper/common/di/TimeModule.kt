package com.qr.pos.amper.common.di

import com.qr.pos.amper.common.data.remote.ServerTimeService
import com.qr.pos.amper.common.data.repository.ServerTimeRepoImp
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.domain.repository.TimeRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeModule {

    @Provides
    @Singleton
    fun providesTimeService(
        retrofit: Retrofit
    ): ServerTimeService = retrofit.create()

    @Provides
    @Singleton
    fun providesServerTimeRepo(
        service: ServerTimeService,
        sf: SharedPrefRepo
    ): TimeRepo = ServerTimeRepoImp(service, sf)
}