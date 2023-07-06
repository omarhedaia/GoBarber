package com.hedaia.gobarber.Views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.databinding.ReservationHistoryRowBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReservationsAdapter (var clickListener: onClickListener): RecyclerView.Adapter<ReservationsAdapter.itemViewHolder>(){

    class itemViewHolder (val binding: ReservationHistoryRowBinding): RecyclerView.ViewHolder(binding.root)
    var reservationsList= emptyList<Reservation>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): itemViewHolder {
        return itemViewHolder(ReservationHistoryRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {
        val reservation=reservationsList[position]
        holder.binding.apply {

            serviceProviderTV.text = reservation.serviceProviderId
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val dateTime = LocalDateTime.parse(reservation.date, formatter)
            val date = dateTime.toLocalDate()
            val time = dateTime.toLocalTime()

            dateEtTxt.text=date.toString()
            timeEtTxt.text=time.toString()
            barberId.text=reservation.barberID!!.name

            reservationDe.setOnClickListener {
                clickListener.reservationUser(reservation)
            }

        }
    }

    override fun getItemCount(): Int {
        return reservationsList.size
    }

    fun updateList(reservationsList:List<Reservation>){
        this.reservationsList = reservationsList
        notifyDataSetChanged()

    }

    interface onClickListener{
        fun reservationUser(Reservation: Reservation)
    }
}