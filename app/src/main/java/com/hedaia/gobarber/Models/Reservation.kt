package com.hedaia.gobarber.Models

data class Reservation(
    val CustomerID:String?=null,
    val BarberID:String?=null,
    val services:String?=null,
    val Date:String?=null,
    val TotalPrice:String?=null,
    val TotalTime:String?=null,
    var status:String="Not Yet")
