package com.example.edvoratest.dataModels

data class Ride(
    var id: String,
    var origin_path: IntArray,
    var date: Long,
    var map_url: String,
    var state: String,
    var city: String,
    var distance: Int = 0
)
