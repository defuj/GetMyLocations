package com.defuj.mylocations

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.compat.GeoDataClient
import com.google.android.libraries.places.compat.PlaceDetectionClient
import com.google.android.libraries.places.compat.Places
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val permissionRequest = 100
    private val position = LatLng(-0.789275,113.9213257)
    private lateinit var mMap : GoogleMap
    private val defaultZoom = 17F //18.6F
    private var mGeoDataClient : GeoDataClient? = null
    private var mPlaceDetectionClient : PlaceDetectionClient? = null
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGeoDataClient = Places.getGeoDataClient(this)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        toolbarHome!!.setOnMenuItemClickListener {
            if(it.itemId == R.id.menu_info){
                startActivity(Intent(this,InfoActivity::class.java))
            }
            return@setOnMenuItemClickListener true
        }
        btnFind!!.setOnClickListener {
            letsGetLocation()
        }

        prepareGetLocation()
    }

    private fun letsGetLocation(){
        if(gpsCheck()){
            if(gpsPermissionCheck()){
                startGetLocations()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionRequest)
            }
        }else{
            val dialog = SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Warning"
            dialog.contentText = "Please enable location services to allow GPS tracking!"
            dialog.setCancelable(false)
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                letsGetLocation()
            }
            dialog.show()
        }
    }

    private fun prepareGetLocation(){
        if(gpsCheck()){
            if(gpsPermissionCheck()){
                val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.current_location) as SupportMapFragment
                mapFragment.getMapAsync(this)

                //startGetLocations()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionRequest)
            }
        }else{
            val dialog = SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Warning"
            dialog.contentText = "Please enable location services to allow GPS tracking!"
            dialog.setCancelable(false)
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                prepareGetLocation()
            }
            dialog.show()
        }
    }

    private fun startGetLocations() {
        val request = LocationRequest()
        //request.interval = 120000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val client : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        client.requestLocationUpdates(request,object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if(p0!!.lastLocation != null){
                    p0.lastLocation.latitude
                    p0.lastLocation.longitude

                    val position = LatLng(p0.lastLocation.latitude,p0.lastLocation.longitude)
                    setCurrentLocation(position)
                }
            }
        },null)
    }

    private fun gpsPermissionCheck() : Boolean{
        val result: Boolean
        val permission :Int = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        result = permission == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun gpsCheck() : Boolean{
        val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        prepareGetLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isMyLocationButtonEnabled = false

        setDefaultLocation()
    }

    private fun setDefaultLocation() {
        mMap.clear()
        /*mMap.addMarker(
            MarkerOptions()
            .position(position)
            .title("Lokasi Default")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))) */
        //mMap.setLatLngBoundsForCameraTarget(positionIDN)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,4.0F))
    }

    private fun setCurrentLocation(position : LatLng){
        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .position(position)
                .title("Lokasi saat ini")
                .snippet("Lat : ${position.latitude}, Long : ${position.longitude}")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,defaultZoom))
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
