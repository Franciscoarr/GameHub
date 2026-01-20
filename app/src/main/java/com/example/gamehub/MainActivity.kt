package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.mockGames
import com.example.gamehub.model.Game
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        actionBar?.hide()

        setContent {
            GameHubTheme {
                val windowSize = calculateWindowSizeClass(this)
                val widthSizeClass = windowSize.widthSizeClass

                // --- ESTADOS ---
                var games by remember { mutableStateOf(mockGames) }
                var currentRoute by remember { mutableStateOf("list") }
                var selectedGameId by remember { mutableStateOf<Int?>(null) }
                var searchQuery by remember { mutableStateOf("") }
                var gameToDelete by remember { mutableStateOf<Game?>(null) }

                // --- FILTRADO ---
                val displayedGames = if (searchQuery.isEmpty()) {
                    games
                } else {
                    games.filter { it.title.contains(searchQuery, ignoreCase = true) }
                }

                val onFavToggle: (Int) -> Unit = { id ->
                    val game = games.find { it.id == id }
                    if (game != null) {
                        if (game.isFavorite) {
                            gameToDelete = game
                        } else {
                            games = games.map { if (it.id == id) it.copy(isFavorite = true) else it }
                        }
                    }
                }

                // --- DIÁLOGO BORRAR FAVORITO ---
                if (gameToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { gameToDelete = null },
                        title = { Text("Eliminar de Favoritos") },
                        text = { Text("¿Quitar ${gameToDelete?.title} de la lista?") },
                        confirmButton = {
                            TextButton(onClick = {
                                games = games.map { if (it.id == gameToDelete?.id) it.copy(isFavorite = false) else it }
                                gameToDelete = null
                            }) { Text("Eliminar", color = Color.Red) }
                        },
                        dismissButton = {
                            TextButton(onClick = { gameToDelete = null }) { Text("Cancelar") }
                        }
                    )
                }

                Scaffold(
                    bottomBar = {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "") }, label = { Text("Lista") })
                                NavigationBarItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "") }, label = { Text("Favs") })
                                NavigationBarItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") }, label = { Text("Perfil") })
                                NavigationBarItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") }, label = { Text("Info") })
                            }
                        }
                    }
                ) { padding ->

                    // LÓGICA DEL HUECO:
                    // En vertical (Compact) respetamos el padding de arriba (status bar).
                    // En horizontal (Else) lo ignoramos para ganar espacio y que no quede el hueco feo.
                    val mainModifier = if (widthSizeClass == WindowWidthSizeClass.Compact) {
                        Modifier.padding(padding).fillMaxSize()
                    } else {
                        // En horizontal solo aplicamos padding abajo (si hubiera barra) e izquierda/derecha
                        Modifier.padding(
                            bottom = padding.calculateBottomPadding(),
                            start = padding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                            end = padding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
                        ).fillMaxSize()
                    }

                    Box(modifier = mainModifier) {

                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            // --- VISTA MÓVIL VERTICAL ---
                            when (currentRoute) {
                                "list" -> Column {
                                    BeautifulSearchBar(searchQuery) { searchQuery = it }
                                    ElemListScreen(displayedGames, onGameClick = {
                                        selectedGameId = it.id
                                        currentRoute = "detail"
                                    }, onFavToggle)
                                }
                                "detail" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    if (game != null) {
                                        Column {
                                            IconButton(onClick = { currentRoute = "list" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                                            DetailItemScreen(game, onFavToggle)
                                        }
                                    }
                                }
                                "favs" -> FavListScreen(games, onGameClick = {
                                    selectedGameId = it.id
                                    currentRoute = "detail_fav"
                                }, onFavToggle)
                                "detail_fav" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    if (game != null) {
                                        Column {
                                            IconButton(onClick = { currentRoute = "favs" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                                            DetailFavScreen(game) // Recuerda usar la versión corregida arriba
                                        }
                                    }
                                }
                                "profile" -> ProfileScreen()
                                "about" -> AboutScreen()
                            }
                        } else {
                            // --- VISTA HORIZONTAL ---
                            Row(Modifier.fillMaxSize()) {

                                // 1. RAIL LATERAL (Con Scroll por si la pantalla es bajita)
                                NavigationRail(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .verticalScroll(rememberScrollState()) // Permite mover el menú si no cabe
                                            .width(80.dp), // Ancho fijo para que no baile
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        NavigationRailItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "Lista") }, label = { Text("Juegos") })
                                        Spacer(Modifier.height(10.dp))
                                        NavigationRailItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "Favs") }, label = { Text("Favs") })
                                        Spacer(Modifier.height(10.dp))
                                        NavigationRailItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "Perfil") }, label = { Text("Perfil") })
                                        Spacer(Modifier.height(10.dp))
                                        NavigationRailItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "Info") }, label = { Text("Info") })
                                    }
                                }

                                // 2. ZONA CENTRAL
                                Box(modifier = Modifier.weight(1f)) {
                                    when (currentRoute) {
                                        "list" -> {
                                            Row(Modifier.fillMaxSize()) {
                                                // IZQUIERDA: LISTA Y BUSCADOR
                                                Column(modifier = Modifier.weight(1f)) {
                                                    BeautifulSearchBar(searchQuery) { searchQuery = it }
                                                    ElemListScreen(displayedGames, onGameClick = { selectedGameId = it.id }, onFavToggle)
                                                }
                                                // DERECHA: DETALLES
                                                Box(modifier = Modifier
                                                    .weight(1f)
                                                    .padding(16.dp)
                                                    .verticalScroll(rememberScrollState()) // Scroll permitido aquí
                                                ) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    if (game != null) DetailItemScreen(game, onFavToggle)
                                                    else EmptySelectionMessage("Selecciona un juego para ver detalles")
                                                }
                                            }
                                        }
                                        "favs" -> {
                                            Row(Modifier.fillMaxSize()) {
                                                // IZQUIERDA: LISTA FAVS
                                                Box(modifier = Modifier.weight(1f)) {
                                                    FavListScreen(games, { selectedGameId = it.id }, onFavToggle)
                                                }
                                                // DERECHA: COMENTARIOS (Aquí crasheaba antes)
                                                Box(modifier = Modifier
                                                    .weight(1f)
                                                    .padding(16.dp)
                                                    .verticalScroll(rememberScrollState()) // Scroll del padre
                                                ) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    // Usamos el DetailFavScreen CORREGIDO (sin lazycolumn interna)
                                                    if (game != null && game.isFavorite) DetailFavScreen(game)
                                                    else EmptySelectionMessage("Selecciona un favorito")
                                                }
                                            }
                                        }
                                        "profile" -> ProfileScreen()
                                        "about" -> AboutScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}