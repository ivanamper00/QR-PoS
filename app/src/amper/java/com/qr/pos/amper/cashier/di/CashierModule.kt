package com.qr.pos.amper.cashier.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.qr.pos.amper.cashier.data.repository.CashierRepoImp
import com.qr.pos.amper.cashier.domain.repository.CashierRepo
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object CashierModule {

    @Provides
    @ActivityRetainedScoped
    fun providesBarcodeScanningOptions(): BarcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    @Provides
    @ActivityRetainedScoped
    fun providesBarcodeScanningClient(
        options: BarcodeScannerOptions
    ): BarcodeScanner = BarcodeScanning.getClient(options)

    @Provides
    @ActivityRetainedScoped
    fun providesCashierRepo(
        db: FirebaseFirestore,
        sf: SharedPrefRepo
    ): CashierRepo = CashierRepoImp(db, sf)

}