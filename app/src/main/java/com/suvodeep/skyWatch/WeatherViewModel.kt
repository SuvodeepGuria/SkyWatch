package com.suvodeep.skyWatch

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suvodeep.skyWatch.ApiCall.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val context: Context) : ViewModel() {
    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: MutableStateFlow<WeatherResponse?> = _weatherResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val _weatherApi = MutableStateFlow(WeatherApi.create())

    fun fetchWeather(city: String, apiKey: String) {
        if (!isNetworkAvailable()) {
            _errorMessage.value = "Please turn on the internet connection"
            return
        }

        viewModelScope.launch {
            try {
                val response = _weatherApi.value.getWeather(city, apiKey)

                if (response.cod == 200) {
                    _weatherResponse.value = response
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Please search a valid city"
                }
            } catch (e: Exception) {
                _weatherResponse.value = null
                _errorMessage.value = "Please search a valid city"
                Log.e("WeatherViewModel", "Error fetching weather data", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val network = it.activeNetwork ?: return false
                val activeNetwork = it.getNetworkCapabilities(network) ?: return false
                activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val networkInfo = it.activeNetworkInfo
                networkInfo != null && networkInfo.isConnectedOrConnecting
            }
        } ?: run {
            Log.e("WeatherViewModel", "ConnectivityManager is null")
            return false
        }
    }

}
