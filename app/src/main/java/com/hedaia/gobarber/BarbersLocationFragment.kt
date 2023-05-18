package com.hedaia.gobarber

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.testgolden.Views.Adapters.ServicesAdapter
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hedaia.gobarber.HomeFragment.userData.currentServiceProvider
import com.hedaia.gobarber.Models.Barbers
import com.hedaia.gobarber.Models.Customer
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.Models.Services
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.BarbersAdapter
import com.hedaia.gobarber.Views.ServicesProvidersAdapter
import com.hedaia.gobarber.databinding.BarberInfoMarkerBinding
import com.hedaia.gobarber.databinding.FragmentBarbersLocationBinding

class BarbersLocationFragment : Fragment(),ServicesProvidersAdapter.onClick,OnMapReadyCallback,
    OnMapsSdkInitializedCallback {

    lateinit var binding: FragmentBarbersLocationBinding
    lateinit var viewModel: CustomersViewModel
    var serviceProviders= arrayListOf<ServiceProvider>()
    lateinit var servicesProvidersAdapter: ServicesProvidersAdapter
    lateinit var currentGoogleMap:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext(),MapsInitializer.Renderer.LATEST,this)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentBarbersLocationBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)
        servicesProvidersAdapter= ServicesProvidersAdapter(this)


        viewModel.getServiceProvider().observe(viewLifecycleOwner) {
            serviceProviders.clear()
            serviceProviders.addAll(it)
            Log.d("TAG", "onCreate: $serviceProviders")
            servicesProvidersAdapter.updateList(serviceProviders)
            if (currentGoogleMap!=null)
            {
                for (serviceProvider in serviceProviders)
                {
                    Log.d("markerLocation", "onViewCreated: ${serviceProvider.name}")

                    val serviceProviderMarkerBinding = BarberInfoMarkerBinding.inflate(layoutInflater)

                    serviceProviderMarkerBinding.apply {

                        barberTitleTv.text = serviceProvider.name
                        barberDistanceTv.text = "20km"
                        barberImageIv.setImageResource(R.drawable.afro)
                        val latitude = serviceProvider.latitude
                        Log.d("markerLocation", "onMapReady: latitude = $latitude ")
                        val longitude = serviceProvider.longitude
                        Log.d("markerLocation", "onMapReady: longitude = $longitude ")

                        val location = LatLng(latitude!!.toDouble(),longitude!!.toDouble())
                val barberIcon = bitmapToSmallMarker(serviceProviderMarkerBinding.root)
                        currentGoogleMap.addMarker(MarkerOptions().position(location).title(serviceProvider.name).icon(barberIcon))
                        currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    }



                }
            }
        }

        binding.apply {
            barbersRV.adapter=servicesProvidersAdapter
//            bottomNavigation.setSelectedItemId(R.id.searchid)
//            bottomNavigation()


        }
        val googleApistatues = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


    }

    override fun onResume() {
        super.onResume()



    }

    override fun setserviceProvider(serviceProvider: ServiceProvider) {
        currentServiceProvider=serviceProvider
        findNavController().navigate(R.id.action_barbersLocationFragment_to_reservationFragment)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        currentGoogleMap = googleMap





//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


    }

    fun bitmapToSmallMarker(view: View):BitmapDescriptor{
        val bitmap = Bitmap.createScaledBitmap(
            viewToBitmap(view),
            view.width,
            view.height,
            false)

        val smallMarker = BitmapDescriptorFactory.fromBitmap(bitmap)
        return smallMarker

    }

    fun viewToBitmap(view:View):Bitmap{

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth,view.measuredHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth,view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }


}