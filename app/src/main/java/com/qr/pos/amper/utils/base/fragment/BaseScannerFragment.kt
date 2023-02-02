package com.qr.pos.amper.utils.base.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

abstract class BaseScannerFragment<Binding: ViewBinding>(layoutRes: Int): BaseFragment<Binding>(layoutRes), ImageAnalysis.Analyzer {

    protected abstract val cameraPreview: PreviewView

    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider

    private val preview by lazy {
        Preview.Builder().build()
            .also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }
    }

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val imageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(cameraPreview.width, cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(
                    cameraExecutor,
                    this
                )
            }
    }

    private val cameraExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }

    @Inject
    lateinit var scanner: BarcodeScanner

    private val cameraPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted) setupCamera()
    }

    override fun setupView() {
        cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext())
        requestCameraPermission()
    }

    private fun requestCameraPermission(){
        if(ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera()
        }else cameraPermissionResult.launch(Manifest.permission.CAMERA)
    }

    private fun setupCamera() {
        val listener = {
            cameraProvider = cameraProviderFeature.get()
            startCamera()
        }
        cameraProviderFeature.addListener(listener, cameraExecutor)
    }

    fun stopCamera() {
        cameraProvider.unbindAll()
    }

    fun startCamera() {
        try {
            stopCamera()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
        } catch(exc: Exception) {
            Log.d("bindCameraPreview", exc.message ?:"")
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        when(barcode.valueType){
                            Barcode.TYPE_TEXT,
                            Barcode.TYPE_URL -> {
                                barcode.rawValue?.let {
                                    onQRFound(it)
                                }
                            }
                        }
                    }
                }.addOnCompleteListener { imageProxy.close() }
        }
    }


    abstract fun onQRFound(value: String)
}