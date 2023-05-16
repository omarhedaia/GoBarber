package com.example.testgolden.Views.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hedaia.gobarber.Models.Services
import com.hedaia.gobarber.databinding.ServicesRowBinding

class ServicesAdapter (var clickListener: onClickListener): RecyclerView.Adapter<ServicesAdapter.itemViewHolder>(){

    class itemViewHolder (val binding: ServicesRowBinding): RecyclerView.ViewHolder(binding.root)
    var servicesList= emptyList<Services>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): itemViewHolder {
        return itemViewHolder(ServicesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: itemViewHolder, position: Int) {
        var service=servicesList[position]
        holder.binding.apply {
            serviceName.text = service.name
            servicePrice.text = "${service.price} SAR."
            serviceName.setOnCheckedChangeListener { _, _ ->
                if(serviceName.isChecked==true){
                    clickListener.Userservices(service,serviceName.isChecked)
                }else{
                    clickListener.Userservices(service,serviceName.isChecked)
                }

            }
        }
    }

    override fun getItemCount(): Int {
      return servicesList.size
    }

    fun updateList(servicesList:List<Services>){
        this.servicesList = servicesList
        notifyDataSetChanged()

    }

    interface onClickListener{
        fun Userservices(services: Services,checked:Boolean)

    }

}
