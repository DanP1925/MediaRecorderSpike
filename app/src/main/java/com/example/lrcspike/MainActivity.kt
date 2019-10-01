package com.example.lrcspike

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {

    lateinit var fileName: String

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var permissionToRecordAccepted = false

    lateinit var recordingButton: Button
    private var mStartRecording = true
    private var recorder: MediaRecorder? = null

    lateinit var playerButton: Button
    private var mStartPlaying = true
    private var player: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordingButton = findViewById(R.id.recordingButton)
        playerButton = findViewById(R.id.playerButton)

        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        recordingButton.setOnClickListener {
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
        }
        playerButton.setOnClickListener {
            onPlay(mStartPlaying)
            mStartPlaying = !mStartPlaying
        }

    }

    fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("Rip", "prepare died")
            }

            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("Rip2", "prepare died")
            }
        }
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

}
