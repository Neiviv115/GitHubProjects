package com.example.supersoiree


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.supersoiree.databinding.RecyclerviewItemBinding
import com.example.supersoiree.model.PubForfav


class FavouritesAdapter(private val pubs: List<PubForfav>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "RecycleArtistAdapter"
    }

    init {


        Log.d(TAG, "RecyclerAdapter list : ${pubs.size}")
    }
    //private lateinit var navController : NavController

    inner class ViewHolder(val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = pubs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val binding = RecyclerviewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder position=${position}")
        val pub: PubForfav = pubs[position]



        holder.binding.textViewPub.text = pub.name
        //set the imageView as a clickable item, allows navigation to next fragment
        holder.binding.delete.setOnClickListener{
            MainActivity().removeFromFav(pub.name)
        }


    }


}