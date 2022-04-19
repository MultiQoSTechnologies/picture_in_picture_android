package com.example.pipdemo

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.widget.MediaController
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.example.pipdemo.Constant.CONTROL_BACKWARD
import com.example.pipdemo.Constant.CONTROL_FORWARD
import com.example.pipdemo.Constant.CONTROL_PAUSE
import com.example.pipdemo.Constant.CONTROL_PLAY
import com.example.pipdemo.databinding.ActivityMainBinding

/* Require minimum api level 26 or Android version 8 */
@RequiresApi(api = Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaController: MediaController? = null
    var stopPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        initializeVideoView()
        registerReceiver(broadcastReceiver, IntentFilter(Constant.ACTION_CONTROLS))
    }

    /*
    Initialize video with default url
    */
    private fun initializeVideoView() {
        mediaController = MediaController(this)
        mediaController!!.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(Uri.parse(getString(R.string.dummy_video_url)))
        binding.videoView.doOnLayout { updatePictureInPictureMode() }
        binding.videoView.seekTo(stopPosition)
        binding.videoView.start()

    }


    override fun onUserLeaveHint() {
        updatePictureInPictureMode()
        minimizeView()
        binding.videoView.setMediaController(mediaController)
        super.onUserLeaveHint()
    }

    /*
      Trigger's when activity is about to go into background
      Ex. user press home button this method is trigger
     */
    private fun updatePictureInPictureMode(): PictureInPictureParams? {
        val visibleRect = Rect()
        binding.videoView.getGlobalVisibleRect(visibleRect)
        val aspectRatio = Rational(binding.videoView.width, binding.videoView.height)
        val pictureInPictureParams = PictureInPictureParams.Builder()
            .setActions(
                listOf(
                    createRemoteAction(
                        android.R.drawable.ic_media_previous,
                        R.string.label_previous,
                        13,
                        CONTROL_BACKWARD
                    ),
                    if (binding.videoView.isPlaying) {
                        createRemoteAction(
                            android.R.drawable.ic_media_pause,
                            R.string.label_pause,
                            2,
                            CONTROL_PAUSE
                        )
                    } else {
                        createRemoteAction(
                            android.R.drawable.ic_media_play,
                            R.string.label_play,
                            1,
                            CONTROL_PLAY
                        )
                    },
                    createRemoteAction(
                        android.R.drawable.ic_media_next,
                        R.string.label_next,
                        14,
                        CONTROL_FORWARD
                    )
                )
            )
            .setAspectRatio(aspectRatio)
            .setSourceRectHint(visibleRect)
            .build()
        setPictureInPictureParams(pictureInPictureParams)
        return pictureInPictureParams
    }

    /*
    Activity enter into picture in picture mode when application in background mode
    */
    private fun minimizeView() {
        enterPictureInPictureMode(updatePictureInPictureMode()!!)
    }

    /*
    Trigger's when activity in background mode and go into picture in picture mode and vise-versa
    */
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    /*
    Add custom icons on picture in picture surface
    Ex. Added play,pause,previous and next button
    */
    private fun createRemoteAction(
        @DrawableRes icon: Int,
        @StringRes title: Int,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            Intent(Constant.ACTION_CONTROLS)
                .putExtra(Constant.EXTRA_CONTROL_TYPE, controlType),
            PendingIntent.FLAG_IMMUTABLE
        )
        val remoteAction = RemoteAction(
            Icon.createWithResource(this, icon),
            getString(title),
            getString(title),
            pendingIntent
        )
        val list = ArrayList<RemoteAction>()
        list.add(remoteAction)
        return remoteAction
    }

    /*

    Trigger's when any action trigger from picture in picture mode
    Ex. when click on any icon play,pause,previous,next broadcast event trigger
    *
    */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != Constant.ACTION_CONTROLS) {
                return
            }
            when (intent.getIntExtra(Constant.EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_PLAY -> {
                    binding.videoView.seekTo(stopPosition)
                    binding.videoView.start()
                }

                CONTROL_PAUSE -> {
                    stopPosition = binding.videoView.currentPosition
                    binding.videoView.pause()
                }

                CONTROL_FORWARD -> {
                    var pos: Int = binding.videoView.currentPosition
                    pos += 10000 // milliseconds
                    binding.videoView.seekTo(pos)
                }
                CONTROL_BACKWARD -> {
                    var pos: Int = binding.videoView.currentPosition
                    pos -= 10000 // milliseconds
                    binding.videoView.seekTo(pos)
                }

            }
            updatePictureInPictureMode()
        }
    }

}