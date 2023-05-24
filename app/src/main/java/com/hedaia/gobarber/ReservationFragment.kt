package com.hedaia.gobarber

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.testgolden.Views.Adapters.ServicesAdapter
import com.hedaia.gobarber.HomeFragment.userData.currentServiceProvider
import com.hedaia.gobarber.Models.Barbers
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.Models.Services
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.BarbersAdapter
import com.hedaia.gobarber.databinding.FragmentReservationBinding
import java.time.LocalDateTime


class ReservationFragment : Fragment(),BarbersAdapter.onClick,ServicesAdapter.onClickListener {

    private lateinit var binding: FragmentReservationBinding

    lateinit var viewModel:CustomersViewModel

    var  barbersList= listOf<Barbers>()
    var  barbersalonList= arrayListOf<Barbers>()
    var  servicesList= listOf<Services>()
    private lateinit var servicesUserList: ArrayList<Services>// User Service
    private lateinit var servicesUserListString: ArrayList<String>// User Service


    lateinit var servicesAdapter : ServicesAdapter
    lateinit var barbersAdapter : BarbersAdapter

    var newReservation: Reservation?=null
    var time=0
    var price=0

    companion object userData{
        var chosenBarber: Barbers? =null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentReservationBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)

        servicesUserList= arrayListOf()
        servicesUserListString= arrayListOf()

        servicesAdapter=ServicesAdapter(this)
        barbersAdapter=BarbersAdapter(this)

        binding.servicesRV.adapter=servicesAdapter
        binding.barbersRV.adapter=barbersAdapter

        viewModel.getServices(currentServiceProvider!!).observe(viewLifecycleOwner,{
            servicesList=it
            servicesAdapter.updateList(servicesList)
        })

        viewModel.getBarbers().observe(viewLifecycleOwner,{
            barbersList=it
            Log.d("Reservation", " BarbersList: $barbersList")
            getBarberSalon()
        })

        binding.apply {
            bookBtn.setOnClickListener {
                if(chosenBarber!=null&&servicesUserList.isNotEmpty()&&price!=0&&time!=0){
                    newReservation= Reservation("",currentCustomer!!.name,
                        chosenBarber!!.name, currentServiceProvider!!.name,
                        servicesUserListString.joinToString(","),
                        LocalDateTime.now().toString(), price.toString(),time.toString(),)
                    viewModel.saveReservation(newReservation!!)
                   findNavController().navigate(R.id.action_reservationFragment_to_barbersLocationFragment)
                }else{
                    Toast.makeText(context,"Please Complete your Reservation!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            backToSearchIV.setOnClickListener{
                goBackToSearchFragment()
            }

        }


        return binding.root
    }

    private fun getBarberSalon() {
        for(barber in barbersList){
            Log.d("Reservation", " Barber $barber")
            if(barber.serviceProviderID== currentServiceProvider!!.name){
                barbersalonList.add(barber)
            }
        }
        Log.d("Reservation", "BarbersalonList: $barbersalonList ")
        barbersAdapter.updateList(barbersalonList)
    }

    override fun setBarberReservation(barber: Barbers, isChecked: Boolean) {
        if(isChecked){
            chosenBarber=barber
        }else{
            chosenBarber=null
            Toast.makeText(context,"Please select the barber name!",Toast.LENGTH_SHORT).show()

        }
    }

    override fun Userservices(service: Services, checked: Boolean) {
        if(checked){
            servicesUserList.add(service)
            servicesUserListString.add(service.name.toString())
            price=price+service.price!!.toInt()
            time=time+service.time!!.toInt()


        }else{
            servicesUserList.remove(service)
            price=price-(service.price)!!.toInt()
            time=time-(service.time)!!.toInt()
        }
        binding.apply {
            priceCount.text= price.toString() +" SAR."
            timeCount.text= time.toString() +" Minutes."
        }
        Log.d("Reservation", "Userservices: $servicesUserList")
    }

    fun goBackToSearchFragment(){
        findNavController().navigate(R.id.action_reservationFragment_to_barbersLocationFragment)
    }

}