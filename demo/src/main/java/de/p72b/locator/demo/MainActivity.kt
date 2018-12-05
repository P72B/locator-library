package de.p72b.locator.demo

import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import de.p72b.locator.location.LocationAwareAppCompatActivity



class MainActivity : LocationAwareAppCompatActivity(), View.OnClickListener {

    private lateinit var locationTextView: TextView
    private lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        mainPresenter = MainPresenter(locationManager, this)
    }

    private fun initViews() {
        findViewById<Button>(R.id.vButton).setOnClickListener(this)
        locationTextView = findViewById(R.id.vTextView)
    }

    override fun onClick(view: View?) {
        mainPresenter.onClick(view)
    }

    fun updateLocation(location: Location) {
        val displayedText = "${location.latitude} / ${location.longitude}"
        locationTextView.text = displayedText
    }

    fun showSnackbar(message: String?) {
        if (message == null) {
            return
        }
        val mySnackbar = Snackbar.make(findViewById(R.id.vMainRoot), message, Snackbar.LENGTH_SHORT)
        mySnackbar.show()
    }
}
