package com.hedaia.gobarber

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.databinding.FragmentCustomerProfileBinding


class CustomerProfileFragment : Fragment() {

    private lateinit var binding: FragmentCustomerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCustomerProfileBinding.inflate(layoutInflater)

        binding.apply {
            username.text= currentCustomer!!.name

            accountBtn.setOnClickListener {
                findNavController().navigate(R.id.action_customerProfileFragment_to_accountFragment)
            }
            reservationBtn.setOnClickListener {
                findNavController().navigate(R.id.action_customerProfileFragment_to_reservationsHistoryFragment)
            }
            privacyBtn.setOnClickListener {

            }
            helpBtn.setOnClickListener {

            }
            logoutBtn.setOnClickListener {
                currentCustomer=null

                val settings = requireContext()!!.getSharedPreferences("user_signin", Context.MODE_PRIVATE)
                settings.edit().clear().commit()

                val intent= Intent(requireContext(),SignActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        }

        return binding.root
    }


}