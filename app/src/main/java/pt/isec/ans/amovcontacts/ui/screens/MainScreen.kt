package pt.isec.ans.amovcontacts.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.isec.ans.amovcontacts.ContactsApp
import pt.isec.ans.amovcontacts.ui.viewmodels.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ContactsViewModel,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ContactsApp

    var showExpanded by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }


    val currentScreen by navController.currentBackStackEntryAsState()
    navController.addOnDestinationChangedListener { navCtrl, destination, arguments -> ; }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0,224,255),
                    titleContentColor = Color(0,0,128),
                ),
                navigationIcon = {
                    if (currentScreen?.destination?.route != "list") {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    when (currentScreen?.destination?.route) {
                        "list"->{
                            IconButton(onClick = { showExpanded = !showExpanded }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Expand/Collapse"
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.createContact()
                                    navController.navigate("edit")
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add Contact"
                                )
                            }
                        }
                        "show"->{
                            IconButton(onClick = {
                                showConfirmationDialog = true
                            }) {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = "Store Location"
                                )
                            }
                            IconButton(onClick = { navController.navigate("edit") }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Edit Contact"
                                )
                            }
                        }
                        "edit"->{
                            IconButton(
                                onClick = {
                                    if (viewModel.saveContact()) {
                                        app.saveData()
                                        navController.popBackStack("list", inclusive = false)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = "Save Contact"
                                )
                            }
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("list") {
                Log.d("MainScreen", "List screen: ${viewModel.contactsList.getContacts().size}")
                ListScreen(
                    contacts = viewModel.contactsList.getContacts(),
                    showExpanded = showExpanded,
                    onSelectContact = { contact ->
                        viewModel.selectContact(contact)
                        navController.navigate("show")
                    }
                )
            }
            composable("show") {
                viewModel.currentContact?.let { contact ->
                    ShowScreen(contact = contact)
                } ?: Text("No contact selected")
            }
            composable("edit") {
                viewModel.birthdayDPState.displayMode = DisplayMode.Input
                EditScreen(
                    name = viewModel.name,
                    email = viewModel.email,
                    phone = viewModel.phone,
                    birthday = viewModel.birthdayDPState,
                    picture = viewModel.picture
                )
            }
        }
        if (showConfirmationDialog) {
            ConfirmationDialog(
                actionDescription = "Store meeting location" ,
                onConfirm = {
                    showConfirmationDialog = false
                    viewModel.storeCurrentLocation()
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    actionDescription : String,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Confirmation dialog")
        },
        title = {
            Text("Confirm operation")
        },
        text = {
            Text("$actionDescription?")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}