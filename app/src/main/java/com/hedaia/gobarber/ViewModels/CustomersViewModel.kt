package com.hedaia.gobarber.ViewModels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hedaia.gobarber.Models.*

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
    fun updateUser(user: Customer) {
        return repository.updateUser(user)
    }


    fun getServiceProvider(): LiveData<List<ServiceProvider>> {
        return repository.getServiceProvider()
    }

    fun getNearbyServiceProvider(userLocation: Location): LiveData<List<ServiceProviderLocation>> {

        return repository.getNearbyServiceProvider(userLocation)
    }


    fun getServices(serviceProvider: ServiceProvider): LiveData<List<Services>> {
        return repository.getService(serviceProvider)
    }

    fun saveReservation(reservation: Reservation) {
        return repository.saveReservation(reservation)
    }
    fun getReservations(customer: Customer): LiveData<List<Reservation>> {
        return repository.getReservations(customer)
    }
    fun getAgenda(): LiveData<List<Agenda>> {
        return repository.getAgenda()
    }

    fun getReservationsHistory(customer: Customer):LiveData<List<Reservation>>{

        return repository.getReservationsHistory(customer)
    }
}