package com.example.gamehub

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.*
import com.example.gamehub.ui.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElemListScreen(
    viewModel: GameViewModel,
    onGameClick: (IGDBGame) -> Unit
) {
    val games by viewModel.apiGames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (games.isEmpty()) {
            // Nota: El usuario debe proveer sus credenciales de IGDB. 
            // Usando valores por defecto o vacíos por ahora.
            viewModel.fetchGames("TU_CLIENT_ID", "TU_ACCESS_TOKEN")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), modifier = Modifier.padding(padding)) {
                items(games) { game ->
                    val isFav by viewModel.isFavorite(game.id).collectAsState(initial = false)
                    GameCard(
                        game = game,
                        isFavorite = isFav,
                        onClick = { onGameClick(game) },
                        onFavClick = {
                            if (isFav) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("El elemento ya está guardado como favorito")
                                }
                            } else {
                                viewModel.addFavorite(game)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItemScreen(game: IGDBGame?, viewModel: GameViewModel) {
    if (game == null) return
    val isFav by viewModel.isFavorite(game.id).collectAsState(initial = false)

    Column(Modifier.padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(text = game.name, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Géneros: ${game.genres?.joinToString(", ") { it.name } ?: "N/A"}",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { if (!isFav) viewModel.addFavorite(game) },
            enabled = !isFav
        ) {
            Icon(if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null)
            Spacer(Modifier.width(8.dp))
            Text(if (isFav) "Guardado en Favoritos" else "Añadir a Favoritos")
        }

        Spacer(Modifier.height(16.dp))
        Text(text = game.summary ?: "Sin resumen disponible.", style = MaterialTheme.typography.bodyLarge)
        if (game.storyline != null) {
            Spacer(Modifier.height(16.dp))
            Text(text = "Historia", style = MaterialTheme.typography.titleMedium)
            Text(text = game.storyline, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FavListScreen(
    viewModel: GameViewModel,
    onGameClick: (FavoriteGame) -> Unit
) {
    val favorites by viewModel.favoriteGames.collectAsState()
    var gameToDelete by remember { mutableStateOf<FavoriteGame?>(null) }

    if (gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Estás seguro de que quieres eliminar '${gameToDelete!!.name}' de tus favoritos?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeFavorite(gameToDelete!!)
                    gameToDelete = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    if (favorites.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes juegos favoritos")
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
            items(favorites) { game ->
                FavoriteGameCard(
                    game = game,
                    onClick = { onGameClick(game) },
                    onDeleteClick = { gameToDelete = game }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailFavScreen(game: FavoriteGame, viewModel: GameViewModel) {
    val comments by viewModel.getComments(game.id).collectAsState(initial = emptyList())
    var showCommentDialog by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("Nuevo comentario") },
            text = {
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Tu comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newComment.isNotBlank()) {
                        viewModel.addComment(game.id, newComment)
                        newComment = ""
                        showCommentDialog = false
                    }
                }) { Text("Publicar") }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCommentDialog = true }) {
                Icon(Icons.Default.AddComment, "Añadir comentario")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(game.name, style = MaterialTheme.typography.headlineMedium)
            Text("Rating: ${game.rating}", style = MaterialTheme.typography.titleSmall, color = colorResource(R.color.gh_favorite))
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(game.summary, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("Comentarios", style = MaterialTheme.typography.titleLarge)
            
            comments.forEach { comment ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(comment.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: GameViewModel) {
    val settings by viewModel.userSettings.collectAsState()
    var tempName by remember { mutableStateOf(settings.username) }

    LaunchedEffect(settings.username) {
        tempName = settings.username
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, null, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = tempName,
            onValueChange = { tempName = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        Text("Tema de la aplicación", style = MaterialTheme.typography.titleMedium)
        
        AppTheme.values().forEach { theme ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = settings.theme == theme,
                    onClick = { viewModel.updateSettings(tempName, theme) }
                )
                Text(theme.name, modifier = Modifier.padding(start = 8.dp))
            }
        }
        
        Spacer(Modifier.height(24.dp))
        Button(onClick = { viewModel.updateSettings(tempName, settings.theme) }) {
            Text("Guardar Cambios")
        }
    }
}

@Composable
fun AboutScreen() {
    val scroll = rememberScrollState()
    val context = LocalContext.current

    val emailTo = stringResource(R.string.email_to)
    val emailSubject = stringResource(R.string.email_subject)
    val emailBody = stringResource(R.string.email_body)
    val chooserTitle = stringResource(R.string.chooser_title)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.gh_blue)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.app_theme),
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(R.color.gh_surface_dark)
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.app_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Spacer(Modifier.height(24.dp))

        AssistChip(
            onClick = { },
            label = { Text(text = stringResource(id = R.string.app_version)) },
            colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.tertiary),
        )
        Spacer(Modifier.height(32.dp))

        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color.White) },
            text = { Text(stringResource(id = R.string.cta_contacto_info), color = Color.White) },
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
                    putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                    putExtra(Intent.EXTRA_TEXT, emailBody)
                }
                context.startActivity(Intent.createChooser(intent, chooserTitle))
            },
            containerColor = colorResource(R.color.gh_blue)
        )
    }
}
