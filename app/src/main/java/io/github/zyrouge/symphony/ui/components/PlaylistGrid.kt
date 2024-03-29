package io.github.zyrouge.symphony.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import io.github.zyrouge.symphony.services.groove.GrooveKinds
import io.github.zyrouge.symphony.services.groove.Playlist
import io.github.zyrouge.symphony.services.groove.PlaylistRepository
import io.github.zyrouge.symphony.services.groove.PlaylistSortBy
import io.github.zyrouge.symphony.ui.helpers.ViewContext

@Composable
fun PlaylistGrid(
    context: ViewContext,
    playlists: List<Playlist>,
    leadingContent: @Composable () -> Unit = {},
) {
    var sortBy by remember {
        mutableStateOf(
            context.symphony.settings.getLastUsedPlaylistsSortBy() ?: PlaylistSortBy.TITLE,
        )
    }
    var sortReverse by remember {
        mutableStateOf(context.symphony.settings.getLastUsedPlaylistsSortReverse())
    }
    val sortedPlaylists by remember {
        derivedStateOf { PlaylistRepository.sort(playlists, sortBy, sortReverse) }
    }

    MediaSortBarScaffold(
        mediaSortBar = {
            Column {
                leadingContent()
                MediaSortBar(
                    context,
                    reverse = sortReverse,
                    onReverseChange = {
                        sortReverse = it
                        context.symphony.settings.setLastUsedPlaylistsSortReverse(it)
                    },
                    sort = sortBy,
                    sorts = PlaylistSortBy.values().associateWith { x -> { x.label(it) } },
                    onSortChange = {
                        sortBy = it
                        context.symphony.settings.setLastUsedPlaylistsSortBy(it)
                    },
                    label = {
                        Text(context.symphony.t.XPlaylists(playlists.size.toString()))
                    },
                )
            }
        },
        content = {
            ResponsiveGrid {
                itemsIndexed(
                    sortedPlaylists,
                    key = { i, x -> "$i-${x.id}" },
                    contentType = { _, _ -> GrooveKinds.PLAYLIST }
                ) { _, playlist ->
                    PlaylistTile(context, playlist)
                }
            }
        }
    )
}

private fun PlaylistSortBy.label(context: ViewContext) = when (this) {
    PlaylistSortBy.CUSTOM -> context.symphony.t.Custom
    PlaylistSortBy.TITLE -> context.symphony.t.Title
    PlaylistSortBy.TRACKS_COUNT -> context.symphony.t.TrackCount
}
