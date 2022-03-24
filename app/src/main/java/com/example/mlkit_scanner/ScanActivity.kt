package com.example.mlkit_scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mlkit_scanner.databinding.ActivityScanBinding
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private val cameraExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val binding by lazy {
        ActivityScanBinding.inflate(layoutInflater)
    }

    private lateinit var barcodeBoxView: BarcodeBoxView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        barcodeBoxView = BarcodeBoxView(this)
        addContentView(barcodeBoxView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        checkPermission()
    }

    private fun checkPermission() {
        //카메라 권한의 승인 상태 가져오기
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            //상태가 승인일 경우에는 코드 진행
            startCamera()
        } else {
            //승인되지 않았다면 권한 요청 프로세스 진행
            requestPermission()
        }
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions을 사용하면 사용자에게 권한을 요청하는 팝업을 보여줍니다.
        //사용자가 선택한 값은 onRequestPermissionsResult메서드를 통해서 전달되어 집니다.
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 99)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            99 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    finish()
                }
            }
        }
    }

    private fun startCamera() {
        Log.d("kim", "startCamera")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            Log.d("kim", "StartCamera")
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    val width = binding.previewView.width.toFloat()
                    val height = binding.previewView.height.toFloat()
                    Log.d("kim", "width: $width, height: $height")
                    it.setAnalyzer(
                        cameraExecutor, QrCodeAnalyzer(
                            this,
                            barcodeBoxView,
                            width,
                            height
                        )
                    )
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("kim", "onDestroy")
        cameraExecutor.shutdown()
    }
}