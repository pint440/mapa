package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.database.SismoEntity
import com.example.ui.TerropingViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SismosScreen(
    viewModel: TerropingViewModel,
    modifier: Modifier = Modifier
) {
    val sismos by viewModel.sismos.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedSismo by viewModel.selectedSismo.collectAsState()

    var filterMagnitude by remember { mutableStateOf(0.0) }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var isMapViewActive by remember { mutableStateOf(false) }

    val filteredSismos = sismos.filter {
        it.mag >= filterMagnitude && (!showOnlyFavorites || it.isFavorite)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TERROPING",
                        color = SeismicRed,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Sismicidad en Venezuela",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
                
                IconButton(
                    onClick = { viewModel.refreshSismos() },
                    modifier = Modifier
                        .background(SlateSurface, CircleShape)
                        .testTag("refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refrescar Sismos",
                        tint = TechBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Banner
            errorMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = msg,
                        color = SeismicRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Hero Banner image (Generated asset)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurface)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1782698033994),
                    contentDescription = "Radar Sismológico",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                
                // Overlay text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x7F0C101A))
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "Monitoreo Sísmico Nacional",
                            color = OffWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Alertas tempranas y análisis sismológico",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Vista (Lista vs Mapa)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateSurface, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Lista de Eventos", "Mapa de Hotspots 🗺️").forEachIndexed { index, title ->
                    val isSelected = (index == 0 && !isMapViewActive) || (index == 1 && isMapViewActive)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) TechBlue else Color.Transparent)
                            .clickable { isMapViewActive = (index == 1) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) SlateBackground else OffWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterMagnitude == 0.0,
                    onClick = { filterMagnitude = 0.0 },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechBlue,
                        selectedLabelColor = SlateBackground,
                        containerColor = SlateSurface,
                        labelColor = OffWhite
                    )
                )

                FilterChip(
                    selected = filterMagnitude == 3.5,
                    onClick = { filterMagnitude = 3.5 },
                    label = { Text("M 3.5+") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SeismicOrange,
                        selectedLabelColor = SlateBackground,
                        containerColor = SlateSurface,
                        labelColor = OffWhite
                    )
                )

                FilterChip(
                    selected = filterMagnitude == 5.0,
                    onClick = { filterMagnitude = 5.0 },
                    label = { Text("M 5.0+") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SeismicRed,
                        selectedLabelColor = OffWhite,
                        containerColor = SlateSurface,
                        labelColor = OffWhite
                    )
                )

                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    label = { Text("Favoritos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SeismicRed,
                        selectedLabelColor = OffWhite,
                        containerColor = SlateSurface,
                        labelColor = OffWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List of Sismos or Map View
            if (isMapViewActive) {
                VenezuelaHotspotMap(
                    sismos = filteredSismos,
                    onSismoSelected = { sismo -> viewModel.selectSismo(sismo) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                if (isRefreshing && filteredSismos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TechBlue)
                    }
                } else if (filteredSismos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "📡",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron sismos registrados.",
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSismos, key = { it.id }) { sismo ->
                            SismoCard(
                                sismo = sismo,
                                onCardClick = { viewModel.selectSismo(sismo) },
                                onFavoriteClick = { viewModel.toggleFavorite(sismo) }
                            )
                        }
                    }
                }
            }
        }

        // Selected Sismo Details Dialog / Bottom Sheet
        selectedSismo?.let { sismo ->
            SismoDetailDialog(
                sismo = sismo,
                viewModel = viewModel,
                onDismiss = { viewModel.selectSismo(null) }
            )
        }
    }
}

@Composable
fun SismoCard(
    sismo: SismoEntity,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val magColor = when {
        sismo.mag >= 5.5 -> SeismicRed
        sismo.mag >= 4.0 -> SeismicOrange
        else -> SeismicGreen
    }

    val formattedTime = remember(sismo.time) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        sdf.format(Date(sismo.time))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("sismo_card_${sismo.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Magnitude circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(magColor.copy(alpha = 0.15f))
                    .border(2.dp, magColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f", sismo.mag),
                    color = magColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sismo.place,
                    color = OffWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedTime,
                    color = TextGray,
                    fontSize = 11.sp
                )
                
                if (sismo.feltByUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(TechBlue.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Sentido por el usuario",
                            color = TechBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Actions
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (sismo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (sismo.isFavorite) SeismicRed else TextGray
                )
            }
        }
    }
}

