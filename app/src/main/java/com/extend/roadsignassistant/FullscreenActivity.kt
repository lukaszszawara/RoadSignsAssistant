package com.extend.roadsignassistant

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.*
import androidx.core.app.ActivityCompat
import com.priyankvasa.android.cameraviewex.CameraView
import com.priyankvasa.android.cameraviewex.ErrorLevel
import com.priyankvasa.android.cameraviewex.Image
import com.priyankvasa.android.cameraviewex.Modes
import rebus.permissionutils.PermissionEnum
import rebus.permissionutils.PermissionManager
import java.io.IOException
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var gtsrbClassifier: GtsrbClassifier? = null
    private lateinit var camera: CameraView
    var tts: TextToSpeech? = null

    var ivPreview: ImageView? = null

    var ivFinalPreview: ImageView? = null

    var tvClassification: TextView? = null



    private val hideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }


    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        camera.start()
    }

    override fun onPause() {

        camera.stop()
        super.onPause()
    }

    override fun onDestroy() {
        camera.destroy()
        super.onDestroy()
    }

    private fun loadGtsrbClassifier() {
        try {
            gtsrbClassifier =
                GtsrbClassifier.classifier(assets, GtsrbModelConfig.MODEL_FILENAME)
        } catch (e: IOException) {
            Toast.makeText(
                this,
                "GTSRB model couldn't be loaded. Check logs for details.",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true
        getPermission()
        camera = findViewById(R.id.camera)
        ivPreview = findViewById(R.id.ivPreview)
        ivFinalPreview = findViewById(R.id.ivFinalPreview);
        tvClassification = findViewById(R.id.tvClassification);
        tts  = TextToSpeech(this, this)

        // Callbacks on UI thread
        camera.addCameraOpenedListener { /* Camera opened. */ }
            .addCameraErrorListener { t: Throwable, errorLevel: ErrorLevel -> /* Camera error! */ }
            .addCameraClosedListener { /* Camera closed. */ }


        loadGtsrbClassifier()

        findViewById<Button>(R.id.photo_button).setOnClickListener{
            onTakePhoto()
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.


        delayedHide(100)
    }


    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }


    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }




    fun getPermission(){
        PermissionManager.Builder()
            .permission(
                PermissionEnum.WRITE_EXTERNAL_STORAGE,
                PermissionEnum.CAMERA,
                PermissionEnum.READ_EXTERNAL_STORAGE)
            .askAgain(false)
            .ask(this)
    }

    fun onTakePhoto() {

        // enable only single capture mode
        camera.setCameraMode(Modes.CameraMode.SINGLE_CAPTURE)
// OR keep other modes as is and enable single capture mode
        camera.enableCameraMode(Modes.CameraMode.SINGLE_CAPTURE)
// Output format is whatever set for [app:outputFormat] parameter
// Callback on UI thread
        camera.addPictureTakenListener { image: Image -> onImageCaptured(image)}
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        camera.capture()
// Disable single capture mode
        //camera.disableCameraMode(Modes.CameraMode.SINGLE_CAPTURE)

    }

    private fun onImageCaptured(picture: Image) {
        val bitmap = BitmapFactory.decodeByteArray(picture.data, 0, picture.data.size)
        val squareBitmap =
            ThumbnailUtils.extractThumbnail(bitmap, getScreenWidth(), getScreenWidth())
        ivPreview?.setImageBitmap(squareBitmap)
        val preprocessedImage = ImageUtils.prepareImageForClassification(squareBitmap)
        ivFinalPreview?.setImageBitmap(preprocessedImage)
        val recognitions: List<Classification> = gtsrbClassifier?.recognizeImage(preprocessedImage) as List<Classification>
        tvClassification?.setText(recognitions.toString())

        say(recognitions.toString().replace("_"," "))
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            for (locale in tts!!.availableLanguages)
            {
                if (locale.language == "pl")
                    tts!!.setLanguage(locale);
            }
            // try it!
            say("test języka polskiego");
            // If you want to another "say", check this log.
            // Your voice will say after you see this log at logcat.
            Log.i("TAG", "TextToSpeech instance initialization is finished.");
        }
    }

    private fun say(s: String) {
        tts?.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }
}