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
import com.google.android.material.snackbar.Snackbar
import com.hedaia.gobarber.Models.Customer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.databinding.FragmentSingUpBinding
import java.math.BigInteger
import java.security.MessageDigest


class SingUpFragment : Fragment() {


    private lateinit var binding:FragmentSingUpBinding
    private lateinit var viewModel: CustomersViewModel
    private var users= arrayListOf<Customer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentSingUpBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this)[CustomersViewModel::class.java]
        viewModel.getUsers().observe(viewLifecycleOwner){
            users.clear()
            users.addAll(it)
            Log.d("TAG", "onCreate: $users")
        }
        binding.apply {
            signupBtn.setOnClickListener {
                val userEmail=useremail.text
                val userConfirmEmail=confirmEmail.text
                val userPass=userPassword.text
                val userConfirmPass=confirmPassword.text
                val userName=username.text
                val userPhone=userPhone.text
                if (userEmail!!.isNotEmpty() && userPass!!.isNotEmpty() &&
                    userName!!.isNotEmpty() && userPhone!!.isNotEmpty()) {
                    if (userEmail.toString() == userConfirmEmail.toString()) {
                        if (userPass.toString() == userConfirmPass.toString()) {
                            val newUser = Customer(
                                userEmail.toString(), userName.toString(),
                                md5Hash(userPass.toString()), userPhone.toString()
                            )
                            viewModel.saveUser(newUser)
                            //Toast.makeText(requireContext(), "Customer Added", Toast.LENGTH_LONG).show()
                            Log.d("Data", "AddProcess: $newUser ")
                            findNavController().navigate(R.id.action_singUpFragment_to_signInFragment)
                             }else{
                                 Snackbar.make(requireView(),"Passwords must match",Snackbar.LENGTH_LONG).show()
//                            Toast.makeText(requireContext(), "Passwords must be match", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Snackbar.make(requireView(),"Emails must match",Snackbar.LENGTH_LONG).show()
//                        Toast.makeText(requireContext(), "Emails must be match", Toast.LENGTH_LONG).show()
                    }

                        } else {

                            Snackbar.make(requireView(),"Please, Enter all of your information.",Snackbar.LENGTH_LONG).show()

//                            Toast.makeText(
//                                context,
//                                "Enter all your information, Please!",
//                                Toast.LENGTH_LONG
//                            ).show()
                        }
                    }

        }


        return binding.root
    }
    private fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        return String.format("%032x", bigInt)
    }


}