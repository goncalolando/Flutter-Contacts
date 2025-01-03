package pt.isec.ans.amovcontacts

import android.app.Application
import com.google.android.gms.location.LocationServices
import pt.isec.ans.amovcontacts.model.Contact
import pt.isec.ans.amovcontacts.model.ContactsList
import pt.isec.ans.amovcontacts.utils.location.FusedLocationHandler
import pt.isec.ans.amovcontacts.utils.location.LocationHandler
import java.util.Date

class ContactsApp : Application() {
    companion object {
        private const val DATAFILE = "contacts.bin"
    }
    private val _contactsList by lazy {
        try {
            openFileInput(DATAFILE)?.let { srcFile -> ContactsList.load(srcFile) }
        } catch (_: Exception) {
            null
        } ?: ContactsList()
    }
    val contactsList get() = _contactsList

    /*val locationHandler : LocationHandler by lazy {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        LocationManagerHandler(locationManager)
    }*/

    val locationHandler : LocationHandler by lazy {
        val locationProvider = LocationServices.getFusedLocationProviderClient(this)
        FusedLocationHandler(locationProvider)
    }

    override fun onCreate() {
        super.onCreate()
        if (_contactsList.getContacts().isEmpty()) {
            _contactsList.addContact(Contact("Joaquim Antunes", "ja@world.com", "+351239000000"))
            _contactsList.addContact(Contact("Daniela Amado", "da@world.com", "+351239000001"))
            _contactsList.addContact(Contact("Ermelinda Freitas", "ef@world.com", "+351239000002",Date()))
            _contactsList.addContact(Contact("António Silva", "as@world.com", "+351239000003"))
        }
    }

    fun saveData() {
        try {
            contactsList.save(openFileOutput(DATAFILE, MODE_PRIVATE))
        } catch (_: Exception) {}
    }
}