package com.hedaia.gobarber.Models

data class ServiceProviderLocation(
    var name:String?=null,
    var longitude:String?=null,
    var latitude:String?=null,
    var distance:Float?=null,
    var minimumWaitTime:Int = 0,
    var maximumWaitTime:Int = 0
)
