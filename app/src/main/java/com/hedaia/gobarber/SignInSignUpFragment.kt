package com.hedaia.gobarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hedaia.gobarber.databinding.FragmentSignInSignUpBinding


class SignInSignUpFragment : Fragment() {

    lateinit var binding:FragmentSignInSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInSignUpBinding.inflate(layoutInflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            signInNavBtn.setOnClickListener{

                findNavController().navigate(R.id.action_signInSignUpFragment_to_signInFragment)

            }
            signUpNavBtn.setOnClickListener {
                findNavController().navigate(R.id.action_signInSignUpFragment_to_singUpFragment)

            }


        }

    }

}