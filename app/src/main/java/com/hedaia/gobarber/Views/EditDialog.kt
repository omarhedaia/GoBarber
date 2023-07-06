package com.hedaia.gobarber.Views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
            val newuserTxt = newusername.text
            val confirmemailTxt = confirmEmail.text
            val currentpassswordTxt = currentPassword.text
            val newpassswordTxt = newPassword.text
            val confirmpassswordTxt = confirmPassword.text

                when (edit) {

                    "name" ->{
                        nameLL.visibility=View.VISIBLE
                        passwordUpdateBtn.setOnClickListener {
                            if(newuserTxt.toString().isNotEmpty()){
                                errorMsg.text=" "
                                var updateCustomer= Customer(currentCustomer!!.email,
                                    currentCustomer!!.id,newuserTxt.toString(), currentCustomer!!.password,
                                currentCustomer!!.phone)
                                viewModel.updateUser(updateCustomer)
                                Toast.makeText(context,"The Username Updated Successfully!",Toast.LENGTH_SHORT).show()
                                currentCustomer=updateCustomer
                                dismiss()
                            }else{
                                errorMsg.text = "Make sure from your name the fields must be match!"
                                newuserTxt!!.clear()
                            }
                        }
                    }

                    "email" ->{
                        emailLL.visibility=View.VISIBLE
                        passwordUpdateBtn.setOnClickListener {
                            if (newemailTxt.toString().isNotEmpty()&&confirmemailTxt.toString().isNotEmpty()){
                                if(newemailTxt.toString().equals(confirmemailTxt.toString())){
                                    errorMsg.text = " "
                                    var updatecustomer =Customer(newemailTxt.toString(), currentCustomer!!.id,
                                        currentCustomer!!.name, currentCustomer!!.password,
                                        currentCustomer!!.phone)
                                    viewModel.updateUser(
                                        updatecustomer
                                    )
                                    Toast.makeText(context,"The email Updated Successfully!",Toast.LENGTH_SHORT).show()
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
                                    var updateCustomer=Customer(currentCustomer!!.email, currentCustomer!!.id, currentCustomer!!.name,md5Hash(newpassswordTxt.toString()),
                                        currentCustomer!!.phone)
                                    viewModel.updateUser(
                                        updateCustomer)
                                    Toast.makeText(context,"The Password Updated Successfully!",Toast.LENGTH_SHORT).show()
                                    currentCustomer=updateCustomer
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
                                    val updateCustomer=Customer(
                                        currentCustomer!!.email, currentCustomer!!.id,currentCustomer!!.name,
                                        currentCustomer!!.password,userphone.text.toString()
                                    )
                                    viewModel.updateUser(
                                        updateCustomer
                                    )
                                    Toast.makeText(context,"The Phone Number Updated Successfully!",Toast.LENGTH_SHORT).show()
                                    currentCustomer=updateCustomer
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