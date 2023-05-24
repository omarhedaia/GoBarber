package com.hedaia.gobarber.Views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hedaia.gobarber.Models.Customer
import com.hedaia.gobarber.SignInFragment.userData.currentCustomer
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.accountFragment
import com.hedaia.gobarber.databinding.EditDialogBinding
import java.math.BigInteger
import java.security.MessageDigest

class EditDialog(context: Context, val edit: String, val required: accountFragment) :
    Dialog(context) {

    lateinit var binding: EditDialogBinding
    lateinit var viewModel: CustomersViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(required).get(CustomersViewModel::class.java)


        binding.apply {

            val newemailTxt = useremail.text
            val confirmemailTxt = confirmEmail.text
            val currentpassswordTxt = currentPassword.text
            val newpassswordTxt = newPassword.text
            val confirmpassswordTxt = confirmPassword.text

                when (edit) {
                    "email" ->{
                        emailLL.visibility=View.VISIBLE
                        passwordUpdateBtn.setOnClickListener {
                            if (newemailTxt.toString().isNotEmpty()&&confirmemailTxt.toString().isNotEmpty()){
                                if(newemailTxt.toString().equals(confirmemailTxt.toString())){
                                    errorMsg.text = " "
                                    viewModel.updateUser(
                                        currentCustomer!!,
                                        newemailTxt.toString(), edit)
                                    var updatecustomer =Customer(newemailTxt.toString(), currentCustomer!!.name,
                                        currentCustomer!!.password,currentCustomer!!.phone)
                                        currentCustomer=updatecustomer
                                    dismiss()
                                }else{
                                    errorMsg.text = "Make sure from your email the fields must be match!"
                                    newpassswordTxt!!.clear()
                                    confirmpassswordTxt!!.clear()
                                }
                            }
                        }
                    }


                    "password" -> {
                        passwordsLL.visibility = View.VISIBLE
                        passwordUpdateBtn.setOnClickListener {
                            if (md5Hash(currentpassswordTxt.toString()) == currentCustomer!!.password) {
                                errorMsg.text = " "
                                if (newpassswordTxt.toString()
                                        .equals(confirmpassswordTxt.toString())
                                ) {
                                    errorMsg.text = " "
                                    viewModel.updateUser(
                                        currentCustomer!!,
                                        md5Hash(newpassswordTxt.toString()), edit
                                    )
                                    var updatecustomer =Customer(currentCustomer!!.email, currentCustomer!!.name,md5Hash(newpassswordTxt.toString()),
                                        currentCustomer!!.phone)
                                    currentCustomer=updatecustomer
                                    dismiss()
                                } else {
                                    errorMsg.text = "Passwords must be match, Try Again!"
                                    newpassswordTxt!!.clear()
                                    confirmpassswordTxt!!.clear()
                                }
                            } else {
                                errorMsg.text = "Please Make sure from your password!"
                            }
                        }
                    }

                    "phone" -> {
                        phoneLL.visibility = View.VISIBLE
                        passwordUpdateBtn.setOnClickListener {
                            if (userphone.text.toString().isNotEmpty()) {
                                if (userphone.text!!.equals(currentCustomer!!.phone)) {
                                    errorMsg.text = "Your phone number matches your old number"
                                } else {
                                    errorMsg.text = " "
                                    viewModel.updateUser(
                                        currentCustomer!!,
                                        userphone.text.toString(),
                                        edit)
                                    var updatecustomer =Customer(
                                        currentCustomer!!.email, currentCustomer!!.name,
                                        currentCustomer!!.password,userphone.text.toString())
                                        currentCustomer=updatecustomer
                                    dismiss()
                                }
                            }
                        }
                    }

                }

                cancelBtn.setOnClickListener {
                    dismiss()
                }


        }

    }


    private fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        val UserPassword = String.format("%032x", bigInt)
        return UserPassword
    }

}