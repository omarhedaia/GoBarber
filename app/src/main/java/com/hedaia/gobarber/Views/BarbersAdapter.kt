package com.hedaia.gobarber.Views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hedaia.gobarber.Models.Barbers
import com.hedaia.gobarber.databinding.BarbersRowBinding

class BarbersAdapter(var clickListener: onClick): RecyclerView.Adapter<BarbersAdapter.itemViewHolder>(){

    class itemViewHolder (val binding: BarbersRowBinding): RecyclerView.ViewHolder(binding.root)

    var barbersList= emptyList<Barbers>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemViewHolder {
       return itemViewHolder(BarbersRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {
        var barber=barbersList[position]
        holder.binding.apply {
            barberName.text=barber.name
            barberName.setOnCheckedChangeListener { _, _ ->
               if(barberName.isChecked){
                   clickListener.setBarberReservation(barber,barberName.isChecked)
               }else{
                   clickListener.setBarberReservation(barber,barberName.isChecked)
               }

           }
        }
    }

    override fun getItemCount(): Int {
        return barbersList.size
    }



    fun updateList(barbersList:ArrayList<Barbers>){
        this.barbersList = barbersList
        notifyDataSetChanged()
    }

    interface onClick{
        fun setBarberReservation(barber:Barbers,isChecked:Boolean)
    }

}