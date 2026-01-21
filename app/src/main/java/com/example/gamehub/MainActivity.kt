package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.gamehub.model.mockGames
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        //Pantalla completa
        //Esto permite que la app dibuje detrás de las barras
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val attrib = window.attributes
            attrib.layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attrib
        }

        //Ocultar barras
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            GameHubTheme {
                //Cálculo del tamaño de ventana para adaptabilidad
                val windowSize = calculateWindowSizeClass(this)
                val widthSizeClass = windowSize.widthSizeClass

                //Estado de los juegos
                var games by remember { mutableStateOf(mockGames) }

                //Navegación y estado
                var currentRoute by remember { mutableStateOf("list") }
                var selectedGameId by remember { mutableStateOf<Int?>(null) }

                //Lógica para añadir/quitar favoritos
                val onFavToggle: (Int) -> Unit = { id ->
                    games = games.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
                }

                Scaffold(
                    bottomBar = {
                        //Solo mostramos BottomBar en modo Compacto
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "Juegos") }, label = { Text("Juegos") })
                                NavigationBarItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "Favoritos") }, label = { Text("Favoritos") })
                                NavigationBarItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "Perfil") }, label = { Text("Perfil") })
                                NavigationBarItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "Info") }, label = { Text("Info") })
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {

                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            //VISTA MÓVIL
                            when (currentRoute) {
                                "list" -> ElemListScreen(
                                    games = games,
                                    onGameClick = {
                                        selectedGameId = it.id
                                        currentRoute = "detail"
                                    },
                                    onFavToggle = onFavToggle
                                )
                                "detail" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    Column {
                                        Button(onClick = { currentRoute = "list" }, modifier = Modifier.padding(8.dp)) { Text("Volver a Lista") }
                                        DetailItemScreen(game, onFavToggle)
                                    }
                                }
                                "favs" -> FavListScreen(
                                    games = games,
                                    onGameClick = {
                                        selectedGameId = it.id
                                        currentRoute = "detail_fav"
                                    },
                                    onRemoveFav = onFavToggle
                                )
                                "detail_fav" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    if (game != null) {
                                        Column {
                                            Button(onClick = { currentRoute = "favs" }, modifier = Modifier.padding(8.dp)) { Text("Volver a Favoritos") }
                                            DetailFavScreen(game)
                                        }
                                    }
                                }
                                "profile" -> ProfileScreen()
                                "about" -> AboutScreen()
                            }
                        } else {
                            //VISTA TABLET
                            Row(Modifier.fillMaxSize()) {
                                //Panel Izquierdo
                                NavigationRail {
                                    Spacer(Modifier.weight(1f))
                                    NavigationRailItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "") }, label = { Text("Home") })
                                    NavigationRailItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "") }, label = { Text("Favs") })
                                    NavigationRailItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") }, label = { Text("Perfil") })
                                    NavigationRailItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") }, label = { Text("Info") })
                                    Spacer(Modifier.weight(1f))
                                }

                                //Contenido
                                Box(modifier = Modifier.weight(1f)) {
                                    when(currentRoute) {
                                        "list" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    ElemListScreen(games, onGameClick = { selectedGameId = it.id }, onFavToggle)
                                                }
                                                //Panel de Detalle a la derecha
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    if (game != null) DetailItemScreen(game, onFavToggle)
                                                    else Text("Selecciona un juego", Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        "favs" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    FavListScreen(games, { selectedGameId = it.id }, onRemoveFav = onFavToggle)
                                                }
                                                //Panel de Detalle Favoritos a la derecha
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    if (game != null) DetailFavScreen(game)
                                                    else Text("Selecciona un favorito", Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        "profile" -> ProfileScreen()
                                        "about" -> AboutScreen()
                                        else -> AboutScreen()
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