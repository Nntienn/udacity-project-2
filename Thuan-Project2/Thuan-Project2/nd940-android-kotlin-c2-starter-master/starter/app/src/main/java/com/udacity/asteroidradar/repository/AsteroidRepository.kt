package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.util.Constants
import com.udacity.asteroidradar.util.Constants.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class AsteroidRepository(private val database: AsteroidDatabase) {

    private val startDate = LocalDateTime.now()

    private val endDate = LocalDateTime.now().plusDays(Constants.DEFAULT_END_DATE_DAYS.toLong())

    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroids(
                startDate.format(DateTimeFormatter.ISO_DATE),
                startDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModel()
        }

    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroids(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModel()
        }

    val savedAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getSavedAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val respondBody = AsteroidApi.retrofitService.getAsteroids(API_KEY)
            asteroidList = parseAsteroidsJsonResult(JSONObject(respondBody))
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }

}