package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class AsteroidApiFilter {
        TODAY,
        WEEK,
        SAVED
    }

    private var filter = MutableLiveData(AsteroidApiFilter.SAVED)
    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid>
        get() = _navigateToDetailAsteroid

    val asteroids = Transformations.switchMap(filter) {
        when (it!!) {
            AsteroidApiFilter.WEEK -> asteroidRepository.weekAsteroids
            AsteroidApiFilter.TODAY -> asteroidRepository.todayAsteroids
            else -> asteroidRepository.savedAsteroids
        }
    }

    fun onAsteroidSelected(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetailAsteroid.value = null
    }

    fun onChangeFilter(filter: AsteroidApiFilter) {
        this.filter.postValue(filter)
    }

    private suspend fun refreshPictureOfDay() {
        _pictureOfDay.value = getPictureOfDay()
    }
}