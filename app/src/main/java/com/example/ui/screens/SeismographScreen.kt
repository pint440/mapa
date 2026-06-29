package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TerropingViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun SeismographScreen(
    viewModel: TerropingViewModel,
    modifier: Modifier = Modifier
) {
    val vibrationData by viewModel.vibrationData.collectAsState()
    val isSensorAvailable by viewModel.isSensorAvailable.collectAsState()

    // Store a sliding window of the last 150 points for the wave line
    val historyPoints = remember { mutableStateListOf<Float>() }
    val maxPoints = 150

    // Fill initial points
    if (historyPoints.isEmpty()) {
        repeat(maxPoints) { historyPoints.add(0f) }
    }

    // Capture vibration changes and add to history
    LaunchedEffect(vibrationData) {
        historyPoints.add(vibrationData)
        if (historyPoints.size > maxPoints) {
            historyPoints.removeAt(0)
        }
    }

    // Slowly decay/stabilize current wave if no accelerometer updates are coming
    LaunchedEffect(key1 = true) {
        while (true) {
            delay(40)
            if (historyPoints.isNotEmpty()) {
                // Decay the oldest points slightly towards 0 to simulate real damping
                for (i in 0 until historyPoints.size) {
                    historyPoints[i] = historyPoints[i] * 0.95f
                }
            }
        }
    }

    // Compute status text based on peak vibration value
    val currentAmp = kotlin.math.abs(vibrationData)
    val (statusText, statusColor) = when {
        currentAmp >= 2.0f -> "¡Sismo Fuerte Detectado!" to SeismicRed
        currentAmp >= 0.5f -> "Vibración Moderada" to SeismicOrange
        currentAmp >= 0.1f -> "Actividad Leve" to TechBlue
        else -> "Señal en Reposo" to SeismicGreen
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "SISMÓGRAFO TERROPING",
            color = OffWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Sensor de vibración del dispositivo en tiempo real",
            color = TextGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = statusText.uppercase(Locale.getDefault()),
                    color = statusColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "AMPLITUD MAX", color = TextGray, fontSize = 9.sp)
                        Text(
                            text = String.format(Locale.US, "%.3f G", currentAmp),
                            color = OffWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = SlateCard
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ACELERÓMETRO", color = TextGray, fontSize = 9.sp)
                        Text(
                            text = if (isSensorAvailable) "ACTIVO" else "SIMULADO",
                            color = if (isSensorAvailable) SeismicGreen else SeismicOrange,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seismograph Chart (The Grid Canvas with connecting lines)
        Text(
            text = "SISMOGRAMA (Registro de Ondas)",
            color = TechBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070A11))
                .border(1.dp, SlateCard, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val midY = height / 2f
                
                // Draw seismograph graph paper grids (green tech look)
                val gridColor = Color(0x1F00E676)
                // Horizontal lines
                val horizontalLinesCount = 10
                for (i in 0..horizontalLinesCount) {
                    val y = (height / horizontalLinesCount) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }
                // Vertical scrolling grids
                val verticalLinesCount = 15
                for (i in 0..verticalLinesCount) {
                    val x = (width / verticalLinesCount) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f
                    )
                }

                // Draw central baseline
                drawLine(
                    color = Color(0x3FFF5252),
                    start = Offset(0f, midY),
                    end = Offset(width, midY),
                    strokeWidth = 2f
                )

                // Draw rolling waveform path
                if (historyPoints.size > 1) {
                    val path = Path()
                    val dx = width / (maxPoints - 1)
                    
                    path.moveTo(0f, midY)
                    
                    for (i in 0 until historyPoints.size) {
                        val x = i * dx
                        // Scale the wave to fit Canvas height
                        // Limit/clamp points so they stay on screen
                        val rawVal = historyPoints[i]
                        val minClamp = 10f
                        val maxClamp = (height - 10f).coerceAtLeast(minClamp)
                        val scaleY = (midY - (rawVal * 80f)).coerceIn(minClamp, maxClamp)
                        path.lineTo(x, scaleY)
                    }

                    drawPath(
                        path = path,
                        color = SeismicRed,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // Neon glowing indicators on seismograph screen
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SeismicRed)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "REG",
                    color = SeismicRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions & Tips
        if (!isSensorAvailable) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Aviso: Este dispositivo no posee acelerómetro o se está usando un emulador. Se simularán pequeñas ondas basales de la Tierra, pero puedes disparar ondas manualmente.",
                    color = TextGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = "Consejo: ¡Mueve o agita físicamente tu teléfono para ver la aguja reaccionar en tiempo real a las vibraciones!",
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        // Simular Pulso Button
        Button(
            onClick = {
                // Simulate a high energy shockpulse by writing directly to vibrationState in VM
                val simulatedVal = viewModel.triggerManualPulse()
                // Update history points with multiple decaying waves for realism
                historyPoints[historyPoints.size - 1] = simulatedVal
                historyPoints[historyPoints.size - 2] = -simulatedVal * 0.8f
                historyPoints[historyPoints.size - 3] = simulatedVal * 0.6f
            },
            colors = ButtonDefaults.buttonColors(containerColor = TechBlue, contentColor = SlateBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("simulate_pulse_button")
        ) {
            Text(
                text = "💥 SIMULAR ONDA SÍSMICA (Pulso Manual)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
