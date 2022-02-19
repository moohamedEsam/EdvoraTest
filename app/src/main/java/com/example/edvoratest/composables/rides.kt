package com.example.edvoratest.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.edvoratest.R
import com.example.edvoratest.dataModels.Ride
import com.example.edvoratest.ui.theme.EdvoraTestTheme
import com.example.edvoratest.utils.Filters
import com.example.edvoratest.viewModels.MainViewModel
import org.koin.androidx.compose.getViewModel
import java.text.DateFormat
import java.util.*

@Composable
fun RidesScreen() {
    val viewModel = getViewModel<MainViewModel>()
    val rides by viewModel.rides
    val filteredRider by viewModel.filteredRides
    val filter by viewModel.filters
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Toast.makeText(
            LocalContext.current,
            "current user station code: ${viewModel.user.station_code}",
            Toast.LENGTH_LONG
        ).show()
        TopRow()
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn {
            items(
                if (filter !is Filters.Nearest)
                    filteredRider
                else
                    rides
            ) {
                RideComposable(ride = it)
            }
        }
    }
}

@Composable
fun RideComposable(ride: Ride) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.evdora_map),
                contentDescription = null,
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(8.dp))
            LocationRow(ride)
            Spacer(modifier = Modifier.height(8.dp))
            RideDetails(ride)
        }
    }
}

@Composable
private fun RideDetails(ride: Ride) {
    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
    Text(text = "Ride id: ${ride.id}")
    Text(
        text = "Origin_station: ${ride.origin_path[0]}",
        modifier = Modifier.padding(top = 4.dp)
    )
    Text(
        text = "station_path: [${ride.origin_path.joinToString(", ")}]",
        modifier = Modifier.padding(top = 4.dp)
    )
    Text(
        text = "Date: ${dateFormat.format(Date(ride.date))}",
        modifier = Modifier.padding(top = 4.dp)
    )
    Text(
        text = "Distance: ${ride.distance}",
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun LocationRow(ride: Ride) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(text = ride.city, modifier = Modifier.padding(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(text = ride.state, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun TopRow() {
    val viewModel: MainViewModel = getViewModel()
    val filters by viewModel.filters
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Rides")
        Spacer(modifier = Modifier.width(4.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(.7f)) {
            items(
                listOf(
                    Filters.Nearest(),
                    Filters.Upcoming(viewModel.upcomingCount),
                    Filters.Past(viewModel.pastCount)
                )
            ) {
                Text(
                    text = it.value,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable {
                            viewModel.setFilters(it)
                        },
                    textDecoration = if (it == filters)
                        TextDecoration.Underline
                    else
                        TextDecoration.None,
                    color = if (it == filters)
                        Color.White
                    else
                        Color.Unspecified
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        FilterRow()
    }
}

@Composable
private fun FilterRow() {
    var expanded by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.clickable {
            expanded = true
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (expanded)
            ShowFilterBox {
                expanded = false
            }
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_foreground),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = "Filters")
    }
}

@Composable
fun ShowFilterBox(onDismissRequest: () -> Unit) {
    var cityExpanded by remember {
        mutableStateOf(false)
    }
    var stateExpanded by remember {
        mutableStateOf(false)
    }
    val viewModel: MainViewModel = getViewModel()
    DropdownMenu(
        expanded = true,
        onDismissRequest = {
            onDismissRequest()
        },
        modifier = Modifier.fillMaxWidth(.4f)
    ) {
        when {
            cityExpanded -> ShowFilter(viewModel.getCities()) {
                viewModel.filterByCity(it)
                cityExpanded = false
            }
            stateExpanded -> ShowFilter(viewModel.getStates()) {
                viewModel.filterByState(it)
                stateExpanded = false
            }
            else -> MainFilterBox(
                {
                    stateExpanded = true
                }
            ) {
                cityExpanded = true
            }
        }

    }
}

@Composable
private fun MainFilterBox(stateOnClick: () -> Unit, cityOnClick: () -> Unit) {
    DropdownMenuItem(
        onClick = {
            stateOnClick()
        }
    ) {
        Text(text = "state")
    }
    DropdownMenuItem(
        onClick = {
            cityOnClick()
        }
    ) {
        Text(text = "city")
    }
}

@Composable
fun ShowFilter(values: Set<String>, onClick: (String) -> Unit) {
    values.forEach {
        DropdownMenuItem(onClick = {
            onClick(it)
        }) {
            Text(it)
        }
    }
}


@Preview
@Composable
fun ListPrev() {
    EdvoraTestTheme {
        RideComposable(
            ride = Ride(
                "001",
                intArrayOf(20, 30, 40),
                Date().time,
                "",
                "cairo",
                "banha"
            )
        )
    }
}