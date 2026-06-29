package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ReporteEntity
import com.example.ui.TerropingViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(
    viewModel: TerropingViewModel,
    modifier: Modifier = Modifier
) {
    val reportes by viewModel.reportes.collectAsState()

    var city by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf("Leve") }

    val intensities = listOf("Leve", "Moderado", "Fuerte")

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
            Text(
                text = "REPORTAR TEMBLOR",
                color = OffWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Si sentiste un sismo en tu zona, repórtalo aquí. Se guardará en tu registro local.",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Report Form
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "NUEVO REPORTE",
                        color = TechBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // City input
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciudad / Municipio (en Venezuela)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = SlateCard,
                            focusedLabelColor = TechBlue,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_city_input")
                    )

                    // Intensity Selector
                    Column {
                        Text(
                            text = "Intensidad Sentida:",
                            color = TextGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            intensities.forEach { option ->
                                val isSelected = intensity == option
                                val btnBg = if (isSelected) {
                                    when (option) {
                                        "Fuerte" -> SeismicRed
                                        "Moderado" -> SeismicOrange
                                        else -> SeismicGreen
                                    }
                                } else {
                                    SlateCard
                                }
                                val btnText = if (isSelected) SlateBackground else OffWhite

                                Button(
                                    onClick = { intensity = option },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = btnBg,
                                        contentColor = btnText
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("intensity_chip_$option"),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Comments input
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("Comentarios adicionales (ej. Se movieron objetos)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = SlateCard,
                            focusedLabelColor = TechBlue,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_comments_input")
                    )

                    // Submit Button
                    Button(
                        onClick = {
                            if (city.isNotBlank()) {
                                viewModel.submitReporte(intensity, city, comments)
                                city = ""
                                comments = ""
                                intensity = "Leve"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SeismicRed, contentColor = OffWhite),
                        shape = RoundedCornerShape(8.dp),
                        enabled = city.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_report_button")
                    ) {
                        Text(
                            text = "ENVIAR REPORTE LOCAL",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reports History List
            Text(
                text = "MIS REPORTES REGISTRADOS",
                color = TechBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (reportes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No has enviado reportes locales de temblores todavía.",
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(reportes, key = { it.id }) { reporte ->
                        ReporteCard(
                            reporte = reporte,
                            onDeleteClick = { viewModel.deleteReporte(reporte.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReporteCard(
    reporte: ReporteEntity,
    onDeleteClick: () -> Unit
) {
    val badgeColor = when (reporte.intensity) {
        "Fuerte" -> SeismicRed
        "Moderado" -> SeismicOrange
        else -> SeismicGreen
    }

    val formattedDate = remember(reporte.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        sdf.format(Date(reporte.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = reporte.intensity.uppercase(Locale.getDefault()),
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = reporte.city,
                        color = OffWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (reporte.comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reporte.comments,
                        color = OffWhite,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Registrado el $formattedDate",
                    color = TextGray,
                    fontSize = 10.sp
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar Reporte",
                    tint = TextGray
                )
            }
        }
    }
}
