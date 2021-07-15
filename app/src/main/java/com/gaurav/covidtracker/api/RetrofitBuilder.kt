package com.gaurav.covidtracker.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    val api: CovidApi by lazy {
        Retrofit.Builder().baseUrl("https://disease.sh/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidApi::class.java)
    }
}