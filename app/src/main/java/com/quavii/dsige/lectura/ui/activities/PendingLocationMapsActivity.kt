package com.quavii.dsige.lectura.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import com.quavii.dsige.lectura.R
import com.quavii.dsige.lectura.data.dao.interfaces.SuministroImplementation
import com.quavii.dsige.lectura.data.dao.overMethod.SuministroOver
import com.quavii.dsige.lectura.data.model.SuministroCortes
import com.quavii.dsige.lectura.data.model.SuministroReconexion
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.quavii.dsige.lectura.data.model.SuministroReparto
import io.realm.Realm
import io.realm.RealmResults

class PendingLocationMapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    lateinit var mMap: GoogleMap
    var estado: Int = 0

    lateinit var locationManager: LocationManager

    lateinit var suministroImp: SuministroImplementation
    lateinit var realm: Realm
    lateinit var marker: MarkerOptions

    override fun onResume() {
        super.onResume()
        isGPSEnabled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_location_maps)

        val bundle = intent.extras
        if (bundle != null) {
            estado = bundle.getInt("estado")

            realm = Realm.getDefaultInstance()
            suministroImp = SuministroOver(realm)

            val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

    }


    override fun onMapReady(p: GoogleMap) {
        mMap = p
        zoomToLocation("-12.036175", "-76.999561")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0f, this)

        when (estado) {
            3 -> {
                val suministroCortes: RealmResults<SuministroCortes> = suministroImp.getSuministroCortes(3, 1)
                mMap.clear()
                for (s: SuministroCortes in suministroCortes) {
                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                        mMap.addMarker(MarkerOptions()
                                .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                                .title(s.iD_Suministro.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                    }
                }
            }
            4 -> {
                val suministrosReconexion: RealmResults<SuministroReconexion> = suministroImp.getSuministroReconexion(4, 1)
                mMap.clear()

                for (s: SuministroReconexion in suministrosReconexion) {
                    if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                        mMap.addMarker(MarkerOptions()
                                .position(LatLng(s.latitud.toDouble(), s.longitud.toDouble()))
                                .title(s.iD_Suministro.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                        // BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
//                         .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)))
                    }
                }
            }
        }
        mMap.setOnMarkerClickListener(this@PendingLocationMapsActivity)

    }

    private fun zoomToLocation(latitud: String, longitud: String) {
        val camera = CameraPosition.Builder()
                .target(LatLng(latitud.toDouble(), longitud.toDouble()))
                .zoom(10f)  // limite 21
                //.bearing(165) // 0 - 365°
                .tilt(30f)        // limit 90
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }

    private fun isGPSEnabled() {
        try {
            val gpsSignal = Settings.Secure.getInt(this.contentResolver, Settings.Secure.LOCATION_MODE)
            if (gpsSignal == 0) {
                showInfoAlert()
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showInfoAlert() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        builder.setTitle("GPS Signal")
        builder.setMessage("Necesitas tener habilitado la señal de GPS. Te gustaria habilitar la señal de GPS ahora ?.")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    override fun onLocationChanged(p: Location) {
        zoomToLocation(p.latitude.toString(), p.longitude.toString())







    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onMarkerClick(p: Marker): Boolean {
        dialogResumen(p.title)
        return true
    }


    private fun dialogResumen(t: String) {
        val builder = android.app.AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.cardview_resumen_maps, null)

        val buttonSalir = v.findViewById<Button>(R.id.buttonSalir)
        val textViewTitle = v.findViewById<TextView>(R.id.textViewTitle)
        val textViewMedidor = v.findViewById<TextView>(R.id.textViewMedidor)
        val textViewContrato = v.findViewById<TextView>(R.id.textViewContrato)
        val textViewDireccion = v.findViewById<TextView>(R.id.textViewDireccion)

        when (estado) {
            3 -> {
                val s: SuministroCortes = suministroImp.suministroCortesById(t.toInt())
                textViewTitle.text = String.format("Orden : %s", s.orden)
                textViewMedidor.text = String.format("Medidor :%s", s.suministro_Medidor)
                textViewContrato.text = String.format("Contrato :%s", s.suministro_Numero)
                textViewDireccion.text = s.suministro_Direccion

            }
            4 -> {
                val s: SuministroReconexion = suministroImp.suministroReconexionById(t.toInt())
                textViewTitle.text = String.format("Orden : %s", s.orden)
                textViewMedidor.text = String.format("Medidor :%s", s.suministro_Medidor)
                textViewContrato.text = String.format("Contrato :%s", s.suministro_Numero)
                textViewDireccion.text = s.suministro_Direccion
            }
            else -> {
                val s: SuministroReparto = suministroImp.suministroRepartoById(t.toInt())
                textViewTitle.text = String.format("Orden : %s", s.Cod_Orden_Reparto)
                textViewMedidor.text = String.format("Medidor :%s", s.Suministro_Medidor_reparto)
                textViewContrato.text = String.format("Contrato :%s", s.Suministro_Numero_reparto)
                textViewDireccion.text = s.Direccion_Reparto
            }
        }

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonSalir.setOnClickListener {
            dialog.dismiss()
        }
    }
}