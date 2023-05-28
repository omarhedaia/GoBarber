package com.hedaia.gobarber

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.hedaia.gobarber.databinding.FragmentSignInBinding
import java.math.BigInteger
import java.security.MessageDigest



class SignInFragment : Fragment() {

    lateinit var binding:FragmentSignInBinding
    lateinit var viewModel: CustomersViewModel
    lateinit var userSigninSharedPref:SharedPreferences
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

        userSigninSharedPref = requireActivity().getSharedPreferences("user_signin", Context.MODE_PRIVATE)

        viewModel.getUsers().observe(viewLifecycleOwner){
            users.clear()
            users.addAll(it)
            Log.d("TAG", "onCreate: $users ")

            val rememberMeBoolean = userSigninSharedPref.getBoolean("rememberMeCB",false)
            if (rememberMeBoolean)
            {
                binding.rememberMeCb.isChecked = rememberMeBoolean
                val userEmail = userSigninSharedPref.getString("userEmail","")!!
                val userPassword = userSigninSharedPref.getString("userPassword","")!!
                for (user in users) {
                    println(" for (user in users), ${user.name} in $users")
                    if (userEmail.equals(user.email)) {
                        checkUserLogin(userPassword, user)
                    }
                }

            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userEmail = ""
        var userPassword = ""



        binding.apply {
            signinBtn.setOnClickListener {
                 userEmail = useremailTxt.text.toString()
                 userPassword = userpasswordTxt.text.toString()
                if(userEmail!!.isNotEmpty()&&userPassword!!.isNotEmpty()){
                    for (user in users) {
                        println(" for (user in users), ${user.name} in $users")
                        if (userEmail == user.email) {
                            checkUserLogin(userPassword, user)
                        }
                    }

                }else{
                    Snackbar.make(requireView(),"Please enter your email and password!",Snackbar.LENGTH_LONG).show()
//                    Toast.makeText(requireContext(),"Please Enter your information", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun checkUserLogin(password: String, user: Customer) {
        if(md5Hash(password)==user.password!!){
            currentCustomer=user
            Log.d("MainActivity", "checkUserLogin: $currentCustomer")
            val intent= Intent(requireContext(),CustomerActivity::class.java)
            if (binding.rememberMeCb.isChecked)
            {
                val userSharedPreferencesEditor = userSigninSharedPref.edit()
                userSharedPreferencesEditor.putBoolean("rememberMeCB",true)
                userSharedPreferencesEditor.putString("userEmail",user.email)!!
                userSharedPreferencesEditor.putString("userPassword",password)!!
                userSharedPreferencesEditor.apply()
            }

            startActivity(intent)
            requireActivity().finish()
        }else{

            Snackbar.make(requireView(),"Please make sure of your password",Snackbar.LENGTH_LONG).show()
//            Toast.makeText(requireContext(),"Please make sure from your password", Toast.LENGTH_SHORT).show()
            binding.useremailTxt.text!!.clear()
            binding.userpasswordTxt.text!!.clear()
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