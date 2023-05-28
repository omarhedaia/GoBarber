package com.hedaia.gobarber.Models

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.math.min

class Repository {
    val db = Firebase.database
    private var myRef: DatabaseReference

    val barbersLiveData: MutableLiveData<List<Barbers>> = MutableLiveData()
    val CustomersLiveData: MutableLiveData<List<Customer>> = MutableLiveData()
    val servicesProvidersLiveData: MutableLiveData<List<ServiceProvider>> = MutableLiveData()
    val servicesProvidersInfoLiveData: MutableLiveData<List<ServiceProviderLocation>> = MutableLiveData()
    val servicesLiveData: MutableLiveData<List<Services>> = MutableLiveData()
    val reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val AgendaLiveData: MutableLiveData<List<Agenda>> = MutableLiveData()


    init {
        myRef = db.getReferenceFromUrl("https://goldenscisoor-default-rtdb.firebaseio.com/")
    }


    // Read from the database
    fun getBarbers(): LiveData<List<Barbers>> {
        myRef.child("Barbers").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(Barbers::class.java)!! }
            barbersLiveData.postValue(value)
        }
        return barbersLiveData
    }

    fun saveUser(user: Customer) {
        myRef.child("Customers").child(user.name.toString()).setValue(user)
        Log.d("Saved??", "SavedUser!")
        getUsers()

    }

    //Retrieve users data
    fun getUsers(): LiveData<List<Customer>> {
        myRef.child("Customers").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(Customer::class.java)!! }
            CustomersLiveData.postValue(value)

        }
        return CustomersLiveData
    }

    /*fun getUser(customer: Customer): Task<DataSnapshot> {
        return  myRef.child("Customers").child(customer.name.toString()).get()
    }*/

    fun updateUser(user: Customer,newString:String,child:String) {
        when(child){
            "password" -> {
                myRef.child("Customers").child(user.name.toString()).child("password")
                    .setValue(newString)
                Log.d("Saved??", "UpdatedPassword!")
            }
            "username" ->{
                myRef.child("Customers").child(user.name.toString()).child("name")
                    .setValue(newString)
                Log.d("Saved??", "UpdatedName!")
            }
            "email" ->{
                myRef.child("Customers").child(user.name.toString()).child("email")
                    .setValue(newString)
                Log.d("Saved??", "UpdatedEmail!")
            }
            "phone" ->{
                myRef.child("Customers").child(user.name.toString()).child("phone")
                    .setValue(newString)
                Log.d("Saved??", "UpdatedPhone!")
            }
        }



        getUsers()

    }

    fun getServiceProvider(): LiveData<List<ServiceProvider>> {
        // Read from the database
        myRef.child("ServiceProvider").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(ServiceProvider::class.java)!! }
            servicesProvidersLiveData.postValue(value)
        }
        return servicesProvidersLiveData
    }

    fun getNearbyServiceProvider(userLocation: Location): LiveData<List<ServiceProviderLocation>> {
        // Read from the database
        val providerInfoList = mutableListOf<ServiceProviderLocation>()
        myRef.child("ServiceProvider").get().addOnSuccessListener {

                val serviceProviders =
                    it.children.map { dataSnapshot -> dataSnapshot.getValue(ServiceProvider::class.java)!! }

                val nearbyServiceProviders = serviceProviders.filter { serviceProvider ->
                    val lat = serviceProvider.latitude
                    val long = serviceProvider.longitude
                    val serviceProviderLocation = Location(serviceProvider.name)
                    serviceProviderLocation.latitude = lat!!.toDouble()
                    serviceProviderLocation.longitude = long!!.toDouble()
                    userLocation.distanceTo(serviceProviderLocation) < 100000
                }


                for (provider in nearbyServiceProviders) {
                    var minWaitTime = 90000
                    var maxWaitTime = 0
                    Log.d("reservation", "provider: ${provider.name} ")


                    myRef.child("Barbers").get().addOnSuccessListener { barberSnapshot ->
                        val barbersList =
                            barberSnapshot.children.map { dataSnapshot -> dataSnapshot.getValue(Barbers::class.java)!! }
                        val barbers = barbersList.filter { barber ->
                            barber.serviceProviderID == provider.name
                        }

                        for (barber in barbers) {
                            Log.d("reservation", "barber: ${barber.name} ")
                            var totalWaitTime = 0

                            myRef.child("Reservations").get().addOnSuccessListener {
                                val value = it.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
                                Log.d("Reservation", "getReservations: $value")
                                for(reservationIn in value ) {
                                    if ((reservationIn.barberID == barber.name) && (reservationIn.status == "Not Yet")) {
                                        Log.d("reservation", "res: ${reservationIn.totalTime} ")
                                        totalWaitTime += (reservationIn.totalTime?.toInt() ?: 0)
                                        Log.d("reservation", "total time: ${totalWaitTime} ")
                                        Log.d("reservation", "min time: ${minWaitTime} ")
                                        Log.d("reservation", "max time: ${maxWaitTime} ")
                                    }
                                }



                                    minWaitTime = min(totalWaitTime, minWaitTime)
                                    maxWaitTime = max(totalWaitTime, maxWaitTime)
                                    Log.d("reservation", "total time 2: ${totalWaitTime} ")
                                    Log.d("reservation", "min time 2: ${minWaitTime} ")
                                    Log.d("reservation", "max time 2: ${maxWaitTime} ")

                                    val providerLocation = Location(provider.name)
                                    providerLocation.latitude = provider.latitude!!.toDouble()
                                    providerLocation.longitude = provider.longitude!!.toDouble()
                                    userLocation.distanceTo(providerLocation)
                                    val serviceProviderLocation = ServiceProviderLocation(
                                        provider.name,
                                        provider.longitude,
                                        provider.latitude,
                                        (userLocation.distanceTo(providerLocation) / 1000),
                                        minWaitTime,
                                        maxWaitTime
                                    )
                                    providerInfoList.add(serviceProviderLocation)

                                }.continueWith{reservationTask ->

                                    if (reservationTask.isSuccessful) {

                                            servicesProvidersInfoLiveData.postValue(providerInfoList)

                                    }


                                }


                            Log.d("reservation", "total time 3: ${totalWaitTime} ")
                            Log.d("reservation", "min time 3: ${minWaitTime} ")
                            Log.d("reservation", "max time 3: ${maxWaitTime} ")

                        }

                    }

                    Log.d("res", "getNearbyServiceProvider: ")



                }




        }



        return servicesProvidersInfoLiveData
    }


    fun getService(serviceProvider: ServiceProvider): LiveData<List<Services>> {
        // Read from the database
        myRef.child("Services").child(serviceProvider.name.toString()).get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(Services::class.java)!! }
            servicesLiveData.postValue(value)
        }
        return servicesLiveData
    }

    fun saveReservation(newReservation: Reservation) {
        val pushed=myRef.child("Reservations").push()
        pushed.setValue(Reservation(pushed.key,newReservation.customerID,newReservation.barberID,
                newReservation.serviceProviderId,newReservation.services,newReservation.date,newReservation.totalPrice,newReservation.totalTime,
            newReservation.status))
        Log.d("Saved??", "SavedReservation!")
    }

    // Read from the database
    fun getReservations(customer: Customer): LiveData<List<Reservation>> {
        var reservationUser= arrayListOf<Reservation>()
        myRef.child("Reservations").get().addOnSuccessListener {
                val value = it.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
            Log.d("Reservation", "getReservations: $value")

            for(customerin in value ){
                if((customerin.customerID==customer.name)&&(customerin.status=="Done")){
                    reservationUser.add(customerin)
                }
            }

            reservationLiveData.postValue(reservationUser)

        }
        return reservationLiveData
    }

    fun getReservationsHistory(customer: Customer):LiveData<List<Reservation>>{
        val reservationHistory = mutableListOf<Reservation>()
        val customerReservations = myRef.child("Reservations").orderByChild("customerID").equalTo(customer.name).get()
        customerReservations.addOnCompleteListener {
            if (it.isSuccessful) {
                val value =
                    it.result.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
                Log.d("reservationHistory", "getReservationsHistory: ${value}")
                reservationHistory.addAll(value)
                reservationLiveData.postValue(reservationHistory)
            }
            else{
                Log.d("reservationHistory", "getReservationsHistory: ${it.exception}")

            }
        }
        return reservationLiveData


    }


    // Read from the database
    fun getAgenda(): LiveData<List<Agenda>> {
        myRef.child("Agenda").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(Agenda::class.java)!! }
            AgendaLiveData.postValue(value)
            Log.d("Agenda", "getAgenda: $AgendaLiveData")
        }
        return AgendaLiveData
    }

}