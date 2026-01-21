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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.Game

//ElemListScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElemListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onFavToggle: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    //Estados para el diálogo de confirmación
    var showRemoveDialog by remember { mutableStateOf(false) }
    var gameToRemove by remember { mutableStateOf<Game?>(null) }

    val filteredGames = games.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    //Lógica del Diálogo
    if (showRemoveDialog && gameToRemove != null) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Quitar de Favoritos") },
            text = { Text("¿Quieres eliminar ${gameToRemove?.title} de tus favoritos?") },
            confirmButton = {
                TextButton(onClick = {
                    onFavToggle(gameToRemove!!.id)
                    showRemoveDialog = false
                    gameToRemove = null
                }) {
                    Text("Eliminar", color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        //Barra de búsqueda pegada arriba
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar juego...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, "") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
            items(filteredGames) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game) },
                    onFavClick = {
                        //Si ya es favorito, pedir confirmación. Si no, añadir directo
                        if (game.isFavorite) {
                            gameToRemove = game
                            showRemoveDialog = true
                        } else {
                            onFavToggle(game.id)
                        }
                    }
                )
            }
        }
    }
}

//FavListScreen
@Composable
fun FavListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onRemoveFav: (Int) -> Unit
) {
    val favs = games.filter { it.isFavorite }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var gameToDeleteId by remember { mutableStateOf<Int?>(null) }

    if (showDeleteDialog && gameToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar de Favoritos") },
            text = { Text("¿Estás seguro de que deseas quitar este juego de tu lista?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveFav(gameToDeleteId!!)
                    showDeleteDialog = false
                    gameToDeleteId = null
                }) {
                    Text("Sí, borrar", color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (favs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes favoritos aún.")
        }
    } else {
        LazyColumn {
            items(favs) { game ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    GameCard(
                        game = game,
                        onClick = { onGameClick(game) },
                        onFavClick = {
                            //En esta lista desactivamos el click del corazón o lo redirigimos al diálogo
                            gameToDeleteId = game.id
                            showDeleteDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        gameToDeleteId = game.id
                        showDeleteDialog = true
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = colorResource(R.color.gh_red))
                    }
                }
            }
        }
    }
}

//DetailItemScreen
@Composable
fun DetailItemScreen(game: Game?, onFavToggle: (Int) -> Unit) {
    if (game == null) return

    //Estado para el diálogo en la pantalla de detalle
    var showRemoveDialog by remember { mutableStateOf(false) }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Quitar de Favoritos") },
            text = { Text("¿Deseas eliminar ${game.title} de favoritos?") },
            confirmButton = {
                TextButton(onClick = {
                    onFavToggle(game.id)
                    showRemoveDialog = false
                }) {
                    Text("Eliminar", color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Column(Modifier.padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(text = game.title, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Género: ${game.genre}", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (game.isFavorite) {
                    // Si ya es favorito, mostramos diálogo para quitar
                    showRemoveDialog = true
                } else {
                    // Si no lo es, añadimos directo
                    onFavToggle(game.id)
                }
            },
            colors = ButtonDefaults.buttonColors(
                // Azul (gh_blue) si es favorito, Primary si no
                containerColor = if (game.isFavorite) colorResource(R.color.gh_blue) else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (game.isFavorite) "Favorito (Quitar)" else "Añadir a Favoritos")
        }

        Spacer(Modifier.height(16.dp))
        Text(text = game.description, style = MaterialTheme.typography.bodyLarge)
    }
}

//DetailFavScreen
@Composable
fun DetailFavScreen(game: Game) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Lógica añadir comentario */ },
                containerColor = colorResource(R.color.gh_highlight)
            ) {
                Icon(Icons.Default.AddComment, contentDescription = "Comentar", tint = Color.Black)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(game.title, style = MaterialTheme.typography.headlineMedium)
            Divider(Modifier.padding(vertical = 8.dp))
            Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(game.comments) { comment ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(comment, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

//ProfileScreen
@Composable
fun ProfileScreen() {
    var isLoggedIn by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(16.dp))
        Text(if (isLoggedIn) "Usuario: Gamer123" else "Invitado", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { isLoggedIn = !isLoggedIn },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoggedIn) colorResource(R.color.gh_surface_dark) else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isLoggedIn) "Cerrar Sesión (Logout)" else "Iniciar Sesión (Login)")
        }
    }
}

//AboutScreen
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
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold),
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
            icon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = stringResource(id = R.string.email_icon_desc),
                    tint = Color.White
                )
            },
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