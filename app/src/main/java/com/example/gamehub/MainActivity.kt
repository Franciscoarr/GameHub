package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamehub.ui.theme.GameHubTheme
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import com.example.gamehub.model.mockGames

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            GameHubTheme {
                val windowSize = calculateWindowSizeClass(this)
                val widthSizeClass = windowSize.widthSizeClass

                var games by remember { mutableStateOf(mockGames) }

                var currentRoute by remember { mutableStateOf("list") }
                var selectedGameId by remember { mutableStateOf<Int?>(null) }

                val onFavToggle: (Int) -> Unit = { id ->
                    games = games.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
                }

                //Layout Principal
                Scaffold(
                    bottomBar = {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "") }, label = { Text("Juegos") })
                                NavigationBarItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "") }, label = { Text("Favoritos") })
                                NavigationBarItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") }, label = { Text("Perfil") })
                                NavigationBarItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") }, label = { Text("Información") })
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {

                        //Auomatización de pantallas
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            //Vista Móvil
                            when (currentRoute) {
                                "detail_fav" -> {
                                    val game = games.find { it.id == selectedGameId }

                                    if (game != null) {
                                        Column {
                                            Button(onClick = { currentRoute = "favs" }) {
                                                Text("Volver a Favoritos")
                                            }
                                            DetailFavScreen(game)
                                        }
                                    }
                                }
                                "list" -> ElemListScreen(games, onGameClick = {
                                    selectedGameId = it.id
                                    currentRoute = "detail"
                                }, onFavToggle)
                                "detail" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    Column {
                                        Button(onClick = { currentRoute = "list" }) { Text("Volver") }
                                        DetailItemScreen(game, onFavToggle)
                                    }
                                }
                                "favs" -> FavListScreen(games, onGameClick = {
                                    selectedGameId = it.id
                                    currentRoute = "detail_fav"
                                }, onFavToggle)
                                "profile" -> ProfileScreen()
                                "about" -> AboutScreen()
                            }
                        } else {
                            //Vista Tablet
                            Row(Modifier.fillMaxSize()) {
                                //Panel Izquierdo
                                NavigationRail {
                                    NavigationRailItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "") })
                                    NavigationRailItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "") })
                                    NavigationRailItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") })
                                    NavigationRailItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") })
                                }

                                //Contenido Central
                                if (currentRoute == "list") {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ElemListScreen(games, onGameClick = { selectedGameId = it.id }, onFavToggle)
                                    }
                                    //Panel Derecho
                                    Box(modifier = Modifier.weight(1.5f).padding(16.dp)) {
                                        val game = games.find { it.id == selectedGameId }
                                        if (game != null) {
                                            DetailItemScreen(game, onFavToggle)
                                        } else {
                                            Text("Selecciona un juego de la lista para ver detalles", modifier = Modifier.align(Alignment.Center))
                                        }
                                    }
                                } else if (currentRoute == "about") {
                                    AboutScreen()
                                } else if (currentRoute == "favs") {
                                    Box(modifier = Modifier.weight(1f)) {
                                        FavListScreen(
                                            games = games,
                                            { selectedGameId = it.id },
                                            onFavToggle
                                        )
                                    }

                                    //Panel Derecho
                                    Box(modifier = Modifier.weight(1.5f).padding(16.dp)) {
                                        val game = games.find { it.id == selectedGameId }

                                        if (game != null) {
                                            DetailFavScreen(game)
                                        } else {
                                            Column(
                                                modifier = Modifier.align(Alignment.Center),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "Selecciona un favorito para ver los comentarios",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    ProfileScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
