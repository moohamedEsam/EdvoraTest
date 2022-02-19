package com.example.edvoratest.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edvoratest.dataModels.Ride
import com.example.edvoratest.dataModels.User
import com.example.edvoratest.utils.Filters
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val _rides = mutableStateOf<List<Ride>>(emptyList())
    var rides: State<List<Ride>> = _rides
    private val _filteredRides = mutableStateOf<List<Ride>>(emptyList())
    val filteredRides: State<List<Ride>> = _filteredRides
    private val _filters = mutableStateOf<Filters>(Filters.Nearest())
    val user = User(
        Random.nextInt(10, 100),
        "mohamed esam",
        ""
    )
    val filters: State<Filters> = _filters
    var pastCount = 0
    var upcomingCount = 0

    fun setFilters(value: Filters) = viewModelScope.launch {
        if (_filters.value != value) {
            _filters.value = value
            filterBy()
        }
    }



    init {
        fillInitialData()
        sortByNearest()
    }

    private fun fillInitialData() {
        _rides.value = buildList {
            repeat(40) { id ->
                val ride = Ride(
                    "00$id",
                    IntArray(6) { Random.nextInt(10, 100) },
                    Date().time + Random.nextLong(
                        Long.MIN_VALUE / 1000000,
                        Long.MAX_VALUE / 1000000
                    ),
                    "url",
                    "cairo",
                    "egypt"
                )
                ride.origin_path.sort()
                if (ride.date >= Date().time)
                    upcomingCount++
                else
                    pastCount++
                add(ride)
            }
        }
    }

    private fun sortByNearest() = viewModelScope.launch {
        Log.i("MainViewModel", "sortByNearest: user station code -> ${user.station_code}")
        _rides.value.forEach { ride ->
            ride.distance = Int.MAX_VALUE
            ride.origin_path.forEach { staion ->
                ride.distance = minOf(ride.distance, abs(user.station_code - staion))
            }
        }
        _rides.value = _rides.value.sortedBy { ride ->
            ride.distance
        }
    }

    fun filterBy() {
        when (_filters.value) {
            is Filters.Nearest -> {
                sortByNearest()
            }
            is Filters.Upcoming -> {
                _filteredRides.value = _rides.value.filter { it.date >= Date().time }
            }
            is Filters.Past -> {
                _filteredRides.value = _rides.value.filter { it.date < Date().time }
            }
        }
    }

    fun getCities() = buildSet {
        _rides.value.forEach {
            add(it.city)
        }
    }

    fun getStates() = buildSet {
        _rides.value.forEach {
            add(it.state)
        }
    }

    fun filterByCity(city: String){
        _rides.value = _rides.value.filter { it.city ==  city}
        _filteredRides.value = _filteredRides.value.filter { it.city ==  city}
    }

    fun filterByState(state: String){
        _rides.value = _rides.value.filter { it.state ==  state}
        _filteredRides.value = _filteredRides.value.filter { it.state ==  state}
    }

}