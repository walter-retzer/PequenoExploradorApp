package com.example.pequenoexploradorapp.presentation.screen

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.SubcomposeAsyncImage
import com.example.pequenoexploradorapp.R
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import com.example.pequenoexploradorapp.presentation.components.MenuToolbar
import com.example.pequenoexploradorapp.presentation.components.snackBarOnlyMessage
import com.example.pequenoexploradorapp.presentation.theme.Pink40
import com.example.pequenoexploradorapp.presentation.theme.mainColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareFavouriteImageScreen(
    image: String
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val toolbarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    var snackBarIsActivated by remember { mutableStateOf(false) }
    var snackBarMessage by remember { mutableStateOf("") }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MenuToolbar(
                title = "Compartilhar",
                onNavigationToMenu = { },
                onNavigationToProfile = { },
                onNavigateToNotifications = { },
                toolbarBehavior = toolbarBehavior,
                isActivatedBadge = false
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (snackBarIsActivated) {
            LaunchedEffect(Unit) {
                snackBarOnlyMessage(
                    snackBarHostState = snackBarHostState,
                    coroutineScope = scope,
                    message = snackBarMessage,
                    duration = SnackbarDuration.Short
                )
                snackBarIsActivated = false
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .paint(
                    painterResource(id = R.drawable.simple_background),
                    contentScale = ContentScale.FillBounds
                )
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = image,
                    loading = {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                color = Pink40
                            )
                        }
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(mainColor.copy(alpha = 0.85f), shape = CircleShape)
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        onClick = {
                            scope.launch {
                                snackBarMessage = saveImageToGallery(
                                    context,
                                    image,
                                    "image_fav_${System.currentTimeMillis()}"
                                )
                                snackBarIsActivated = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Favorite Image",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(mainColor.copy(alpha = 0.85f), shape = CircleShape)
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        onClick = {
                            scope.launch {
                                shareImageToWhatsApp(
                                    context,
                                    image
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Favorite Image",
                            tint = Color.White
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Salvar",
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Text(
                        text = "Compartilhar",
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}


suspend fun shareImageToWhatsApp(context: Context, imageUrl: String) {
    try {
        val bitmap = withContext(Dispatchers.IO) {
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openStream())
        }
        val imageFile = saveBitmapToCache(context, bitmap)
        if (imageFile != null) shareImageViaWhatsApp(context, imageFile)
        else throw IOException(ConstantsApp.SAVE_IMAGE_ERROR_DECODE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File? {
    return try {
        val cacheDir = File(context.cacheDir, "images")
        cacheDir.mkdirs()
        val file = File(cacheDir, "shared_image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun shareImageViaWhatsApp(context: Context, imageFile: File) {
    try {
        val imageUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            setDataAndType(imageUri, "image/jpeg")
            putExtra(Intent.EXTRA_TEXT, ConstantsApp.SHARE_IMAGE)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, "Imagem ")
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


suspend fun saveImageToGallery(context: Context, imageUrl: String, fileName: String): String {
    return try {
        val bitmap = withContext(Dispatchers.IO) {
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openStream())
        }
        val savedUri = saveBitmapToGallery(context, bitmap, fileName)
        if (savedUri != null) ConstantsApp.SAVE_IMAGE_OK
        else ConstantsApp.SAVE_IMAGE_ERROR
    } catch (e: Exception) {
        e.printStackTrace()
        ConstantsApp.SAVE_IMAGE_ERROR_DECODE
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): String? {
    val contentResolver = context.contentResolver
    val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    return try {
        val uri = contentResolver.insert(imageCollection, contentValues)
        uri?.let {
            val outputStream: OutputStream? = contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(it, contentValues, null, null)
            uri.toString()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
