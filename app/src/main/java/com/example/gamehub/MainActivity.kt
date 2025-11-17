package com.example.gamehub

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.gamehub.ui.theme.GameHubTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepOnScreen = true
        splash.setKeepOnScreenCondition { keepOnScreen }

        setContent {
            LaunchedEffect(Unit) { delay(1000); keepOnScreen = false }
            GameHubTheme {
                val ctx = LocalContext.current
                Surface(Modifier.fillMaxSize()) {
                    AboutScreen(
                        onEmailClick = {
                            val to = ctx.getString(R.string.email_to)
                            val subject = ctx.getString(R.string.email_subject)
                            val body = ctx.getString(R.string.email_body)

                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${Uri.encode(to)}")
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                                putExtra(Intent.EXTRA_TEXT, body)
                            }

                            try {
                                ctx.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        ctx.getString(R.string.chooser_title)
                                    )
                                )
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.no_email_apps),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
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
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.app_theme),
            style = MaterialTheme.typography.titleMedium,
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
            label = { Text(text = stringResource(id = R.string.app_version)) }
        )
        Spacer(Modifier.height(32.dp))

        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.cd_enviar_email)) },
            text = { Text(stringResource(R.string.cta_contacto_info)) },
            onClick = onEmailClick
        )
    }
}


@Preview(
    name = "GameHub – Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewAboutLight() {
    GameHubTheme {
        Surface(Modifier.fillMaxSize()) {
            AboutScreen(onEmailClick = {})
        }
    }
}

@Preview(
    name = "GameHub – Dark",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewAboutDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize()) {
            AboutScreen(onEmailClick = {})
        }
    }
}