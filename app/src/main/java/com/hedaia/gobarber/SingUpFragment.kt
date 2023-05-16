package com.hedaia.gobarber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hedaia.gobarber.Models.Customer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.databinding.FragmentSingUpBinding
import java.math.BigInteger
import java.security.MessageDigest


class SingUpFragment : Fragment() {


    private lateinit var binding:FragmentSingUpBinding
    lateinit var viewModel: CustomersViewModel
    var users= arrayListOf<Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSingUpBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)
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
                   /* if(userEmail==userConfirmEmail){
                        if(userPass==userConfirmPass){*/
                            var newUser = Customer(
                                userEmail.toString(), userName.toString(),
                                md5Hash(userPass.toString()), userPhone.toString())
                            viewModel.saveUser(newUser)
                            Toast.makeText(context, "Customer Added", Toast.LENGTH_LONG).show()
                            Log.d("Data", "AddProcess: $newUser ")
                            findNavController().navigate(R.id.action_singUpFragment_to_signInFragment)
                       /* }else{
                            Toast.makeText(context, "Passwords must be match", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "Emails must be match", Toast.LENGTH_LONG).show()
                    }*/

                }else{
                    Toast.makeText(context, "Enter all your information, Please!", Toast.LENGTH_LONG).show()
                }
            }
        }


        return binding.root
    }
    private fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        val UserPassword =String.format("%032x", bigInt)
        return UserPassword
    }


}