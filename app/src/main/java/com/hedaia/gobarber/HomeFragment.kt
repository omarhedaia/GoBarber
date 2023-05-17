package com.hedaia.gobarber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hedaia.gobarber.Models.Agenda
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.AgendaAdapter
import com.hedaia.gobarber.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), AgendaAdapter.onClick {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var agendaAdapter: AgendaAdapter
    lateinit var viewModel: CustomersViewModel

    var agendas= arrayListOf<Agenda>()
    var serviceProviders= arrayListOf<ServiceProvider>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)
        agendaAdapter= AgendaAdapter(this)


        viewModel.getAgenda().observe(viewLifecycleOwner){
            agendas.clear()
            agendas.addAll(it)
            Log.d("TAG", "onCreate: $agendas ")
            agendaAdapter.updateList(agendas)
        }
        viewModel.getServiceProvider().observe(viewLifecycleOwner) {
            serviceProviders.clear()
            serviceProviders.addAll(it)
            Log.d("TAG", "onCreate: $serviceProviders")
        }

        binding.apply {
//            bottomNavigation.setSelectedItemId(R.id.homeid)
//            bottomNavigation()
            welcomeTV.text="Welcome Back ${currentCustomer!!.name} !"
            serviceProviderRv.adapter=agendaAdapter
        }

        return binding.root
    }


    companion object userData{
        var currentServiceProvider: ServiceProvider? =null
    }

    override fun toOffer(serviceProvider: String) {

        for (current in serviceProviders) {
            if (serviceProvider == current.name) {
                currentServiceProvider = current
                Log.d("currentServiceProvider", "$currentServiceProvider ")
//                findNavController().navigate(R.id.action_homeFragment_to_reservationFragment)
            }
        }
    }

//
//    private fun bottomNavigation() {
//        // Perform item selected listener
//        binding.bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//
//                R.id.homeid -> {return@OnNavigationItemSelectedListener true }
//
//                R.id.searchid -> {
//                   findNavController().navigate(R.id.action_homeFragment_to_barbersLocationFragment)
//                    return@OnNavigationItemSelectedListener true
//                }
//
//                R.id.profileid -> {
//                    findNavController().navigate(R.id.action_homeFragment_to_customerProfileFragment)
//                    return@OnNavigationItemSelectedListener true
//                }
//            }
//            false
//        })
//    }


}