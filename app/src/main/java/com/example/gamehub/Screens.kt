package com.example.gamehub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.Game
import com.example.gamehub.ui.theme.RedGH
import com.example.gamehub.ui.theme.orbitronFont

//1. ElemListScreen (Lista General)
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

//2. DetailItemScreen (Detalle General)
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

//3. FavListScreen (Lista Favoritos)
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

//4. DetailFavScreen (Detalle Favorito con Comentarios)
@Composable
fun DetailFavScreen(game: Game) {
    // Quitamos el Scaffold y el LazyColumn para evitar el CRASH con el scroll padre
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(game.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        Text("Tus Comentarios:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Usamos forEach en lugar de LazyColumn porque el Scroll ya lo tiene la caja contenedora
        if (game.comments.isNotEmpty()) {
            game.comments.forEach { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = comment,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Text("No hay comentarios aún.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        // Botón decorativo de añadir comentario
        Button(onClick = { /* Lógica futura */ }) {
            Icon(Icons.Default.Add, "Añadir")
            Spacer(Modifier.width(8.dp))
            Text("Añadir comentario")
        }
    }
}

//5. ProfileScreen (Perfil)
@Composable
fun ProfileScreen() {
    // Estado local para controlar si el usuario está logueado o no
    var isLoggedIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isLoggedIn) "Usuario: Gamer123" else "Modo Invitado",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { isLoggedIn = !isLoggedIn }, // Cambia el estado al pulsar
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoggedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            // El texto cambia según el estado
            Text(text = if (isLoggedIn) "Cerrar Sesión (Logout)" else "Iniciar Sesión (Login)")
        }
    }
}

//6. AboutScreen (Sobre la aplicación)
@Composable
fun AboutScreen(
    onEmailClick: () -> Unit = {}
) {
    val scroll = rememberScrollState()

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
                fontWeight = FontWeight.Bold,
                fontFamily = orbitronFont),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.app_theme),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = orbitronFont
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.app_description),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        AssistChip(
            onClick = {  },
            label = { Text(text = stringResource(id = R.string.app_version)) },
            colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.tertiary),
        )
        Spacer(Modifier.height(32.dp))

        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.cd_enviar_email)) },
            text = { Text(stringResource(R.string.cta_contacto_info)) },
            onClick = onEmailClick,
            containerColor = MaterialTheme.colorScheme.secondary
        )
    }
}