@Composable
fun SismoDetailDialog(
    sismo: SismoEntity,
    viewModel: TerropingViewModel,
    onDismiss: () -> Unit
) {
    val aiExplanation by viewModel.aiExplanation.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    val formattedTime = remember(sismo.time) {
        val sdf = SimpleDateFormat("EEEE d 'de' MMMM, yyyy - hh:mm a", Locale("es", "VE"))
        sdf.format(Date(sismo.time))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Title and Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle de Evento",
                        color = OffWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar", color = TechBlue)
                    }
                }

                Divider(color = SlateCard, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Magnitude display
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateCard, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val alertText = when {
                                sismo.mag >= 5.5 -> "ALERTA SÍSMICA: ALTA"
                                sismo.mag >= 4.0 -> "ALERTA SÍSMICA: MODERADA"
                                else -> "ALERTA SÍSMICA: BAJA"
                            }
                            val alertColor = when {
                                sismo.mag >= 5.5 -> SeismicRed
                                sismo.mag >= 4.0 -> SeismicOrange
                                else -> SeismicGreen
                            }

                            Text(
                                text = "MAGNITUD",
                                color = TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f M", sismo.mag),
                                color = alertColor,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = alertText,
                                color = alertColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Sismo properties
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PropertyRow(label = "Ubicación", value = sismo.place)
                            PropertyRow(label = "Fecha y Hora", value = formattedTime)
                            PropertyRow(label = "Profundidad", value = "${sismo.depth} km")
                            PropertyRow(label = "Latitud", value = "${sismo.latitude}° N")
                            PropertyRow(label = "Longitud", value = "${sismo.longitude}° O")
                        }
                    }

                    // Map Simulator (Canvas based epicentral radar visualization)
                    item {
                        Column {
                            Text(
                                text = "Visualizador del Epicentro",
                                color = TechBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(SlateBackground, RoundedCornerShape(12.dp))
                                    .border(1.dp, SlateCard, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Epicentre ripple animation
                                var pulseRadius by remember { mutableStateOf(10f) }
                                LaunchedEffect(key1 = true) {
                                    while (true) {
                                        kotlinx.coroutines.delay(50)
                                        pulseRadius = (pulseRadius + 2f) % 80f
                                    }
                                }

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    
                                    // Draw background radar lines
                                    drawCircle(
                                        color = Color(0x1F00E5FF),
                                        radius = 120f,
                                        center = center,
                                        style = Stroke(width = 1f)
                                    )
                                    drawCircle(
                                        color = Color(0x1F00E5FF),
                                        radius = 60f,
                                        center = center,
                                        style = Stroke(width = 1f)
                                    )
                                    
                                    // Draw dynamic pulsing epicenter ring
                                    val pulseColor = if (sismo.mag >= 5.0) SeismicRed else SeismicOrange
                                    drawCircle(
                                        color = pulseColor.copy(alpha = (1f - pulseRadius / 80f).coerceIn(0f, 1f)),
                                        radius = pulseRadius,
                                        center = center,
                                        style = Stroke(width = 3f)
                                    )
                                    
                                    // Draw epicenter center dot
                                    drawCircle(
                                        color = pulseColor,
                                        radius = 8f,
                                        center = center
                                    )
                                }
                                
                                Text(
                                    text = "Ubicación Sísmica Calculada",
                                    color = OffWhite.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }

                    // AI Explanation (Terroping AI Advice)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🤖 Terroping AI",
                                        color = TechBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Asistente Inteligente",
                                        color = TextGray,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                if (isAiLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TechBlue, strokeWidth = 2.dp)
                                        Text(
                                            text = "Analizando evento y fallas tectónicas de Venezuela...",
                                            color = TextGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                } else {
                                    Text(
                                        text = aiExplanation ?: "No se pudo cargar el análisis sísmico.",
                                        color = OffWhite,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Felt reported action
                    item {
                        Button(
                            onClick = { viewModel.reportFelt(sismo) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sismo.feltByUser) SlateCard else TechBlue,
                                contentColor = if (sismo.feltByUser) TextGray else SlateBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !sismo.feltByUser,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("felt_report_button")
                        ) {
                            Text(
                                text = if (sismo.feltByUser) "✓ Sismo Reportado Sentido" else "🙋 Lo sentí (Reportar vibración)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            color = OffWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.65f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun VenezuelaHotspotMap(
    sismos: List<SismoEntity>,
    onSismoSelected: (SismoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFaults by remember { mutableStateOf(true) }
    var showCities by remember { mutableStateOf(true) }
    var selectedLocalSismo by remember { mutableStateOf<SismoEntity?>(null) }
    val textMeasurer = rememberTextMeasurer()

    // Pulse animation for epicenters
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadiusScale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Boundaries vertices
    val venezuelaBoundary = remember {
        listOf(
            -71.6 to 11.8, -71.3 to 11.8, -71.3 to 11.0, -71.6 to 10.9,
            -71.0 to 10.9, -71.0 to 11.5, -70.0 to 12.2, -69.8 to 12.1,
            -69.8 to 11.7, -69.0 to 11.5, -68.3 to 10.9, -68.0 to 10.6,
            -67.0 to 10.6, -66.0 to 10.6, -65.5 to 10.2, -64.7 to 10.2,
            -64.2 to 10.4, -63.5 to 10.5, -62.2 to 10.7, -61.8 to 10.7,
            -61.8 to 10.2, -62.6 to 10.0, -62.5 to 9.5, -61.0 to 9.5,
            -60.0 to 8.5, -59.8 to 8.3, -61.2 to 7.0, -61.2 to 5.0,
            -60.5 to 4.5, -60.5 to 2.2, -61.5 to 1.5, -62.5 to 1.0,
            -63.5 to 2.2, -64.5 to 2.5, -65.5 to 1.5, -66.5 to 1.0,
            -67.0 to 1.2, -67.5 to 2.0, -67.5 to 4.0, -67.8 to 6.2,
            -69.0 to 6.2, -71.0 to 7.0, -72.0 to 7.3, -72.4 to 8.2,
            -72.2 to 9.0, -73.0 to 9.2, -73.3 to 10.0, -72.6 to 11.0,
            -71.6 to 11.8
        )
    }

    val lagoMaracaibo = remember {
        listOf(
            -71.8 to 10.2, -71.3 to 10.2, -71.0 to 9.7,
            -71.3 to 9.2, -71.7 to 9.3, -71.9 to 9.8,
            -71.8 to 10.2
        )
    }

    val cities = remember {
        listOf(
            Triple("Caracas", -66.90, 10.50),
            Triple("Maracaibo", -71.64, 10.64),
            Triple("Barquisimeto", -69.35, 10.07),
            Triple("Mérida", -71.14, 8.59),
            Triple("Cumaná", -64.18, 10.45),
            Triple("San Cristóbal", -72.22, 7.77),
            Triple("Puerto Ordaz", -62.65, 8.31)
        )
    }

    val faultBocono = remember {
        listOf(
            -72.2 to 7.8, -71.5 to 8.4, -71.1 to 8.6,
            -70.6 to 9.3, -69.7 to 9.5, -69.3 to 10.1,
            -68.7 to 10.3, -68.0 to 10.5
        )
    }

    val faultSanSebastian = remember {
        listOf(
            -68.0 to 10.5, -67.5 to 10.5, -66.9 to 10.5,
            -65.5 to 10.4, -64.7 to 10.2
        )
    }

    val faultElPilar = remember {
        listOf(
            -64.7 to 10.2, -64.2 to 10.4, -63.5 to 10.5,
            -62.8 to 10.5, -62.3 to 10.6, -61.5 to 10.6
        )
    }

    val faultOcaAncon = remember {
        listOf(
            -72.5 to 11.0, -71.6 to 11.0, -70.8 to 11.2,
            -70.0 to 11.3, -69.5 to 11.4
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Map controls card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capas del Mapa:",
                    color = OffWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showFaults,
                        onClick = { showFaults = !showFaults },
                        label = { Text("Fallas Tectónicas", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SeismicOrange.copy(alpha = 0.2f),
                            selectedLabelColor = SeismicOrange,
                            containerColor = SlateCard,
                            labelColor = TextGray
                        ),
                        modifier = Modifier.height(28.dp)
                    )

                    FilterChip(
                        selected = showCities,
                        onClick = { showCities = !showCities },
                        label = { Text("Ciudades", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechBlue.copy(alpha = 0.2f),
                            selectedLabelColor = TechBlue,
                            containerColor = SlateCard,
                            labelColor = TextGray
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }

        // Map Canvas Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SlateSurface)
                .border(1.dp, SlateCard, RoundedCornerShape(16.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(sismos) {
                        detectTapGestures { tapOffset ->
                            var closest: SismoEntity? = null
                            var minDistance = Float.MAX_VALUE
                            sismos.forEach { sismo ->
                                val offset = projectLonLat(
                                    sismo.longitude,
                                    sismo.latitude,
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                )
                                val dist = (tapOffset - offset).getDistance()
                                if (dist < minDistance) {
                                    minDistance = dist
                                    closest = sismo
                                }
                            }
                            if (minDistance < 60f && closest != null) {
                                selectedLocalSismo = closest
                            } else {
                                selectedLocalSismo = null
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                // Draw coordinates grid lines (meridians and parallels)
                val gridColor = Color(0x0A00E5FF)
                
                // Draw latitude parallels
                for (lat in 2..12 step 2) {
                    val latOffsetStart = projectLonLat(-73.5, lat.toDouble(), width, height)
                    val latOffsetEnd = projectLonLat(-59.5, lat.toDouble(), width, height)
                    drawLine(gridColor, latOffsetStart, latOffsetEnd, strokeWidth = 1f)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "$lat°N",
                        style = TextStyle(color = TextGray.copy(alpha = 0.3f), fontSize = 8.sp),
                        topLeft = Offset(10f, latOffsetStart.y - 12f)
                    )
                }
                
                // Draw longitude meridians
                for (lon in -72..-60 step 3) {
                    val lonOffsetStart = projectLonLat(lon.toDouble(), 1.0, width, height)
                    val lonOffsetEnd = projectLonLat(lon.toDouble(), 12.5, width, height)
                    drawLine(gridColor, lonOffsetStart, lonOffsetEnd, strokeWidth = 1f)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "$lon°W",
                        style = TextStyle(color = TextGray.copy(alpha = 0.3f), fontSize = 8.sp),
                        topLeft = Offset(lonOffsetStart.x - 20f, height - 20f)
                    )
                }

                // Compile Venezuela Country Outline Path
                val countryPath = Path().apply {
                    if (venezuelaBoundary.isNotEmpty()) {
                        val firstPt = projectLonLat(venezuelaBoundary[0].first, venezuelaBoundary[0].second, width, height)
                        moveTo(firstPt.x, firstPt.y)
                        for (i in 1 until venezuelaBoundary.size) {
                            val pt = projectLonLat(venezuelaBoundary[i].first, venezuelaBoundary[i].second, width, height)
                            lineTo(pt.x, pt.y)
                        }
                        close()
                    }
                }

                // Draw filled country area
                drawPath(
                    path = countryPath,
                    color = SlateBackground.copy(alpha = 0.6f)
                )

                // Draw glowing country borders
                drawPath(
                    path = countryPath,
                    color = TechDarkBlue.copy(alpha = 0.4f),
                    style = Stroke(width = 4f)
                )
                drawPath(
                    path = countryPath,
                    color = TechBlue.copy(alpha = 0.3f),
                    style = Stroke(width = 1.5f)
                )

                // Draw Lago de Maracaibo outline
                val lakePath = Path().apply {
                    if (lagoMaracaibo.isNotEmpty()) {
                        val firstPt = projectLonLat(lagoMaracaibo[0].first, lagoMaracaibo[0].second, width, height)
                        moveTo(firstPt.x, firstPt.y)
                        for (i in 1 until lagoMaracaibo.size) {
                            val pt = projectLonLat(lagoMaracaibo[i].first, lagoMaracaibo[i].second, width, height)
                            lineTo(pt.x, pt.y)
                        }
                        close()
                    }
                }
                drawPath(
                    path = lakePath,
                    color = SlateBackground,
                )
                drawPath(
                    path = lakePath,
                    color = TechBlue.copy(alpha = 0.2f),
                    style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                )

                // Draw Fault Lines
                if (showFaults) {
                    val faultsList = listOf(
                        "Falla de Boconó" to faultBocono,
                        "Falla de San Sebastián" to faultSanSebastian,
                        "Falla de El Pilar" to faultElPilar,
                        "Falla de Oca-Ancón" to faultOcaAncon
                    )

                    faultsList.forEach { (name, points) ->
                        val faultPath = Path().apply {
                            if (points.isNotEmpty()) {
                                val firstPt = projectLonLat(points[0].first, points[0].second, width, height)
                                moveTo(firstPt.x, firstPt.y)
                                for (i in 1 until points.size) {
                                    val pt = projectLonLat(points[i].first, points[i].second, width, height)
                                    lineTo(pt.x, pt.y)
                                }
                            }
                        }

                        // Glow layer
                        drawPath(
                            path = faultPath,
                            color = SeismicOrange.copy(alpha = 0.3f),
                            style = Stroke(width = 3f)
                        )
                        // Core layer
                        drawPath(
                            path = faultPath,
                            color = SeismicOrange,
                            style = Stroke(width = 1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
                        )

                        // Label at the middle of the fault
                        if (points.size > 2) {
                            val midIdx = points.size / 2
                            val labelPt = projectLonLat(points[midIdx].first, points[midIdx].second, width, height)
                            drawText(
                                textMeasurer = textMeasurer,
                                text = name.uppercase(),
                                style = TextStyle(
                                    color = SeismicOrange.copy(alpha = 0.7f),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                topLeft = Offset(labelPt.x + 8f, labelPt.y - 12f)
                            )
                        }
                    }
                }

                // Draw Cities
                if (showCities) {
                    cities.forEach { (name, lon, lat) ->
                        val cityPt = projectLonLat(lon, lat, width, height)
                        // Small dot
                        drawCircle(
                            color = OffWhite.copy(alpha = 0.8f),
                            radius = 3f,
                            center = cityPt
                        )
                        // City name
                        drawText(
                            textMeasurer = textMeasurer,
                            text = name,
                            style = TextStyle(
                                color = TextGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            topLeft = Offset(cityPt.x + 6f, cityPt.y - 6f)
                        )
                    }
                }

                // Draw Sismo Hotspots (Pulsing and Static Layers)
                sismos.forEach { sismo ->
                    val sismoPt = projectLonLat(sismo.longitude, sismo.latitude, width, height)
                    val baseRadius = (sismo.mag.toFloat() * 3f).coerceIn(6f, 25f)
                    val color = when {
                        sismo.mag >= 5.0 -> SeismicRed
                        sismo.mag >= 3.5 -> SeismicOrange
                        else -> SeismicGreen
                    }

                    // Pulse outer animation ring
                    drawCircle(
                        color = color.copy(alpha = pulseAlpha * 0.4f),
                        radius = baseRadius * pulseRadiusScale,
                        center = sismoPt
                    )

                    // Selection ring indicator
                    val isSelected = selectedLocalSismo?.id == sismo.id
                    if (isSelected) {
                        drawCircle(
                            color = TechBlue,
                            radius = baseRadius + 10f,
                            center = sismoPt,
                            style = Stroke(width = 2f)
                        )
                    }

                    // Static center core
                    drawCircle(
                        color = color,
                        radius = baseRadius * 0.6f,
                        center = sismoPt
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = baseRadius * 0.2f,
                        center = sismoPt
                    )
                }
            }

            // Legend Overlay (Top Left)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(SlateBackground.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .border(1.dp, SlateCard, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "LEYENDA SÍSMICA",
                    color = OffWhite,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SeismicRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Fuerte (M 5.0+)", color = TextGray, fontSize = 8.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SeismicOrange))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Moderado (M 3.5 - 4.9)", color = TextGray, fontSize = 8.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SeismicGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Leve (< M 3.5)", color = TextGray, fontSize = 8.sp)
                }
            }

            // Quick Info Card Overlay (Bottom Center) - appears when a hotspot is tapped
            androidx.compose.animation.AnimatedVisibility(
                visible = selectedLocalSismo != null,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(expandFrom = Alignment.Bottom),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically(shrinkTowards = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth(0.95f)
            ) {
                selectedLocalSismo?.let { sismo ->
                    val color = when {
                        sismo.mag >= 5.0 -> SeismicRed
                        sismo.mag >= 3.5 -> SeismicOrange
                        else -> SeismicGreen
                    }
                    
                    val dateFormatted = remember(sismo.time) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                        val d = Date(sismo.time)
                        sdf.format(d)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateBackground.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.border(1.dp, TechBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "M ${String.format(Locale.US, "%.1f", sismo.mag)}",
                                            color = color,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${sismo.depth} km prof.",
                                        color = TextGray,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sismo.place,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = dateFormatted,
                                    color = TextGray,
                                    fontSize = 10.sp
                                )
                            }
                            
                            Button(
                                onClick = { onSismoSelected(sismo) },
                                colors = ButtonDefaults.buttonColors(containerColor = TechBlue, contentColor = SlateBackground),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(text = "Ver Detalles", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Map Helper Projection Function
fun projectLonLat(lon: Double, lat: Double, width: Float, height: Float): Offset {
    val minLon = -73.5
    val maxLon = -59.5
    val minLat = 1.0
    val maxLat = 12.5
    
    val paddingFraction = 0.08f
    val paddedWidth = width * (1f - 2f * paddingFraction)
    val paddedHeight = height * (1f - 2f * paddingFraction)
    val startX = width * paddingFraction
    val startY = height * paddingFraction
    
    val pctX = (lon - minLon) / (maxLon - minLon)
    val pctY = (lat - minLat) / (maxLat - minLat)
    
    val x = startX + pctX.toFloat() * paddedWidth
    val y = startY + (1f - pctY.toFloat()) * paddedHeight
    
    return Offset(x, y)
}
