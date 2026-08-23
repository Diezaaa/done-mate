package com.example.donemate.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.donemate.R

@Composable
fun TaskImage(stringImageUri: String){
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(13.5.dp)), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = stringImageUri.toUri(),
            contentDescription = "image of a task",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .blur(18.dp)
        )
        AsyncImage(
            model = stringImageUri.toUri(),
            contentDescription = "image of a task",
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .height(190.dp)
        )
    }
}