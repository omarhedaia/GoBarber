package com.hedaia.gobarber.Models

data class Reservation(
    val reservationID:String?=null,
    val customerID:String?=null,
    var barberID:Barbers?=null,
    val serviceProviderId:String?=null,
    val services:String?=null,
    val date:String?=null,
    val totalPrice:String?=null,
    val totalTime:String?=null,
    var status:String="Not Yet")
