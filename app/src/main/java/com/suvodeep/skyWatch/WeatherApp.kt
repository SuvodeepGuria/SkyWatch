package com.suvodeep.skyWatch

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun WeatherApp() {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(context))
    val weatherResponse by viewModel.weatherResponse.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val city by remember { mutableStateOf("") }
    val apiKey = BuildConfig.API_KEY                       //API Key is hide
    val inputState= remember { mutableStateOf("") }
    val validState= remember(inputState.value) { inputState.value.trim().isNotEmpty()}
    val keyboardController= LocalSoftwareKeyboardController.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = "")
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "SkyWatch",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Cursive,
                color = Color(255, 3, 3, 208)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchField(
                    modifier = Modifier,
                    enabled = true,
                    labelId = "Search any City",
                    valueState = inputState,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions {
                        if (validState) {
                            viewModel.fetchWeather(inputState.value.trim(), apiKey)
                            keyboardController?.hide()
                        } else {
                            Toast.makeText(context, "Please enter a valid city name.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    singleLined = true
                )
                IconButton(
                    onClick = {
                        if (validState) {
                            viewModel.fetchWeather(inputState.value.trim(), apiKey)
                            keyboardController?.hide()
                        } else {
                            Toast.makeText(context, "Please enter a valid city name.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            weatherResponse?.let { it ->
                val celsiusTemp = it.main.temp
                val fahrenheitTemp = (celsiusTemp * 9 / 5) + 32
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeatherCard(label = "City", value = it.name, icon = Icons.Default.Place)
                            WeatherCard(
                                label = "Temperature",
                                value = "$celsiusTemp°C  ($fahrenheitTemp°F)",
                                icon = Icons.Default.Thermostat
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeatherCard(
                                label = "Humidity",
                                value = "${it.main.humidity}%",
                                icon = Icons.Default.WaterDrop
                            )
                            WeatherCard(
                                label = "Condition",
                                value = it.weather[0].description.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                },
                                icon = Icons.Default.CloudQueue
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                WeatherCard(
                                    label = "Wind Speed",
                                    value = "${it.wind.speed} m/s",
                                    icon = Icons.Default.Air
                                )
                            }
                            item {
                                WeatherCard(
                                    label = "Cloudiness",
                                    value = "${it.clouds.all}%",
                                    icon = Icons.Default.Cloud
                                )
                            }
                            item {
                                WeatherCard(
                                    label = "Pressure",
                                    value = "${it.main.pressure} hPa",
                                    icon = Icons.Default.Speed
                                )
                            }
                            item {
                                WeatherCard(
                                    label = "Visibility",
                                    value = "${it.visibility / 1000} km",
                                    icon = Icons.Default.Visibility
                                )
                            }
                            item {
                                WeatherCard(
                                    label = "Sunrise",
                                    value = convertTimestampToTime(it.sys.sunrise),
                                    icon = Icons.Default.WbSunny
                                )
                            }
                            item {
                                WeatherCard(
                                    label = "Sunset",
                                    value = convertTimestampToTime(it.sys.sunset),
                                    icon = Icons.Default.NightlightRound
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(140.dp),
        colors = CardDefaults.cardColors(Color(245, 182, 193, 80)) // Light pink color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

fun convertTimestampToTime(timestamp: Int): String {
    val sdf = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = java.util.Date(timestamp * 1000L)
    return sdf.format(date)
}

@Composable
fun AppWithSplashScreen() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        showSplash = false
    }

    if (showSplash) {
        StartingPage()
    } else {
        WeatherApp()
    }
}


@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Search,
    singleLined: Boolean = true,
    labelId: String,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it }, // Update the MutableState
        label = { Text(text = labelId) }, // Use @Composable for label
        singleLine = singleLined,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions, // Correct parameter name
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(CornerSize(40.dp))
    )
}
