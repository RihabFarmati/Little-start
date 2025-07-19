package com.app.littlestar.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import com.app.littlestar.R

@Composable
fun CandleScreen(isBlowing: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Candle body (static)
        Image(
            painter = painterResource(id = R.drawable.red_candle_body),
            contentDescription = "Candle Body",
        )

        if (!isBlowing) {
            // Display animated flame GIF
            val context = LocalContext.current
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    add(GifDecoder.Factory())
                }
                .build()

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.flame)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Flame GIF",
                modifier = Modifier
                    .size(50.dp)
                    .offset(y = (-50).dp)
            )
        }
    }
}

