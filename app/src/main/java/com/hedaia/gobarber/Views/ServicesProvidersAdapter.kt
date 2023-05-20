package com.hedaia.gobarber.Views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.databinding.HomeItemLayoutBinding
import com.hedaia.gobarber.databinding.ServiceProvidersRowBinding

class ServicesProvidersAdapter(var clickListener: onClick): RecyclerView.Adapter<ServicesProvidersAdapter.itemViewHolder>(){

    class itemViewHolder (val binding: ServiceProvidersRowBinding): RecyclerView.ViewHolder(binding.root)

    var serviceProvidersList= emptyList<ServiceProvider>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemViewHolder {
        return itemViewHolder(ServiceProvidersRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {
        var serviceProvider=serviceProvidersList[position]
        holder.binding.apply {
            salonName.text=serviceProvider.name
            //distancekmTv.text=
            //distanceTimeTv.text=
            salonCv.setOnClickListener {
                clickListener.setserviceProvider(serviceProvider)
            }
        }
    }

    override fun getItemCount(): Int {
        return serviceProvidersList.size
    }



    fun updateList(serviceProvidersList:List<ServiceProvider>){
        this.serviceProvidersList = serviceProvidersList
        notifyDataSetChanged()
    }
    interface onClick{
        fun setserviceProvider(serviceProvider:ServiceProvider)
    }
}