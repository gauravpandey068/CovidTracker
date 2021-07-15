package com.gaurav.covidtracker.model

data class CountryInfo(
    val id: Int,
    val flag: String,
    val iso2: String,
    val iso3: String,
    val lat: Double,
    val long: Double,
)