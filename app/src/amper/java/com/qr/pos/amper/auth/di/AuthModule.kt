package com.qr.pos.amper.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qr.pos.amper.auth.data.remote.LoginRepoImp
import com.qr.pos.amper.auth.data.remote.RegisterRepoImp
import com.qr.pos.amper.auth.domain.repository.LoginRepo
import com.qr.pos.amper.auth.domain.repository.RegistrationRepo
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthModule {

    @Provides
    @ActivityRetainedScoped
    fun providesRegistrationRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): RegistrationRepo = RegisterRepoImp(firebaseFirestore,firebaseAuth)

    @Provides
    @ActivityRetainedScoped
    fun provideLoginRepo(
        firebaseAuth: FirebaseAuth,
        sharedPref: SharedPrefRepo,
        db: FirebaseFirestore
    ): LoginRepo = LoginRepoImp(firebaseAuth, sharedPref, db)

}