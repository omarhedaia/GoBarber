package com.hedaia.gobarber.Views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.ReservationsHistoryFragment
import com.hedaia.gobarber.ViewModels.CustomersViewModel
import com.hedaia.gobarber.accountFragment
import com.hedaia.gobarber.databinding.DetailsDialogBinding
import com.hedaia.gobarber.databinding.EditDialogBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailsDialog(context: Context, val reservation: Reservation, val required: ReservationsHistoryFragment) :
    Dialog(context) {

        lateinit var binding: DetailsDialogBinding
        lateinit var viewModel: CustomersViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DetailsDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(required).get(CustomersViewModel::class.java)
        binding.apply {

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val dateTime = LocalDateTime.parse(reservation.date, formatter)
            val date = dateTime.toLocalDate()
            val time = dateTime.toLocalTime()

            timeEtTxt.text= time.toString()
            dateEtTxt.text=date.toString()

            serviceProvider.text=reservation.serviceProviderId
            barberName.text=reservation.barberID!!.name
            price.text=reservation.totalPrice
            servicesTxt.text=reservation.services

            ReservationButton.setOnClickListener {
                viewModel.saveReservation(Reservation("",reservation.customerID,
                reservation.barberID,reservation.serviceProviderId,reservation.services, LocalDateTime.now().toString(),
                    reservation.totalPrice,reservation.totalTime,))
                dismiss()
            }

            cancelBtn.setOnClickListener {
                dismiss()
            }

        }


    }
}