package com.hedaia.gobarber

import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.databinding.FragmentCurrentReserrvationBinding
import java.text.SimpleDateFormat
import java.util.*




class CurrentReserrvationFragment : Fragment() {

    private var currentReservation: Reservation? = null
    lateinit var binding:FragmentCurrentReserrvationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            currentReservation = it.getParcelable("customer_reservation",Reservation::class.java)
            else
                currentReservation = it.getParcelable("customer_reservation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCurrentReserrvationBinding.inflate(layoutInflater)

        binding.apply {

            if (currentReservation!=null) {

                timeIsUpTv.visibility = View.GONE
                showUpTimeLeftTitle.visibility = View.VISIBLE
                showUpTimeLeftCounterTv.visibility = View.VISIBLE

                val currentTime = Calendar.getInstance().timeInMillis
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val date: Date = dateFormat.parse(currentReservation!!.showUpTime!!)!!

                val calendar: Calendar = Calendar.getInstance()
                calendar.time = date
                val showUpTime = calendar.timeInMillis
                val totalWaitTime = showUpTime - currentTime

                val countdownTimer = object : CountDownTimer(totalWaitTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Code to be executed on each tick of the countdown timer
                        showUpTimeLeftCounterTv.text = formatTime(millisUntilFinished)
                    }

                    override fun onFinish() {
                        // Code to be executed when the countdown timer finishes
                        timeIsUpTv.visibility = View.VISIBLE
                        showUpTimeLeftTitle.visibility = View.GONE
                        showUpTimeLeftCounterTv.visibility = View.GONE
                    }
                }.start()

                serviceProviderTV.text = currentReservation!!.serviceProviderId
                barberNameTv.text = currentReservation!!.barberID!!.name
                reservingTimeTv.text = formatDateAndTime(currentReservation!!.date!!)
                showUpTimeTv.text = formatDateAndTime(currentReservation!!.showUpTime!!)
                totalpriceTv.text = currentReservation!!.totalPrice
            }

        }


        return binding.root
    }

    fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val seconds = totalSeconds %60
        val minutes = totalSeconds /60 %60
        val hours = totalSeconds /60 /60 %24
        val days = totalSeconds /60 /60 /24 %30



        return String.format("%02d:%02d:%02d:%02d",days,hours, minutes, seconds)
    }

    fun formatDateAndTime(dateAndTime: String): String {

        var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val date: Date = dateFormat.parse(dateAndTime)!!
        dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }


}