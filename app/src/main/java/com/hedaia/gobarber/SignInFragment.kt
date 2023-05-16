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
import com.hedaia.gobarber.databinding.FragmentSignInBinding
import java.math.BigInteger
import java.security.MessageDigest



class SignInFragment : Fragment() {

    lateinit var binding:FragmentSignInBinding
    lateinit var viewModel: CustomersViewModel
    var users = arrayListOf<Customer>()

    companion object userData{
        var currentCustomer: Customer? =null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater)

        viewModel= ViewModelProvider(this).get(CustomersViewModel::class.java)

        viewModel.getUsers().observe(viewLifecycleOwner){
            users.clear()
            users.addAll(it)
            Log.d("TAG", "onCreate: $users ")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            signinBtn.setOnClickListener {
                val userEmail=useremailTxt.text
                val userPassword=userpasswordTxt.text
                if(userEmail!!.isNotEmpty()&&userPassword!!.isNotEmpty()){
                    for (user in users) {
                        println(" for (user in users), ${user.name} in $users")
                        if (userEmail.toString() == user.email) {
                            checkUserLogin(userPassword.toString(), user)
                            userEmail.clear()
                            userPassword.clear()
                        }
                    }
                }else{
                    Toast.makeText(context,"Please Enter you information", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun checkUserLogin(password: String, user: Customer) {
        if(md5Hash(password)==user.password!!){
            currentCustomer=user
            Log.d("MainActivity", "checkUserLogin: $currentCustomer")
            val intent= Intent(requireContext(),CustomerActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }else{
            Toast.makeText(context,"Please make sure from your password", Toast.LENGTH_SHORT).show()
        }
    }

    //This function to hash password before compare it with one in database
    private fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        val UserPassword =String.format("%032x", bigInt)
        return UserPassword
    }
}