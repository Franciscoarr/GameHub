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
import androidx.compose.ui.res.stringResource
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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val attrib = window.attributes
            attrib.layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attrib
        }

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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

                Scaffold(
                    bottomBar = {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "list",
                                    onClick = { currentRoute = "list" },
                                    icon = { Icon(Icons.Default.Home, stringResource(R.string.nav_games)) },
                                    label = { Text(stringResource(R.string.nav_games)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "favs",
                                    onClick = { currentRoute = "favs" },
                                    icon = { Icon(Icons.Default.Star, stringResource(R.string.nav_favs)) },
                                    label = { Text(stringResource(R.string.nav_favs)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { currentRoute = "profile" },
                                    icon = { Icon(Icons.Default.Person, stringResource(R.string.nav_profile)) },
                                    label = { Text(stringResource(R.string.nav_profile)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "about",
                                    onClick = { currentRoute = "about" },
                                    icon = { Icon(Icons.Default.Info, stringResource(R.string.nav_info)) },
                                    label = { Text(stringResource(R.string.nav_info)) }
                                )
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
                                        Button(onClick = { currentRoute = "list" }, modifier = Modifier.padding(8.dp)) {
                                            Text(stringResource(R.string.back_to_list))
                                        }
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
                                            Button(onClick = { currentRoute = "favs" }, modifier = Modifier.padding(8.dp)) {
                                                Text(stringResource(R.string.back_to_favs))
                                            }
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
                                NavigationRail {
                                    Spacer(Modifier.weight(1f))
                                    NavigationRailItem(
                                        selected = currentRoute == "list",
                                        onClick = { currentRoute = "list" },
                                        icon = { Icon(Icons.Default.Home, "") },
                                        label = { Text(stringResource(R.string.nav_rail_home)) }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "favs",
                                        onClick = { currentRoute = "favs" },
                                        icon = { Icon(Icons.Default.Star, "") },
                                        label = { Text(stringResource(R.string.nav_rail_favs)) }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "profile",
                                        onClick = { currentRoute = "profile" },
                                        icon = { Icon(Icons.Default.Person, "") },
                                        label = { Text(stringResource(R.string.nav_profile)) }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "about",
                                        onClick = { currentRoute = "about" },
                                        icon = { Icon(Icons.Default.Info, "") },
                                        label = { Text(stringResource(R.string.nav_info)) }
                                    )
                                    Spacer(Modifier.weight(1f))
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    when(currentRoute) {
                                        "list" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    ElemListScreen(games, onGameClick = { selectedGameId = it.id }, onFavToggle)
                                                }
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    if (game != null) DetailItemScreen(game, onFavToggle)
                                                    else Text(stringResource(R.string.select_game_hint), Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        "favs" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    FavListScreen(games, { selectedGameId = it.id }, onRemoveFav = onFavToggle)
                                                }
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    val game = games.find { it.id == selectedGameId }
                                                    if (game != null) DetailFavScreen(game)
                                                    else Text(stringResource(R.string.select_fav_hint), Modifier.align(Alignment.Center))
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