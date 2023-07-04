package com.hedaia.gobarber.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reservation(
    val reservationID:String?=null,
    val customerID:String?=null,
    val barberID:String?=null,
    val serviceProviderId:String?=null,
    val services:String?=null,
    val date:String?=null,
    val showUpTime:String?=null,
    val totalPrice:String?=null,
    val totalTime:String?=null,
    var status:String="Not Yet"):Parcelable
