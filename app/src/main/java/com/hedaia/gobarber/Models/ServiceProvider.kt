package com.hedaia.gobarber.Models

data class ServiceProvider(
    var name:String?=null,
    var longitude:String?=null,
    var latitude:String?=null,
    var status:String?="Closed",
    var admin:String?=null
)
