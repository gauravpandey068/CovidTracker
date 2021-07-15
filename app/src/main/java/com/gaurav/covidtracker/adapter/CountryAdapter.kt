package com.gaurav.covidtracker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaurav.covidtracker.databinding.RecyclerViewItemBinding
import com.gaurav.covidtracker.model.Country

class CountryAdapter : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    inner class CountryViewHolder(val binding: RecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.countryInfo.id == newItem.countryInfo.id
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)
    var countries: List<Country>
        get() = differ.currentList
        set(values) = differ.submitList(values)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
       return CountryViewHolder(RecyclerViewItemBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       ))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.binding.apply {
            val country = countries[position]
            textViewCountryName.text = country.country
            Glide.with(imageViewCountryFlag.context).load(country.countryInfo.flag).into(imageViewCountryFlag)
            textViewTotalCase.text = "Total Case: \n${country.cases.toString()}"
            textViewTotalDeath.text = "Total Death: \n${country.deaths.toString()}"
            textViewTotalRecovery.text = "Total Recovery: \n${country.recovered.toString()}"

        }


    }

    override fun getItemCount()= countries.size

}