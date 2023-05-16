package com.hedaia.gobarber.Views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hedaia.gobarber.Models.Agenda
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.databinding.HomeItemLayoutBinding

class AgendaAdapter (var clickListener: onClick): RecyclerView.Adapter<AgendaAdapter.itemViewHolder>(){

    class itemViewHolder (val binding: HomeItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    var agendaList= emptyList<Agenda>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemViewHolder {
        return itemViewHolder(HomeItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {
        var agenda=agendaList[position]
        holder.binding.apply {
            salonName.text=agenda.featureName
            descriptionItemIv.text=agenda.featureDesc
            ReservationButton.setOnClickListener {
                clickListener.toOffer(agenda.name!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return agendaList.size
    }



    fun updateList(agendaList:ArrayList<Agenda>){
        this.agendaList = agendaList
        notifyDataSetChanged()
    }

    interface onClick{
        fun toOffer(ServiceProvider:String)
    }
}