package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.gamehub.model.FavoriteGame
import com.example.gamehub.model.IGDBGame
import com.example.gamehub.ui.GameViewModel
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        actionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        setContent {
            val settings by viewModel.userSettings.collectAsState()

            GameHubTheme(appTheme = settings.theme) {
                val windowSize = calculateWindowSizeClass(this)
                val widthSizeClass = windowSize.widthSizeClass

                var currentRoute by remember { mutableStateOf("list") }
                var selectedGame by remember { mutableStateOf<IGDBGame?>(null) }
                var selectedFav by remember { mutableStateOf<FavoriteGame?>(null) }

                Scaffold(
                    bottomBar = {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "list" || currentRoute == "detail",
                                    onClick = { currentRoute = "list" },
                                    icon = { Icon(Icons.Default.Home, stringResource(R.string.nav_games)) },
                                    label = { Text(stringResource(R.string.nav_games)) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "favs" || currentRoute == "detail_fav",
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
                    Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                        if (widthSizeClass == WindowWidthSizeClass.Compact) {
                            when (currentRoute) {
                                "list" -> ElemListScreen(
                                    viewModel = viewModel,
                                    onGameClick = {
                                        selectedGame = it
                                        currentRoute = "detail"
                                    }
                                )
                                "detail" -> {
                                    Column {
                                        IconButton(onClick = { currentRoute = "list" }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                        }
                                        DetailItemScreen(selectedGame, viewModel)
                                    }
                                }
                                "favs" -> FavListScreen(
                                    viewModel = viewModel,
                                    onGameClick = {
                                        selectedFav = it
                                        currentRoute = "detail_fav"
                                    }
                                )
                                "detail_fav" -> {
                                    if (selectedFav != null) {
                                        Column {
                                            IconButton(onClick = { currentRoute = "favs" }) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                            }
                                            DetailFavScreen(selectedFav!!, viewModel)
                                        }
                                    }
                                }
                                "profile" -> ProfileScreen(viewModel)
                                "about" -> AboutScreen()
                            }
                        } else {
                            Row(Modifier.fillMaxSize()) {
                                NavigationRail {
                                    Spacer(Modifier.weight(1f))
                                    NavigationRailItem(
                                        selected = currentRoute == "list",
                                        onClick = { currentRoute = "list" },
                                        icon = { Icon(Icons.Default.Home, "") },
                                        label = { Text("Juegos") }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "favs",
                                        onClick = { currentRoute = "favs" },
                                        icon = { Icon(Icons.Default.Star, "") },
                                        label = { Text("Favoritos") }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "profile",
                                        onClick = { currentRoute = "profile" },
                                        icon = { Icon(Icons.Default.Person, "") },
                                        label = { Text("Perfil") }
                                    )
                                    NavigationRailItem(
                                        selected = currentRoute == "about",
                                        onClick = { currentRoute = "about" },
                                        icon = { Icon(Icons.Default.Info, "") },
                                        label = { Text("Info") }
                                    )
                                    Spacer(Modifier.weight(1f))
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    when(currentRoute) {
                                        "list" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    ElemListScreen(viewModel) { selectedGame = it }
                                                }
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    if (selectedGame != null) DetailItemScreen(selectedGame, viewModel)
                                                    else Text("Selecciona un juego", Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        "favs" -> {
                                            Row {
                                                Box(Modifier.weight(1f)) {
                                                    FavListScreen(viewModel) { selectedFav = it }
                                                }
                                                Box(Modifier.weight(1f).padding(16.dp)) {
                                                    if (selectedFav != null) DetailFavScreen(selectedFav!!, viewModel)
                                                    else Text("Selecciona un favorito", Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        "profile" -> ProfileScreen(viewModel)
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
