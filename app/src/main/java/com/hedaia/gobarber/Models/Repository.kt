package com.hedaia.gobarber.Models

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Repository {
    val db = Firebase.database
    private var myRef: DatabaseReference

    val barbersLiveData: MutableLiveData<List<Barbers>> = MutableLiveData()
    val CustomersLiveData: MutableLiveData<List<Customer>> = MutableLiveData()
    val servicesProvidersLiveData: MutableLiveData<List<ServiceProvider>> = MutableLiveData()
    val servicesLiveData: MutableLiveData<List<Services>> = MutableLiveData()
    val reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val AgendaLiveData: MutableLiveData<List<Agenda>> = MutableLiveData()


    init {
        myRef = db.getReferenceFromUrl("https://goldenscisoor-default-rtdb.firebaseio.com/")
    }


    // Read from the database
    fun getBarbers(): LiveData<List<Barbers>> {
        myRef.child("Barbers").get().addOnSuccessListener {
            val value=it.children.map { dataSnapshot -> dataSnapshot.getValue(Barbers::class.java)!!}
            barbersLiveData.postValue(value)
        }
        return barbersLiveData
    }

    fun saveUser(user: Customer)
    {
        myRef.child("Customers").child(user.name.toString()).setValue(user)
        Log.d("Saved??", "SavedUser!")
        getUsers()

    }
    //Retrieve users data
    fun getUsers(): LiveData<List<Customer>> {
        myRef.child("Customers").get().addOnSuccessListener {
            val value = it.children.map {dataSnapshot -> dataSnapshot.getValue(Customer::class.java)!! }
            CustomersLiveData.postValue(value)

        }
        return CustomersLiveData
    }

    fun getServiceProvider(): LiveData<List<ServiceProvider>> {
        // Read from the database
        myRef.child("ServiceProvider").get().addOnSuccessListener {
            val value = it.children.map {dataSnapshot -> dataSnapshot.getValue(ServiceProvider::class.java)!! }
            servicesProvidersLiveData.postValue(value)
        }
        return servicesProvidersLiveData
    }

    fun getNearbyServiceProvider(userLocation: Location):LiveData<List<ServiceProvider>> {
        // Read from the database
        myRef.child("ServiceProvider").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(ServiceProvider::class.java)!! }
            val nearbyServiceProviders = value.filter { serviceProvider ->
                val lat = serviceProvider.latitude
                val long = serviceProvider.longitude
                val serviceProviderLocation = Location(serviceProvider.name)
                serviceProviderLocation.latitude = lat!!.toDouble()
                serviceProviderLocation.longitude= long!!.toDouble()

                userLocation.distanceTo(serviceProviderLocation) < 10000
            }
            servicesProvidersLiveData.postValue(nearbyServiceProviders)
        }
        return servicesProvidersLiveData
    }

    fun getService(serviceProvider:ServiceProvider): LiveData<List<Services>> {
        // Read from the database
        myRef.child("Services").child(serviceProvider.name.toString()).get().addOnSuccessListener {
            val value = it.children.map {dataSnapshot -> dataSnapshot.getValue(Services::class.java)!! }
            servicesLiveData.postValue(value)
        }
        return servicesLiveData
    }

    fun saveReservation(newReservation: Reservation) {
        myRef.child("Reservation").child(newReservation.BarberID.toString()).push().child(newReservation.CustomerID.toString()).setValue(newReservation)
        Log.d("Saved??", "SavedReservation!")
        getReservation(newReservation)
    }

    // Read from the database
    fun getReservation(newReservation:Reservation): LiveData<List<Reservation>> {
        myRef.child("Reservation").child(newReservation.BarberID.toString()).get().addOnSuccessListener {
            val value=it.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!!}
            reservationLiveData.postValue(value)
        }
        return reservationLiveData
    }

    // Read from the database
    fun getAgenda(): LiveData<List<Agenda>> {
        myRef.child("Agenda").get().addOnSuccessListener {
            val value=it.children.map { dataSnapshot -> dataSnapshot.getValue(Agenda::class.java)!!}
            AgendaLiveData.postValue(value)
            Log.d("Agenda", "getAgenda: $AgendaLiveData")
        }
        return AgendaLiveData
    }

}