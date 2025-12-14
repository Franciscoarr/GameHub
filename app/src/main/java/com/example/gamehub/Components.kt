package com.example.gamehub

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamehub.model.Game
import com.example.gamehub.ui.theme.FavoriteGH
import com.example.gamehub.ui.theme.orbitronFont

//COMPONENTE 1: GameCard
@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit,
    onFavClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Placeholder de imagen
            Image(
                painter = painterResource(id = game.imageRes),
                contentDescription = "Carátula de ${game.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = game.title, 
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = orbitronFont),
                    color = MaterialTheme.colorScheme.secondary)
                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                //Uso del componente personalizado 2 dentro del 1
                RatingBadge(rating = game.rating)
            }

            IconButton(onClick = onFavClick) {
                Icon(
                    imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (game.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

//COMPONENTE 2: RatingBadge
@Composable
fun RatingBadge(rating: Double) {
    Surface(
        color = FavoriteGH.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, FavoriteGH)
    ) {
        Text(
            text = "★ $rating",
            color = FavoriteGH,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}