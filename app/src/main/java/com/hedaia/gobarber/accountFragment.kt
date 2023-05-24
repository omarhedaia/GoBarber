package com.hedaia.gobarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.Views.EditDialog
import com.hedaia.gobarber.databinding.FragmentAccountBinding


class accountFragment : Fragment() {

    private lateinit var binding:FragmentAccountBinding
    lateinit var viewModel: CustomersViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAccountBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)


        binding.apply {

            welcomingtxt.text="Welcome ${currentCustomer!!.name}!"
            username.setText(currentCustomer!!.name)
            useremail.setText(currentCustomer!!.email)
            userPhone.setText(currentCustomer!!.phone)


            userPassword.setOnClickListener {
                var editDialog =
                    EditDialog(requireContext(),
                        "password", this@accountFragment)
                editDialog.show()
                editDialog.setCancelable(false)
            }

            useremail.setOnClickListener {
                var editDialog =
                    EditDialog(requireContext(),
                        "email", this@accountFragment)
                editDialog.show()
                editDialog.setCancelable(false)
            }
            userPhone.setOnClickListener {
                var editDialog =
                    EditDialog(requireContext(),
                        "phone", this@accountFragment)
                editDialog.show()
                editDialog.setCancelable(false)
            }
            backToProfileIV.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_customerProfileFragment)
            }


        }

        return binding.root
    }



}