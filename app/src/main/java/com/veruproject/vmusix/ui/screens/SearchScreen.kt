package com.veruproject.vmusix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.domain.model.Track
import com.veruproject.vmusix.ui.components.VmusixLoader

@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Track>,
    isSearching: Boolean,
    onTrackClick: (Track, List<Track>) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Lagu", "Artis", "Album", "Playlist")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Input Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Cari Musik",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text("Cari lagu, artis, album, atau playlist...", color = BrandConfig.MUTED_TEXT, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search icon",
                        tint = BrandConfig.MUTED_TEXT
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = BrandConfig.MUTED_TEXT
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandConfig.CARD_COLOR,
                    unfocusedContainerColor = BrandConfig.CARD_COLOR,
                    focusedBorderColor = BrandConfig.ACCENT_COLOR,
                    unfocusedBorderColor = BrandConfig.BORDER_COLOR,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else BrandConfig.MUTED_TEXT,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandConfig.PRIMARY_COLOR,
                            containerColor = BrandConfig.CARD_COLOR
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BrandConfig.BORDER_COLOR,
                            selectedBorderColor = BrandConfig.ACCENT_COLOR
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        VmusixLoader()
                    }
                }

                searchQuery.isBlank() -> {
                    // Empty state with quick suggestions
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Pencarian Populer",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )
                        }

                        val popularQueries = listOf(
                            "The Weeknd - After Hours",
                            "Billie Eilish - Birds of a Feather",
                            "Sabrina Carpenter - Espresso",
                            "Taylor Swift - Cruel Summer",
                            "Harry Styles - As It Was",
                            "Joji - Glimpse of Us",
                            "Chappell Roan - Good Luck Babe"
                        )

                        items(popularQueries) { query ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandConfig.CARD_COLOR)
                                    .clickable { onQueryChange(query) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = BrandConfig.ACCENT_COLOR,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = query,
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }

                searchResults.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tidak Ada Hasil",
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Coba kata kunci lain atau periksa koneksi internet",
                                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("search_results_list"),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(searchResults) { track ->
                            SearchResultRow(
                                track = track,
                                onClick = { onTrackClick(track, searchResults) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BrandConfig.CARD_COLOR)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.thumbnailUrl,
            contentDescription = track.title,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${track.artist} • ${track.album.ifEmpty { "Single" }}",
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandConfig.MUTED_TEXT),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = track.durationFormatted,
            style = MaterialTheme.typography.labelSmall.copy(color = BrandConfig.MUTED_TEXT)
        )
    }
}
