package com.hedaia.gobarber

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.hedaia.gobarber.HomeFragment.userData.currentServiceProvider
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.Models.ServiceProviderLocation
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.ServicesProvidersAdapter
import com.hedaia.gobarber.databinding.BarberInfoMarkerBinding
import com.hedaia.gobarber.databinding.CurrentLocationLayoutBinding
import com.hedaia.gobarber.databinding.FragmentBarbersLocationBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*



class BarbersLocationFragment : Fragment(),ServicesProvidersAdapter.onClick,OnMapReadyCallback,
    OnMapsSdkInitializedCallback,com.google.android.gms.location.LocationListener {

    val TAG = "BarbersLocation"
    lateinit var binding: FragmentBarbersLocationBinding
    lateinit var viewModel: CustomersViewModel
    var serviceProviders = arrayListOf<ServiceProviderLocation>()
    lateinit var servicesProvidersAdapter: ServicesProvidersAdapter
    lateinit var currentGoogleMap: GoogleMap
    lateinit var currentLocationMarker: Marker
    lateinit var supportMapFragment: SupportMapFragment
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this)

    }

    fun initCurrentMarker() {
        val newCurrentLocation = LatLng(0.0, 0.0)
        val markerOptions = MarkerOptions()
        markerOptions.position(newCurrentLocation)
        markerOptions.title("You are here")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        currentLocationMarker = currentGoogleMap.addMarker(markerOptions)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBarbersLocationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(CustomersViewModel::class.java)
        servicesProvidersAdapter = ServicesProvidersAdapter(this)

        supportMapFragment =
            this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        Log.d(TAG, "onCreateView: ")
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        Dexter.withContext(requireContext()).withPermissions(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report != null) {
                        if (report.areAllPermissionsGranted()) {
                            getCurrentLocation()
                        } else {
                            val dialogPermissionListener: PermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                    .withContext(context)
                                    .withTitle("Location permission")
                                    .withMessage("Location permission is needed to find barbers around you")
                                    .withButtonText("Ok")
                                    .withIcon(R.drawable.ic_baseline_location_on_24)
                                    .build()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequestList: MutableList<PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    if (permissionToken != null)
                        permissionToken.continuePermissionRequest()
                }

            }).check()



        binding.apply {
            barbersRV.adapter = servicesProvidersAdapter

        }

        return binding.root
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "getCurrentLocation: not granted")
            return
        } else {
            Log.d(TAG, "getCurrentLocation: granted")
            val locationRequest =
                com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10).apply {
                    setMinUpdateDistanceMeters(10f)
                }.build()

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                this,
                requireContext().mainLooper
            )


        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


    }


    override fun setserviceProvider(serviceProvider: ServiceProvider) {
        currentServiceProvider = serviceProvider
        findNavController().navigate(R.id.action_barbersLocationFragment_to_reservationFragment)
    }

    override fun onMapReady(googleMap: GoogleMap) {


        currentGoogleMap = googleMap

        currentGoogleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View? {
               return null


            }

            override fun getInfoWindow(marker: Marker): View {
                val barberInfoMarkerBinding = BarberInfoMarkerBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                val currentLocationLayoutBinding = CurrentLocationLayoutBinding.inflate(LayoutInflater.from(requireContext()),null,false)


                val serviceProviderDto:ServiceProviderLocation? = marker.tag as ServiceProviderLocation?
                if (serviceProviderDto!=null)
                {
                    barberInfoMarkerBinding.apply {
//                        val distanceString = "${(serviceProviderDto.distance)}"
                        val distanceFloatString = String.format("%.1f",serviceProviderDto.distance) + " km"
                        barberTitleTv.text = serviceProviderDto.name
                        barberDistanceTv.text = distanceFloatString
                        barberMintimeTv.text = serviceProviderDto.minimumWaitTime.toString()
                        barberMaxtimeTv.text = serviceProviderDto.maximumWaitTime.toString()
                        barberImageIv.setImageResource(R.drawable.afro)
                    }
                    return barberInfoMarkerBinding.root

                }else{
                    return currentLocationLayoutBinding.root

                }
            }

        })

    }


    fun addUserLocation(localUserLocation: Location) {

        changeCityName(localUserLocation)
        Log.d(TAG, "addUserLocation: ")
        currentLocationMarker.remove()

        currentGoogleMap.clear()
        val newCurrentLocation = LatLng(localUserLocation.latitude, localUserLocation.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(newCurrentLocation)
        markerOptions.title("You are here")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        currentLocationMarker = currentGoogleMap.addMarker(markerOptions)!!
        currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newCurrentLocation))
        currentGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(5f))

        viewModel.getNearbyServiceProvider(localUserLocation).observe(viewLifecycleOwner) {
            serviceProviders.clear()
            serviceProviders.addAll(it)
            Log.d(TAG, "addUserLocation: viewmodel")
            Log.d("TAG", "onCreate: $serviceProviders")
            val serviceProviderList = it.map { serviceProviderLocation -> ServiceProvider(serviceProviderLocation.name,serviceProviderLocation.longitude,serviceProviderLocation.latitude) }
            servicesProvidersAdapter.updateList(serviceProviderList)
            if (currentGoogleMap != null) {
                for (serviceProvider in serviceProviders) {
                    Log.d("markerLocation", "onViewCreated: ${serviceProvider.name}")

                        val latitude = serviceProvider.latitude
                        Log.d("markerLocation", "onMapReady: latitude = $latitude ")
                        val longitude = serviceProvider.longitude
                        Log.d("markerLocation", "onMapReady: longitude = $longitude ")

                        val location = LatLng(latitude!!.toDouble(), longitude!!.toDouble())

                    val serviceMarkerOptions = MarkerOptions()
                    serviceMarkerOptions.title(serviceProvider.name)
                    serviceMarkerOptions.position(location)
                    val bitmap = resourceToBitmap(R.drawable.scissors_icon)
                    serviceMarkerOptions.icon( BitmapDescriptorFactory.fromBitmap(bitmap))


                    currentGoogleMap.addMarker(
                        serviceMarkerOptions
                    )!!.tag = serviceProvider



                }
            }
        }

    }


    fun bitmapToSmallMarker(view: View): BitmapDescriptor {
        val bitmap = Bitmap.createScaledBitmap(
            viewToBitmap(view),
            view.width,
            view.height,
            false
        )

        val smallMarker = BitmapDescriptorFactory.fromBitmap(bitmap)
        return smallMarker

    }

    fun viewToBitmap(view: View): Bitmap {

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    fun resourceToBitmap(resourceImage: Int): Bitmap {

        val bitmap = AppCompatResources.getDrawable(requireContext(),resourceImage)!!.toBitmap(100,100,Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val filter: ColorFilter = PorterDuffColorFilter(
            resources.getColor(R.color.red),
            PorterDuff.Mode.SRC_IN
        )
        paint.colorFilter = filter

        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmap
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d(
                "MapsDemo",
                "The latest version of the renderer is used."
            )
            MapsInitializer.Renderer.LEGACY -> Log.d(
                "MapsDemo",
                "The legacy version of the renderer is used."
            )
        }
    }

    override fun onLocationChanged(location: Location) {

        //current is not initialized error
        supportMapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                currentGoogleMap = map
                initCurrentMarker()
                if (location != null) {
                    addUserLocation(location)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location Permission is Required!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }


    fun changeCityName(location:Location){

        val geocoder = Geocoder(requireContext(), Locale.ENGLISH)
        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (!addresses.isEmpty()) {
                        val addressString = "${addresses[0].locality}, ${addresses[0].countryName}"

                        binding.locationTv.text = addressString
                    }
                }

            }
            geocoder.getFromLocation(location.latitude,location.longitude,1,geocodeListener)
        }
        else
        {
            val addresses = geocoder.getFromLocation(location.latitude,location.longitude,1)
            if (!addresses.isNullOrEmpty())
            {
                val addressString = "${addresses[0].locality}, ${addresses[0].countryName}"
                binding.locationTv.text = addressString
            }
        }

    }


}