package com.hedaia.gobarber

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
import com.hedaia.gobarber.databinding.FragmentBarbersLocationBinding

class BarbersLocationFragment : Fragment(),ServicesProvidersAdapter.onClick {

    lateinit var binding: FragmentBarbersLocationBinding
    lateinit var viewModel: CustomersViewModel
    var serviceProviders= arrayListOf<ServiceProvider>()
    lateinit var servicesProvidersAdapter: ServicesProvidersAdapter



    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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
        }

        binding.apply {
            barbersRV.adapter=servicesProvidersAdapter
            bottomNavigation.setSelectedItemId(R.id.searchid)
            bottomNavigation()


        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun bottomNavigation() {
        // Perform item selected listener
        binding.bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homeid -> { findNavController().navigate(R.id.action_barbersLocationFragment_to_homeFragment)
                    return@OnNavigationItemSelectedListener true }

                R.id.searchid -> {
                    return@OnNavigationItemSelectedListener true
                }

                R.id.profileid -> {
                    findNavController().navigate(R.id.action_barbersLocationFragment_to_customerProfileFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun setserviceProvider(serviceProvider: ServiceProvider) {
        currentServiceProvider=serviceProvider
        findNavController().navigate(R.id.action_barbersLocationFragment_to_reservationFragment)
    }


}