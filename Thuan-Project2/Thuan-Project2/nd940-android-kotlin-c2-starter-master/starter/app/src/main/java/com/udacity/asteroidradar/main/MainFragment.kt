package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[(MainViewModel::class.java)]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val asteroidAdapter = MainAsteroidAdapter(MainAsteroidAdapter.AsteroidListener {
            viewModel.onAsteroidSelected(it)
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> {
            it.apply {
                asteroidAdapter.submitList(this)
            }
        })

        viewModel.navigateToDetailAsteroid.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onAsteroidNavigated()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeFilter(
            when (item.itemId) {
                R.id.today_asteroids_menu -> MainViewModel.AsteroidApiFilter.TODAY
                R.id.week_asteroids_menu -> MainViewModel.AsteroidApiFilter.WEEK
                else -> MainViewModel.AsteroidApiFilter.SAVED
            }
        )
        return true
    }
}
