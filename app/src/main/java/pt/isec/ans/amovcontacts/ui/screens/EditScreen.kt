package pt.isec.ans.amovcontacts.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import pt.isec.ans.amovcontacts.utils.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    name: MutableState<String>,
    email: MutableState<String>,
    phone: MutableState<String>,
    birthday: DatePickerState,
    picture: MutableState<String?>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagePath : String by lazy { FileUtils.getTempFilename(context) }
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                picture.value = FileUtils.createFileFromUri(context,it)
            }
        }
    )
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult  ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            picture.value = FileUtils.copyFile(context,imagePath)
        }
    }
    val takePicture2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success  ->
        if (success) {
            picture.value = FileUtils.copyFile(context,imagePath)
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = name.value,
            isError = name.value.isEmpty(),
            label = {
                Text("Name:")
            },
            onValueChange = { newText ->
                name.value = newText
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email.value,
            isError = email.value.isEmpty(),
            label = {
                Text("Email:")
            },
            onValueChange = { newText ->
                email.value = newText
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = phone.value,
            isError = phone.value.isEmpty(),
            label = {
                Text("Phone:")
            },
            onValueChange = { newText ->
                phone.value = newText
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Birthday:")
        DatePicker(
            state = birthday,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    pickImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                //modifier = Modifier.align(Alignment.CenterHorizontally),
                modifier = Modifier.weight(1f),
            ) {
                Text("Select picture")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
//                    takePicture.launch(
//                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//                            putExtra(
//                                MediaStore.EXTRA_OUTPUT,
//                                FileProvider.getUriForFile(
//                                    context,
//                                    "pt.isec.ans.amovcontacts.android.fileprovider",
//                                    File(imagePath)
//                                )
//                            )
//                        }
//                    )
                    takePicture2.launch(
                        FileProvider.getUriForFile(
                            context,
                            "pt.isec.ans.amovcontacts.android.fileprovider",
                            File(imagePath)
                        )
                    )
                },
                //modifier = Modifier.align(Alignment.CenterHorizontally),
                modifier = Modifier.weight(1f),
            ) {
                Text("Take picture")
            }
        }
        Spacer(Modifier.height(16.dp))
        picture.value?.let { path ->
            AsyncImage(
                model = path,
                contentScale = ContentScale.Crop,
                contentDescription = "Contact image",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}