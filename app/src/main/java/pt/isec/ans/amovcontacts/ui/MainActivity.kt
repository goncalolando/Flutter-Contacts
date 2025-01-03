package pt.isec.ans.amovcontacts.ui

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import pt.isec.ans.amovcontacts.ContactsApp
import pt.isec.ans.amovcontacts.ui.screens.MainScreen
import pt.isec.ans.amovcontacts.ui.theme.AMovContactsTheme
import pt.isec.ans.amovcontacts.ui.viewmodels.ContactsViewModel
import pt.isec.ans.amovcontacts.ui.viewmodels.ContactsViewModelFactory

class MainActivity : ComponentActivity() {
    private val app by lazy { application as ContactsApp }
    private val viewModel: ContactsViewModel
            by viewModels { ContactsViewModelFactory(app.contactsList,app.locationHandler) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osm", MODE_PRIVATE))
        enableEdgeToEdge()
        setContent {
            AMovContactsTheme {
                MainScreen(viewModel = viewModel)
            }
        }
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askSinglePermission.launch(android.Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                askSinglePermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                askMultiplePermissions.launch(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }

        verifyAndAskLocationPermissions()

    }

    private val askSinglePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        /*TODO*/
        verifyAndAskLocationPermissions()
    }

    private val askMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        /*TODO*/
        verifyAndAskLocationPermissions()
    }

    private fun verifyAndAskLocationPermissions() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.hasLocationPermission = false
            askLocationPermissions.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            viewModel.hasLocationPermission = true
            viewModel.startLocationUpdates()
            verifyBackgroundPermission()
        }
    }

    private val askLocationPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        viewModel.hasLocationPermission =
                    map[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                    map[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (viewModel.hasLocationPermission) {
            viewModel.startLocationUpdates()
            verifyBackgroundPermission()
        }
    }

    private fun verifyBackgroundPermission() {
        viewModel.hasBackgroundLocationPermission = (
                    checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED  )
        if (!viewModel.hasLocationPermission || viewModel.hasBackgroundLocationPermission)
            return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                val dlg = AlertDialog.Builder(this)
                    .setTitle("Background Location")
                    .setMessage(
                        "This application needs your permission to use location while in the background.\n" +
                                "Please choose the correct option in the following screen" +
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                                    " (\"${packageManager.backgroundPermissionOptionLabel}\")."
                                else
                                    "."
                    )
                    .setPositiveButton("Ok") { _, _ ->
                        backgroundPermissionAuthorization.launch(
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                    .create()
                dlg.show()
            }
        }
    }

    private val backgroundPermissionAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        viewModel.hasBackgroundLocationPermission = result
        Toast.makeText(this,"Background location enabled: $result", Toast.LENGTH_LONG).show()
    }


//    override fun onResume() {
//        super.onResume()
//        viewModel.startLocationUpdates()
//    }

//    override fun onPause() {
//        super.onPause()
//        app.saveData()
//    }
}

