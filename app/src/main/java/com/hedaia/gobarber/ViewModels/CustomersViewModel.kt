package com.hedaia.gobarber.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.hedaia.gobarber.Models.Agenda
import com.hedaia.gobarber.Models.Barbers
import com.hedaia.gobarber.Models.Customer
import com.hedaia.gobarber.Models.Repository
import com.hedaia.gobarber.Models.Reservation
import com.hedaia.gobarber.Models.ServiceProvider
import com.hedaia.gobarber.Models.Services

class CustomersViewModel(application: Application): AndroidViewModel(application) {
    var repository: Repository

    init {

        repository = Repository()

    }
    fun getBarbers(): LiveData<List<Barbers>> {
        return repository.getBarbers()
    }


    fun saveUser(newUser: Customer) {
        return repository.saveUser(newUser)
    }

    fun getUsers(): LiveData<List<Customer>> {
        return repository.getUsers()
    }


    fun getServiceProvider(): LiveData<List<ServiceProvider>> {
        return repository.getServiceProvider()
    }


    fun getServices(serviceProvider: ServiceProvider): LiveData<List<Services>> {
        return repository.getService(serviceProvider)
    }

    fun saveReservation(reservation: Reservation) {
        return repository.saveReservation(reservation)
    }
    fun getReservation(reservation:Reservation): LiveData<List<Reservation>> {
        return repository.getReservation(reservation)
    }
    fun getAgenda(): LiveData<List<Agenda>> {
        return repository.getAgenda()
    }
}