package com.example.barcodeexamplekt

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setUpControls()
        }
    }

    private fun setUpControls() {
        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector)
            .setAutoFocusEnabled(true)
            .build()
        val surfaceView = findViewById<SurfaceView>(R.id.cameraSurfaceView)
        surfaceView.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setUpControls()
            }
        }else{
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val surfaceCallBack = object:  SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                cameraSource.start(holder)
            }catch (exception: Exception){
                Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val processor = object: Detector.Processor<Barcode>{
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
          if (detections!=null && detections.detectedItems.isNotEmpty()){
              val qrCodes: SparseArray<Barcode> = detections.detectedItems
              val code = qrCodes.valueAt(0)
              val textScan = findViewById<TextView>(R.id.textScanResult)
              textScan.text = code.displayValue
          }else{
              val textScan = findViewById<TextView>(R.id.textScanResult)
              textScan.text = ""
          }
        }

    }
}