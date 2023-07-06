package com.hedaia.gobarber.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barbers(
    var email:String?=null,
    var id:String?=null,
    var password:String?=null,
    var name:String?=null,
    var phone:String?=null,
    var nationality:String?=null,
    var serviceProviderID:String?=null
): Parcelable
