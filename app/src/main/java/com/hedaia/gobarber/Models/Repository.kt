package com.hedaia.gobarber.Models

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.QuerySnapshot
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
    val servicesProvidersInfoLiveData: MutableLiveData<List<ServiceProviderLocation>> =
        MutableLiveData()
    val servicesLiveData: MutableLiveData<List<Services>> = MutableLiveData()
    val reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val AgendaLiveData: MutableLiveData<List<Agenda>> = MutableLiveData()
    val reservationDetailsLiveData:MutableLiveData<ReservationDetails> = MutableLiveData()


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

    //Updated to ID
    fun saveUser(user: Customer) {
        val pushed=myRef.child("Customers").push()
            pushed.setValue(Customer(user.email,pushed.key,user.name,user.password,user.phone))
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

    fun updateUser(user: Customer) {
                myRef.child("Customers").child(user.id.toString())
                    .setValue(user)
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
                            val value =
                                it.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
                            Log.d("Reservation", "getReservations: $value")
                            for (reservationIn in value) {
                                if ((reservationIn.barberID== barber) && (reservationIn.status == "Not Yet")) {
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

                        }.continueWith { reservationTask ->

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


    fun getNearbyServiceProvider2(userLocation: Location): LiveData<List<ServiceProviderLocation>> {
        // Read from the database
        val providerInfoList = mutableListOf<ServiceProviderLocation>()
        val center = GeoLocation(userLocation.latitude, userLocation.longitude)
        val radiusInM = 50.0 * 1000.0


        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<DataSnapshot>> = ArrayList()
        for (b in bounds) {
            val q = myRef.child("ServiceProvider")
                .orderByChild("geohash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<ServiceProvider> = ArrayList()

                for (task in tasks) {
                    val snap = task.result
                    val serviceProviders =
                        snap.children.map { snapShot -> snapShot.getValue(ServiceProvider::class.java)!! }
                    for (doc in serviceProviders) {
                        val lat = doc.latitude!!.toDouble()
                        val lng = doc.longitude!!.toDouble()

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }

                    for (provider in matchingDocs) {

                        var minWaitTime = 90000
                        var maxWaitTime = 0
                        Log.d("reservation", "provider: ${provider.name} ")

                        myRef.child("Barbers").get().addOnSuccessListener { barberSnapshot ->
                            val barbersList =
                                barberSnapshot.children.map { dataSnapshot ->
                                    dataSnapshot.getValue(
                                        Barbers::class.java
                                    )!!
                                }
                            val barbers = barbersList.filter { barber ->
                                barber.serviceProviderID == provider.name
                            }

                            for (barber in barbers) {
                                Log.d("reservation", "barber: ${barber.name} ")
                                var totalWaitTime = 0

                                myRef.child("Reservations").get().addOnSuccessListener {
                                    val value = it.children.map { dataSnapshot ->
                                        dataSnapshot.getValue(Reservation::class.java)!!
                                    }
                                    Log.d("Reservation", "getReservations: $value")
                                    for (reservationIn in value) {
                                        if ((reservationIn.barberID!!.id == barber.id) && (reservationIn.status == "Not Yet")) {
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

                                }.continueWith { reservationTask ->

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

            }



        return servicesProvidersInfoLiveData
    }


    fun getCurrentReservation(customer: Customer):LiveData<ReservationDetails> {
        myRef.child("Reservations").orderByChild("date").get().addOnSuccessListener {

            val openTime = 8.0
            val closeTime = 11.0
            var reservationTimeLeft = openTime
            var reservationTime = 0.0

            val reservations =
                it.children.map { snap ->
                    snap.getValue(Reservation::class.java)
                }

            for (reservation in reservations) {
                if (reservation!!.customerID != customer.id) {
//                    totalWorkingHours = totalWorkingHours - reservation.totalTime!!.toInt()
                    reservationTimeLeft =
                        reservationTimeLeft + (reservation.totalTime!!.toInt() / 60) - 0.10
                } else {
                    reservationTime = reservationTimeLeft - 0.10
                    val reservationDetails = ReservationDetails(
                        reservation.reservationID,
                        reservation.customerID,
                        reservation.barberID,
                        reservation.serviceProviderId,
                        reservation.services,
                        reservation.date,
                        reservation.totalPrice,
                        reservation.totalTime,
                        reservation.status,
                        reservationTime
                    )

                    reservationDetailsLiveData.postValue(reservationDetails)
                    break


                }


            }


        }

        return reservationDetailsLiveData

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
        val pushed = myRef.child("Reservations").push()
        pushed.setValue(
            Reservation(
                pushed.key,
                newReservation.customerID,
                newReservation.barberID,
                newReservation.serviceProviderId,
                newReservation.services,
                newReservation.date,
                newReservation.totalPrice,
                newReservation.totalTime,
                newReservation.status
            )
        )
        Log.d("Saved??", "SavedReservation!")
    }

    // Read from the database
    fun getReservations(customer: Customer): LiveData<List<Reservation>> {
        var reservationUser = arrayListOf<Reservation>()
        myRef.child("Reservations").get().addOnSuccessListener {
            val value =
                it.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
            Log.d("Reservation", "getReservations: $value")

            for (customerin in value) {
                if ((customerin.customerID == customer.id) && (customerin.status == "Done")) {
                    reservationUser.add(customerin)
                }
            }

            reservationLiveData.postValue(reservationUser)

        }
        return reservationLiveData
    }

    fun getReservationsHistory(customer: Customer): LiveData<List<Reservation>> {
        val reservationHistory = mutableListOf<Reservation>()
        val customerReservations =
            myRef.child("Reservations").orderByChild("customerID").equalTo(customer.id).get()
        customerReservations.addOnCompleteListener {
            if (it.isSuccessful) {
                val value =
                    it.result.children.map { dataSnapshot -> dataSnapshot.getValue(Reservation::class.java)!! }
                Log.d("reservationHistory", "getReservationsHistory: ${value}")
                reservationHistory.addAll(value)
                reservationLiveData.postValue(reservationHistory)
            } else {
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