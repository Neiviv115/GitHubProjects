package com.example.supersoiree

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.supersoiree.Authentication.Companion.TAG
import com.example.supersoiree.databinding.FragmentFavouritesBinding
import com.example.supersoiree.model.PubForfav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        binding.recyclerview.layoutManager=LinearLayoutManager(requireContext())
        val mainHandler = Handler(Looper.getMainLooper())

        GlobalScope.launch {
            val favList = withContext(Dispatchers.Main) {
                MainActivity().getFav()
            }
                Log.d(TAG,"issue de getfav${favList.size}")
            withContext(Dispatchers.Main) {
                binding.recyclerview.adapter = FavouritesAdapter(favList)
            }
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}