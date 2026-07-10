package com.smartcart_merchant.features.store.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun OpenStreetMapView(
    modifier: Modifier = Modifier,
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    markerTitle: String = "",
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            if (view.tag == null) {
                view.setTileSource(TileSourceFactory.MAPNIK)
                view.setMultiTouchControls(true)
                view.controller.setZoom(14.0)
                view.controller.setCenter(GeoPoint(-12.0464, -76.9083))

                val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { onLocationSelected(it.latitude, it.longitude) }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean = false
                })
                view.overlays.add(mapEventsOverlay)
                view.tag = true
            }

            view.overlays.removeAll { it is Marker }
            if (selectedLatitude != null && selectedLongitude != null) {
                val marker = Marker(view).apply {
                    position = GeoPoint(selectedLatitude, selectedLongitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = markerTitle.ifEmpty { "Ubicación seleccionada" }
                }
                view.overlays.add(marker)
                view.invalidate()
            }
        }
    )
}
