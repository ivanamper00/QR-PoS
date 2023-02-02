package com.qr.pos.amper.inventory.di

import com.google.firebase.firestore.FirebaseFirestore
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.inventory.data.repository.InventoryRepoImp
import com.qr.pos.amper.inventory.domain.repository.InventoryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object InventoryModule {

    @Provides
    @ActivityRetainedScoped
    fun providesInventoryRepo(
        db: FirebaseFirestore,
        sf: SharedPrefRepo
    ): InventoryRepo = InventoryRepoImp(db, sf)
}