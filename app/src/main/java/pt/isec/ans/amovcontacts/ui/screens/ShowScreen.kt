package pt.isec.ans.amovcontacts.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import pt.isec.ans.amovcontacts.R
import pt.isec.ans.amovcontacts.model.Contact
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ShowScreen(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
        ShowScreenLandscape(contact,modifier)
    else
        ShowScreenPortrait(contact,modifier)
}

@Composable
private fun ShowScreenPortrait(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // https://static-00.iconduck.com/assets.00/avatar-icon-512x512-gu21ei4u.png
        // https://developer.android.com/static/images/home/android-15.svg
        AsyncImage(
            model = contact.picture?: R.drawable.avatar_icon,
            contentScale = ContentScale.Crop,
            contentDescription = "Contact image",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Name:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
        Text(contact.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 16.dp),)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Email:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
        Text(contact.email, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 16.dp),)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Phone:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
        Text(contact.phone, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 16.dp),)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Birthday:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
        contact.birthday?.let { birthday ->
            Text(
                text = dateFormatter.format(birthday),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp),
            )
        } ?: Text(
            text = "----.--.--",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Meeting Points:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
//        LazyColumn {
//            items(items = contact.getMeetingPoints()) { meeting ->
//                Row (horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
//                    Text(meeting.latitude.toString())
//                    Text(meeting.longitude.toString())
//                    Text(dateFormatter.format(meeting.date))
//                }
//
//            }
//        }

//        val context = LocalContext.current
//        var geoPoint by remember {
//            mutableStateOf(GeoPoint(40.1925, -8.4128))
//        }
//
//        val mapView = MapView(context).apply {
//            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
//            setMultiTouchControls(true)
//            controller.setCenter(geoPoint)
//            controller.setZoom(17.0)
//        }
//
//        Box (
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//        ){
//            AndroidView(
//                modifier = Modifier
//                    .fillMaxSize(),
//                factory = { mapView },
//                update = { view ->
//                    view.controller.setCenter(geoPoint)
//                }
//            )
//        }

        // define camera state
        val cameraState = rememberCameraState {
            geoPoint = GeoPoint(40.1925, -8.4128)
            zoom = 12.0 // optional, default is 5.0
        }

        // define marker state
        val deisISEC = rememberMarkerState(
            geoPoint = GeoPoint(40.1925, -8.4128)
        )

        // add node
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .padding(16.dp),
        ) {
            OpenStreetMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState
            ){
//                com.utsman.osmandcompose.Marker(
//                    state = deisISEC // add marker state
//                )
                for(meeting in contact.getMeetingPoints()) {
                    com.utsman.osmandcompose.Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(meeting.latitude, meeting.longitude)
                        ),
                        title = dateFormatter.format(meeting.date)
                    ) {
                        Text(it.title)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowScreenLandscape(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // https://static-00.iconduck.com/assets.00/avatar-icon-512x512-gu21ei4u.png
        // https://developer.android.com/static/images/home/android-15.svg
        AsyncImage(
            model = contact.picture ?: R.drawable.avatar_icon,
            contentScale = ContentScale.Crop,
            contentDescription = "Contact image",
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text("Name:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Text(
                contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Email:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Text(
                contact.email,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Phone:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Text(
                contact.phone,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Birthday:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
            contact.birthday?.let { birthday ->
                Text(
                    text = dateFormatter.format(birthday),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp),
                )
            } ?: Text(
                text = "----.--.--",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text("Meeting Points:", fontStyle = FontStyle.Italic, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
//            LazyColumn {
//                items(items = contact.getMeetingPoints()) { meeting ->
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(meeting.latitude.toString())
//                        Text(meeting.longitude.toString())
//                        Text(dateFormatter.format(meeting.date))
//                    }
//
//                }
//            }
            //val deisisec = LatLng(40.1925, -8.4128)
//            val positions = arrayOf(
//                LatLng(40.1925, -8.4128),
//                LatLng(39.1925, -8.4128),
//                LatLng(41.1925, -8.4128),
//                LatLng(40.1925, -7.4128),
//                LatLng(40.1925, -9.4128),
//            )

            val cameraPositionState = rememberCameraPositionState {
                CameraPosition.fromLatLngZoom(LatLng(0.0,0.0), 13f)
            }

            if (contact.getMeetingPoints().isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                contact.getMeetingPoints().forEach { meeting ->
                    boundsBuilder.include(LatLng(meeting.latitude, meeting.longitude))
                }
                val bounds = boundsBuilder.build()

                LaunchedEffect(bounds) {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(bounds, 64),
                        durationMs = 2000
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.SATELLITE,
                        isMyLocationEnabled = true,

                    ),
                ) {
                    contact.getMeetingPoints().forEach { meeting ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    meeting.latitude,
                                    meeting.longitude
                                )
                            ),
                            title = dateFormatter.format(meeting.date)
                        )
                    }
                    //                Marker(
                    //                    state = MarkerState(position = deisisec),
                    //                    title = "DEIS-ISEC",
                    //                    snippet = "Dep. Informatics Engineering"
                    //                )
                }
            }
        }
    }
}

