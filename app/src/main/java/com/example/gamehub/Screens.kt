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
import com.example.gamehub.model.Game

//1. ElemListScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElemListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onFavToggle: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var gameToRemove by remember { mutableStateOf<Game?>(null) }

    val context = LocalContext.current

    val filteredGames = games.filter { game ->
        val title = context.getString(game.titleRes)
        // CAMBIO: Usamos startsWith para filtrar por la primera letra/comienzo
        title.startsWith(searchQuery, ignoreCase = true)
    }

    if (showRemoveDialog && gameToRemove != null) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text(stringResource(R.string.remove_fav_title)) },
            text = { Text(stringResource(R.string.remove_fav_msg, stringResource(gameToRemove!!.titleRes))) },
            confirmButton = {
                TextButton(onClick = {
                    onFavToggle(gameToRemove!!.id)
                    showRemoveDialog = false
                    gameToRemove = null
                }) {
                    Text(stringResource(R.string.delete), color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_placeholder)) },
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

//2. FavListScreen
@Composable
fun FavListScreen(
    games: List<Game>,
    onGameClick: (Game) -> Unit,
    onRemoveFav: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val favs = games.filter { game ->
        val isFav = game.isFavorite
        val title = context.getString(game.titleRes)

        // CAMBIO: Usamos startsWith también aquí
        isFav && title.startsWith(searchQuery, ignoreCase = true)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var gameToDeleteId by remember { mutableStateOf<Int?>(null) }

    if (showDeleteDialog && gameToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.remove_fav_confirm_title)) },
            text = { Text(stringResource(R.string.remove_fav_confirm_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveFav(gameToDeleteId!!)
                    showDeleteDialog = false
                    gameToDeleteId = null
                }) {
                    Text(stringResource(R.string.yes_delete), color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, "") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        if (favs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (searchQuery.isEmpty()) {
                    Text(stringResource(R.string.no_favs_yet))
                } else {
                    Text("No hay resultados que empiecen por \"$searchQuery\"")
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                items(favs) { game ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GameCard(
                            game = game,
                            onClick = { onGameClick(game) },
                            onFavClick = {
                                gameToDeleteId = game.id
                                showDeleteDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            gameToDeleteId = game.id
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = colorResource(R.color.gh_red))
                        }
                    }
                }
            }
        }
    }
}

//3. DetailItemScreen
@Composable
fun DetailItemScreen(game: Game?, onFavToggle: (Int) -> Unit) {
    if (game == null) return

    var showRemoveDialog by remember { mutableStateOf(false) }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text(stringResource(R.string.remove_fav_title)) },
            text = { Text(stringResource(R.string.remove_fav_detail_msg, stringResource(game.titleRes))) },
            confirmButton = {
                TextButton(onClick = {
                    onFavToggle(game.id)
                    showRemoveDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = colorResource(R.color.gh_red))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    Column(Modifier.padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(text = stringResource(game.titleRes), style = MaterialTheme.typography.headlineMedium)
        Text(text = stringResource(R.string.genre_label, stringResource(game.genreRes)), style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (game.isFavorite) {
                    showRemoveDialog = true
                } else {
                    onFavToggle(game.id)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (game.isFavorite) colorResource(R.color.gh_blue) else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (game.isFavorite) stringResource(R.string.fav_remove_btn) else stringResource(R.string.fav_add_btn))
        }

        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(game.descriptionRes), style = MaterialTheme.typography.bodyLarge)
    }
}

//4. DetailFavScreen
@Composable
fun DetailFavScreen(game: Game) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Lógica añadir comentario */ },
                containerColor = colorResource(R.color.gh_highlight)
            ) {
                Icon(Icons.Default.AddComment, contentDescription = stringResource(R.string.add_comment_desc), tint = Color.Black)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(stringResource(game.titleRes), style = MaterialTheme.typography.headlineMedium)
            Divider(Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.comments_title), style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(game.commentsRes) { commentResId ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(stringResource(commentResId), modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

//5. ProfileScreen
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
        Text(if (isLoggedIn) stringResource(R.string.user_profile) else stringResource(R.string.guest_user), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { isLoggedIn = !isLoggedIn },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoggedIn) colorResource(R.color.gh_surface_dark) else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isLoggedIn) stringResource(R.string.logout) else stringResource(R.string.login))
        }
    }
}

//6. AboutScreen
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