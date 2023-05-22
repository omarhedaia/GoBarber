package com.hedaia.gobarber.Models

data class Reservation(
    val customerID:String?=null,
    val barberID:String?=null,
    val services:String?=null,
    val date:String?=null,
    val totalPrice:String?=null,
    val totalTime:String?=null,
    var status:String="Not Yet")
