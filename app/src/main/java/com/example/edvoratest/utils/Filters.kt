package com.example.edvoratest.utils

sealed class Filters(val value: String) {
    class Nearest : Filters("Nearest")
    class Upcoming(count: Int) : Filters("Upcoming($count)")
    class Past(count: Int) : Filters("Past($count)")
}
