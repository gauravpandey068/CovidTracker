package com.gaurav.covidtracker.api

import com.gaurav.covidtracker.model.Country
import com.gaurav.covidtracker.model.World
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CovidApi {

    //get world data
    @GET("all")
    suspend fun getWorldData():Response<World>

    //get all country data
    @GET("countries")
    suspend fun getCountriesData(): Response<List<Country>>


    //get single country data
    /*@GET("countries/{countryName}")
    suspend fun getCountryData(@Path("countryName") countryName: String): Response<Country>*/

    @GET("countries/{countryName}")
    suspend fun getCountryData(@Path("countryName") countryName: String?): Response<Country>
}