package com.gaurav.covidtracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaurav.covidtracker.adapter.CountryAdapter
import com.gaurav.covidtracker.api.RetrofitBuilder
import com.gaurav.covidtracker.databinding.ActivityMainBinding
import com.gaurav.covidtracker.model.World
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import retrofit2.HttpException
import java.io.IOException

const val TAG = "Main Activity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var countryAdapter: CountryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardView.isVisible = false

        setRecycleViewAdapter()

        lifecycleScope.launchWhenCreated {
            binding.progressBar.isVisible = true
            val data = try {
                RetrofitBuilder.api.getWorldData()
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
                binding.progressBar.isVisible = false
                return@launchWhenCreated

            } catch (e: HttpException) {
                Log.d(TAG, e.message.toString())
                binding.progressBar.isVisible = false
                return@launchWhenCreated

            }
            val response = try {
                RetrofitBuilder.api.getCountriesData()
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
                binding.progressBar.isVisible = false
                return@launchWhenCreated

            } catch (e: HttpException) {
                Log.d(TAG, e.message.toString())
                binding.progressBar.isVisible = false
                return@launchWhenCreated

            }
            if (response.isSuccessful && response.body() != null && data.isSuccessful && data.body() != null) {
                val post: World = data.body() as World
                filldata(post)
                countryAdapter.countries = response.body()!!

            } else {
                Log.d(TAG, "Response not Successful")
            }
            binding.progressBar.isVisible = false
        }


        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                if (query!!.isNotEmpty()) {
                    val intent =
                        Intent(binding.searchBar.context, SearchActivity::class.java).apply {
                            putExtra("keyword", query)
                        }
                    startActivity(intent)

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun filldata(post: World) {
        binding.cardView.isVisible = true
        binding.textViewWorld.text = "World"
        binding.textViewWorldTotalCase.text = "Total Case:\n${post.cases.toString()}"
        binding.textViewWorldTotalDeath.text = "Total Death:\n${post.deaths.toString()}"
        binding.textViewWorldTotalRecovery.text = "Total Recovery:\n${post.recovered.toString()}"
        fillPieChart(post)
    }

    private fun setRecycleViewAdapter() = binding.recycleViewHome.apply {
        countryAdapter = CountryAdapter()
        adapter = countryAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }
    private fun fillPieChart(post: World) {
        //setup Pie Entries
        val pieEntry = arrayListOf<PieEntry>()
        pieEntry.add(PieEntry(post.cases.toFloat()))
        pieEntry.add(PieEntry(post.deaths.toFloat()))
        pieEntry.add(PieEntry(post.recovered.toFloat()))

        binding.pieChartWorld.animateXY(1000, 1000) // This 1000 is time that how much time pieChart Created

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

        binding.pieChartWorld.description.isEnabled = false
        binding.pieChartWorld.data = pieData
    }

}