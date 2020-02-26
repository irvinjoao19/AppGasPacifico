package com.quavii.dsige.lectura.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.quavii.dsige.lectura.R
import android.location.Location
import android.util.Log
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class ExamplePhotoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPhoto -> {
                val location = Location("localizacion 1")
                location.latitude = -11.9964163  //latitud
                location.longitude = -77.0060805 //longitud
                val location2 = Location("localizacion 2")
                location2.latitude = -11.996479267130283  //latitud
                location2.longitude = -77.00568353307929 //longitud
                val distance = calculationByDistance(location, location2)
                textViewLatitud.text = String.format("%s %s %s", "Distancia es ", distance, "m.")

                //    if
                //                //      //     val gps = Gps(this) (gps.isLocationEnabled()) {
                //         if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                //             gps.showAlert()
                //         } else {
                //             textViewLatitud.text = gps.latitude.toString()
                //             textViewLongitud.text = gps.longitude.toString()
                //         }
                //     } else {
                //         gps.showSettingsAlert()
                //     }
            }
        }
    }

    lateinit var buttonPhoto: Button
    lateinit var textViewLatitud: TextView
    lateinit var textViewLongitud: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_photo)
        buttonPhoto = findViewById(R.id.buttonPhoto)
        textViewLatitud = findViewById(R.id.textViewLatitud)
        textViewLongitud = findViewById(R.id.textViewLongitud)
        buttonPhoto.setOnClickListener(this)
    }


   private fun calculationByDistance(StartP: Location, EndP: Location): Double {
        val Radius = 6371 * 1000  // radius of earth in Km * meters
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2))
        val c = 2 * asin(sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i("Radius Value", "" + valueResult + "  km  " + kmInDec
                + " Meter   " + meterInDec)

        // return Radius * c
        return kmInDec.toDouble()
    }
}