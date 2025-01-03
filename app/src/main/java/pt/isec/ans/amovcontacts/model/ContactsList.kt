package pt.isec.ans.amovcontacts.model

import android.util.Log
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.io.Serializable
import java.text.Collator
import java.util.Locale

class ContactsList : Serializable {
    companion object {
        fun load(source : InputStream) : ContactsList?  {
            try {
                ObjectInputStream(source).use { ois ->
                    return ois.readObject() as ContactsList
                }
            } catch (_: Exception){
                return null
            }
        }
    }
    private val contacts = mutableListOf<Contact>()

    fun addContact(contact: Contact) {
        contacts.add(contact)
    }

    fun getContacts(): List<Contact> {
        val collator = Collator.getInstance(Locale.getDefault()).apply {
            strength = Collator.PRIMARY
        }

        return contacts.sortedWith { contact1, contact2 ->
            collator.compare(contact1.name, contact2.name)
        }
    }

    fun save(destination: OutputStream) {
        try {
            ObjectOutputStream(destination).use { oos ->
                oos.writeObject(this)
            }
        } catch (_: Exception) { }
    }

    fun clearAll() {
        Log.d("ContactsList", "Clearing all contacts")
        contacts.clear()
    }

}