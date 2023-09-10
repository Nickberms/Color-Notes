package com.example.colornotes.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colornotes.Utils
import com.example.colornotes.ui.theme.ColorNotesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel?,
    noteId: String,
    onNavigate: () -> Unit,
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()
    detailUiState.note.isNotBlank() &&
            detailUiState.title.isNotBlank()
    val selectedColor by animateColorAsState(
        targetValue = Utils.colors[detailUiState.colorIndex]
    )
    val isNoteIdNotBlank = noteId.isNotBlank()
    val icon = Icons.Default.Check
    val scope = rememberCoroutineScope()
    val buttonBackgroundColor =
        if (detailUiState.title.isNotBlank() || detailUiState.note.isNotBlank()) {
            Color(0xFF72d572)
        } else {
            Color(0xFFf36c60)
        }
    LaunchedEffect(key1 = Unit) {
        if (isNoteIdNotBlank) {
            detailViewModel?.getNote(noteId)
        } else {
            detailViewModel?.resetState()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isNoteIdNotBlank) {
                        if (detailUiState.title.isNotBlank() || detailUiState.note.isNotBlank()) {
                            detailViewModel?.updateNote(noteId)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please provide some content first")
                            }
                        }
                    } else {
                        if (detailUiState.title.isNotBlank() || detailUiState.note.isNotBlank()) {
                            detailViewModel?.addNote()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please provide some content first")
                            }
                        }
                    }
                },
                containerColor = buttonBackgroundColor
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = selectedColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            LaunchedEffect(detailUiState.noteAddedStatus) {
                if (detailUiState.noteAddedStatus) {
                    snackbarHostState.showSnackbar("Your note has been added")
                    detailViewModel?.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }
            LaunchedEffect(detailUiState.updateNoteStatus) {
                if (detailUiState.updateNoteStatus) {
                    snackbarHostState.showSnackbar("Your note has been updated")
                    detailViewModel?.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 8.dp,
                )
            ) {
                itemsIndexed(Utils.colors) { colorIndex, color ->
                    ColorItem(color = color) {
                        detailViewModel?.onColorChange(colorIndex)
                    }
                }
            }
            val borderColor = Color.Black
            val borderWidth = 2.dp
            val paddingModifier = Modifier.padding(8.dp)
            TextField(
                value = detailUiState.title,
                onValueChange = {
                    detailViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Enter your note title here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .then(paddingModifier)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            TextField(
                value = detailUiState.note,
                onValueChange = { detailViewModel?.onNoteChange(it) },
                label = { Text(text = "Enter your note contents here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .then(paddingModifier)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable {
                onClick.invoke()
            },
        border = BorderStroke(2.dp, Color.Black)
    ) {
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrevDetailScreen() {
    ColorNotesTheme {
        DetailScreen(detailViewModel = null, noteId = "") {
        }
    }
}