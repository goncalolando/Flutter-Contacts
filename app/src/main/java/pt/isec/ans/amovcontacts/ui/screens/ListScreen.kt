package pt.isec.ans.amovcontacts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isec.ans.amovcontacts.model.Contact
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ListScreen(
    contacts : List<Contact>,
    showExpanded : Boolean = false,
    onSelectContact : (Contact) -> Unit = {},
    modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            contacts,
            key = { contact -> contact.hashCode() }) { contact ->
            ContactCard(
                contact = contact,
                showExpanded = showExpanded,
                onSelectContact = onSelectContact,
            )

        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    showExpanded: Boolean,
    onSelectContact: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(255,224,192)
        ),
        onClick = { onSelectContact(contact) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = contact.name, fontSize = 20.sp)
            if (showExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = contact.email, fontSize = 14.sp)
                    Text(text = contact.phone, fontSize = 14.sp)
                }
                contact.birthday?.let { birthday ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = formatter.format(birthday),
                        fontSize = 14.sp
                    )
                } ?: Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "----.--.--",
                    fontSize = 14.sp
                )
            }
        }
    }

}
