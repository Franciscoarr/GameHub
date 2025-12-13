package com.example.gamehub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.Game
import com.example.gamehub.ui.theme.RedGH

// 1. ElemListScreen (Lista General)
@Composable
fun ElemListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onFavToggle: (Int) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
        items(games) { game ->
            GameCard(
                game = game,
                onClick = { onGameClick(game) },
                onFavClick = { onFavToggle(game.id) }
            )
        }
    }
}

// 2. DetailItemScreen (Detalle General)
@Composable
fun DetailItemScreen(game: Game?, onFavToggle: (Int) -> Unit) {
    if (game == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Selecciona un juego")
        }
        return
    }

    Column(Modifier.padding(24.dp).fillMaxSize()) {
        Text(text = game.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onFavToggle(game.id) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (game.isFavorite) RedGH else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (game.isFavorite) "Quitar Favorito" else "Añadir Favorito")
        }
        Spacer(Modifier.height(16.dp))
        Text(text = game.description, style = MaterialTheme.typography.bodyLarge)
    }
}

// 3. FavListScreen (Lista Favoritos)
@Composable
fun FavListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onRemoveFav: (Int) -> Unit
) {
    val favs = games.filter { it.isFavorite }
    LazyColumn {
        items(favs) { game ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                GameCard(game = game, onClick = { onGameClick(game) }, onFavClick = {}, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemoveFav(game.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RedGH)
                }
            }
        }
    }
}

// 4. DetailFavScreen (Detalle Favorito con Comentarios)
@Composable
fun DetailFavScreen(game: Game) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Lógica añadir comentario */ }) {
                Icon(Icons.Default.Add, contentDescription = "Comentar")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(game.title, style = MaterialTheme.typography.headlineMedium)
            Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(game.comments) { comment ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(comment, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

// 5. ProfileScreen
@Composable
fun ProfileScreen() {
    var isLoggedIn by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isLoggedIn) "Usuario: Gamer123" else "Invitado", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { isLoggedIn = !isLoggedIn }) {
            Text(if (isLoggedIn) "Logout" else "Login")
        }
    }
}