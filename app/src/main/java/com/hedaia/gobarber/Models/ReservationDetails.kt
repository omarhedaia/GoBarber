package com.hedaia.gobarber.Models

data class ReservationDetails (

        val reservationID:String?=null,
        val customerID:String?=null,
        val barberID:Barbers?=null,
        val serviceProviderId:String?=null,
        val services:String?=null,
        val date:String?=null,
        val totalPrice:String?=null,
        val totalTime:String?=null,
        var status:String="Not Yet",
        val reservationTime:Double = 0.0
        )