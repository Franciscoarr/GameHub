package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamehub.ui.theme.GameHubTheme
import com.example.gamehub.ui.theme.orbitronFont
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
        setContent {
            GameHubTheme {
                val windowSize = calculateWindowSizeClass(this)
                val widthSizeClass = windowSize.widthSizeClass

                // Estado Global simple para la demo
                var games by remember { mutableStateOf(mockGames) }

                // Lógica de navegación simple
                var currentRoute by remember { mutableStateOf("list") }
                var selectedGameId by remember { mutableStateOf<Int?>(null) }

                val onFavToggle: (Int) -> Unit = { id ->
                    games = games.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
                }

                // Layout Principal con BottomBar para móvil
                Scaffold(
                    bottomBar = {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Home, "") }, label = { Text("Juegos") })
                                NavigationBarItem(selected = currentRoute == "favs", onClick = { currentRoute = "favs" }, icon = { Icon(Icons.Default.Star, "") }, label = { Text("Favs") })
                                NavigationBarItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") }, label = { Text("Perfil") })
                                NavigationBarItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") }, label = { Text("About") })
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {

                        // LÓGICA DE AUTOMATIZACIÓN DE PANTALLAS
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            // --- VISTA COMPACTA (Móvil) ---
                            when (currentRoute) {
                                "list" -> ElemListScreen(games, onGameClick = {
                                    selectedGameId = it.id
                                    currentRoute = "detail"
                                }, onFavToggle)
                                "detail" -> {
                                    val game = games.find { it.id == selectedGameId }
                                    // Botón atrás simple simulado
                                    Column {
                                        Button(onClick = { currentRoute = "list" }) { Text("Volver") }
                                        DetailItemScreen(game, onFavToggle)
                                    }
                                }
                                "favs" -> FavListScreen(games, { }, onFavToggle)
                                "profile" -> ProfileScreen()
                                "about" -> AboutScreen()
                            }
                        } else {
                            // --- VISTA EXPANDIDA (Tablet/Foldable) ---
                            // Variante Master-Detail (Lista a la izquierda, Detalle a la derecha)
                            Row(Modifier.fillMaxSize()) {
                                // Panel Izquierdo: Navegación Lateral o Lista
                                NavigationRail {
                                    NavigationRailItem(selected = currentRoute == "list", onClick = { currentRoute = "list" }, icon = { Icon(Icons.Default.Person, "") })
                                    NavigationRailItem(selected = currentRoute == "profile", onClick = { currentRoute = "profile" }, icon = { Icon(Icons.Default.Person, "") })
                                    NavigationRailItem(selected = currentRoute == "about", onClick = { currentRoute = "about" }, icon = { Icon(Icons.Default.Info, "") })
                                }

                                // Contenido Central
                                if (currentRoute == "list") {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ElemListScreen(games, onGameClick = { selectedGameId = it.id }, onFavToggle)
                                    }
                                    // Panel Derecho (Detalle) siempre visible
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
            onClick = { /* decorativo */ },
            label = { Text(text = stringResource(id = R.string.app_version)) },
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

@Preview(
    name = "GameHub – Dark",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewAboutDark() {
    GameHubTheme {
        Surface(Modifier.fillMaxSize()) {
            AboutScreen()
        }
    }
}
