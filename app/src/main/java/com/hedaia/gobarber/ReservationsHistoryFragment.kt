package com.hedaia.gobarber

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.DetailsDialog
import com.hedaia.gobarber.Views.ReservationsAdapter
import com.hedaia.gobarber.databinding.FragmentReservationsHistoryBinding


class ReservationsHistoryFragment : Fragment(),ReservationsAdapter.onClickListener {

    private lateinit var binding:FragmentReservationsHistoryBinding
    private lateinit var reservationAdapter: ReservationsAdapter
    private lateinit var viewModel: CustomersViewModel

    private var reservations= arrayListOf<Reservation>()
  //  var serviceProviders= arrayListOf<ServiceProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reservationAdapter=ReservationsAdapter(this)
        viewModel= ViewModelProvider(this)[CustomersViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentReservationsHistoryBinding.inflate(layoutInflater)

        viewModel.getReservations(currentCustomer!!).observe(viewLifecycleOwner){
            reservations.clear()
            reservations.addAll(it)
            Log.d("TAG", "onCreate: $reservations ")
            reservationAdapter.updateList(reservations)
        }

        binding.apply {
            reservationHisotryRV.adapter=reservationAdapter

            backToProfileIV.setOnClickListener {
                findNavController().navigate(R.id.action_reservationsHistoryFragment_to_customerProfileFragment)
            }

        }
        return binding.root
    }

    override fun reservationUser(Reservation: Reservation) {
        val detailsDialog =
            DetailsDialog(requireContext(),
                Reservation, this@ReservationsHistoryFragment)
        detailsDialog.show()
        detailsDialog.setCancelable(false)
    }


}