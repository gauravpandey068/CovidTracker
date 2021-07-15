package com.gaurav.covidtracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gaurav.covidtracker.api.RetrofitBuilder
import com.gaurav.covidtracker.databinding.ActivitySearchBinding
import com.gaurav.covidtracker.model.Country
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import retrofit2.HttpException
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent =
                    Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        this.onBackPressedDispatcher.addCallback(callBack)


        binding.searchActivity.isVisible = false
        binding.progressBarSearch.isVisible = true
        val counterName = intent.getStringExtra("keyword")

        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitBuilder.api.getCountryData(counterName)
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
                binding.progressBarSearch.isVisible = false
                return@launchWhenCreated

            } catch (e: HttpException) {
                Log.d(TAG, e.message.toString())
                binding.progressBarSearch.isVisible = false
                return@launchWhenCreated

            }
            if (response.isSuccessful && response.body() != null) {
                val country: Country = response.body() as Country
                binding.progressBarSearch.isVisible = false

                fillPieChart(country)
                binding.textViewSearchCountryName.text = "${country.country}, ${country.continent}"
                Glide.with(binding.imageViewFlag.context).load(country.countryInfo.flag)
                    .into(binding.imageViewFlag)
                binding.textViewTotalCaseForCountry.text =
                    "Total Case: \n${country.cases}"
                binding.textViewTotalDeathForCountry.text =
                    "Total Death: \n${country.deaths}"
                binding.textViewTotalRecoveryForCountry.text =
                    "Total Recovery: \n${country.recovered}"
                binding.textViewTodayCases.text = "Today Cases:\n${country.todayCases}"
                binding.textViewTodayDeaths .text = "Today Deaths:\n${country.todayDeaths}"
                binding.textViewTodayRecovered.text = "Today Recovered:\n${country.todayRecovered}"
                binding.textViewTotalTest.text = "Total Tests: \n${country.tests}"
                binding.textViewActiveCase.text = "Active Cases: \n${country.active}"
                binding.searchActivity.isVisible = true

            } else {
                binding.progressBarSearch.isVisible = false
                binding.textViewError.text = "No Data Found for $counterName."
            }
        }

    }

    private fun fillPieChart(country: Country) {
        //setup Pie Entries
        val pieEntry = arrayListOf<PieEntry>()
        pieEntry.add(PieEntry(country.cases.toFloat()))
        pieEntry.add(PieEntry(country.deaths.toFloat()))
        pieEntry.add(PieEntry(country.recovered.toFloat()))

        binding.pieChart.animateXY(1000, 1000) // This 1000 is time that how much time pieChart Created

        //SetUp PieChartEntity Colors
        val pieDataSet = PieDataSet(pieEntry, "Covid Cases")
        pieDataSet.setColors(
            resources.getColor(R.color.cases),
            resources.getColor(R.color.deaths),
            resources.getColor(R.color.recovered)
        )

        //Setup Pie Data Set in Pie Data
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)

        binding.pieChart.description.isEnabled = false
        binding.pieChart.data = pieData
    }
}