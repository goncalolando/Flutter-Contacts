package pt.isec.ans.amovcontacts.ui.viewmodels

import android.location.Location
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.ans.amovcontacts.model.Contact
import pt.isec.ans.amovcontacts.model.ContactsList
import pt.isec.ans.amovcontacts.utils.location.LocationHandler
import java.util.Date
import java.util.Locale

class ContactsViewModelFactory(
    private val contactsList: ContactsList,
    private val locationHandler: LocationHandler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(contactsList,locationHandler) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class ContactsViewModel(
    val contactsList: ContactsList,
    private val locationHandler: LocationHandler
) : ViewModel() {
    var currentContact: Contact? = null

    val name = mutableStateOf("")
    val phone = mutableStateOf("")
    val email = mutableStateOf("")
    val birthdayDPState = DatePickerState(Locale.getDefault(), Date().time)
    val picture = mutableStateOf<String?>(null)

    fun createContact() {
        currentContact = null
        name.value = ""
        phone.value = ""
        email.value = ""
        birthdayDPState.selectedDateMillis = 0
        picture.value = null
    }

    fun selectContact(contact: Contact) {
        currentContact = contact
        name.value = contact.name
        phone.value = contact.phone
        email.value = contact.email
        birthdayDPState.selectedDateMillis = contact.birthday?.time ?: 0
        picture.value = contact.picture
    }

    fun saveContact(): Boolean {
        if (name.value.isEmpty() || phone.value.isEmpty() || email.value.isEmpty()) {
            return false
        }
        if (currentContact == null) {
            contactsList.addContact(
                Contact(
                    name = name.value,
                    email = email.value,
                    phone = phone.value,
                    birthday = birthdayDPState.selectedDateMillis?.let { Date(it) },
                    picture = picture.value
                )
            )
        } else {
            currentContact?.let { contact ->
                contact.name = name.value
                contact.email = email.value
                contact.phone = phone.value
                contact.birthday = birthdayDPState.selectedDateMillis?.let { Date(it) }
                contact.picture = picture.value
            }
        }
        return true
    }

    // ---- Locaation ----
    var hasLocationPermission: Boolean = false
    var hasBackgroundLocationPermission: Boolean = false

    private val currentLocation = mutableStateOf(Location(null))

    init {
        locationHandler.onLocation = { location ->
            currentLocation.value = location
        }
    }

    fun startLocationUpdates() {
        if (hasLocationPermission)
            locationHandler.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationHandler.stopLocationUpdates()
    }

    fun storeCurrentLocation() {
        if (!hasLocationPermission || currentContact == null)
            return
        currentContact!!.addMeetingPoint(
            currentLocation.value.latitude,
            currentLocation.value.longitude,
            Date(currentLocation.value.time)
        )
    }

    override fun onCleared() {
        super.onCleared()
        locationHandler.stopLocationUpdates()
    }
}