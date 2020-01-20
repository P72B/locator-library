package de.p72b.locator.demo

import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import de.p72b.locator.location.LocationAwareAppCompatActivity
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class MainActivity : LocationAwareAppCompatActivity(), View.OnClickListener {

    private lateinit var locationTextView: TextView
    private lateinit var locationTextViewTstamp: TextView
    private lateinit var locationTextViewPreviousUpdate: TextView
    private lateinit var locationIndicator: View
    private lateinit var buttonLocationUpdates: Button
    private lateinit var mainPresenter: MainPresenter
    private val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.GERMANY)
    private var lastLocationUpdateTstamp: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        mainPresenter = MainPresenter(locationManager, this)
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.onResume()
    }

    override fun onPause() {
        mainPresenter.onPause()
        super.onPause()
    }

    private fun initViews() {
        findViewById<Button>(R.id.vButtonStatus).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonLoud).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonSilent).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonLocationRequestDefault).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonLocationRequestHigh).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonLocationRequestLow).setOnClickListener(this)
        buttonLocationUpdates = findViewById(R.id.vButtonLocationUpdates)
        buttonLocationUpdates.setOnClickListener(this)
        locationTextView = findViewById(R.id.vTextViewLocation)
        locationTextViewTstamp = findViewById(R.id.vTextViewTstamp)
        locationTextViewPreviousUpdate = findViewById(R.id.vTextViewPreviousUpdate)
        locationIndicator = findViewById(R.id.vLocationIndicator)
    }

    override fun onClick(view: View?) {
        mainPresenter.onClick(view)
    }

    fun updateLocation(location: Location) {
        val displayedText = "${location.latitude} / ${location.longitude}"
        locationTextView.text = displayedText
        val now = Calendar.getInstance().time
        if (lastLocationUpdateTstamp != null) {
            val millis = now.time - lastLocationUpdateTstamp!!.time
            val lastUpdate = String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            )
            locationTextViewPreviousUpdate.text = "previous update $lastUpdate ago"
        }
        lastLocationUpdateTstamp = now
        locationTextViewTstamp.text = sdf.format(now)
        animateIndicator()
    }

    fun showSnackbar(message: String?) {
        if (message == null) {
            return
        }
        val mySnackbar = Snackbar.make(findViewById(R.id.vMainRoot), message, Snackbar.LENGTH_SHORT)
        mySnackbar.show()
    }

    fun setLocationUpdatesState(title: String) {
        buttonLocationUpdates.text = title
    }

    private fun animateIndicator() {
        val duration = 2000
        val anim = AnimationUtils.loadAnimation(baseContext, R.anim.fade_out)
        anim.fillAfter = true
        anim.duration = duration.toLong()
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                locationIndicator.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                locationIndicator.visibility = View.INVISIBLE
            }
        })
        locationIndicator.startAnimation(anim)
    }
}
