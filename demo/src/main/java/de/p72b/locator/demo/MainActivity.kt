package de.p72b.locator.demo

import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import de.p72b.locator.location.LocationAwareAppCompatActivity
import java.util.*
import java.text.SimpleDateFormat


class MainActivity : LocationAwareAppCompatActivity(), View.OnClickListener {

    private lateinit var locationTextView: TextView
    private lateinit var locationTextViewTstamp: TextView
    private lateinit var mainPresenter: MainPresenter
    private val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.GERMANY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        mainPresenter = MainPresenter(locationManager, this)
    }

    private fun initViews() {
        findViewById<Button>(R.id.vButtonLoud).setOnClickListener(this)
        findViewById<Button>(R.id.vButtonSilent).setOnClickListener(this)
        locationTextView = findViewById(R.id.vTextViewLocation)
        locationTextViewTstamp = findViewById(R.id.vTextViewTstamp)
    }

    override fun onClick(view: View?) {
        mainPresenter.onClick(view)
    }

    fun updateLocation(location: Location) {
        val displayedText = "${location.latitude} / ${location.longitude}"
        locationTextView.text = displayedText

        locationTextViewTstamp.text = sdf.format(Calendar.getInstance().time)
    }

    fun showSnackbar(message: String?) {
        if (message == null) {
            return
        }
        val mySnackbar = Snackbar.make(findViewById(R.id.vMainRoot), message, Snackbar.LENGTH_SHORT)
        mySnackbar.show()
    }
}
