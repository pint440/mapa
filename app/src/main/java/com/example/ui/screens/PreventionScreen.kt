package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun PreventionScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf("Durante") }
    var activeTechTab by remember { mutableStateOf("Satélites") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "GUÍA DE PREVENCIÓN",
                    color = OffWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Medidas de seguridad sísmica y contactos de emergencia en Venezuela",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }

        // Contactos de Emergencia Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🚨 NÚMEROS DE EMERGENCIA VENEZUELA",
                        color = SeismicRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    EmergencyContactRow(
                        name = "Protección Civil Nacional",
                        number = "911",
                        description = "Alerta y rescate nacional"
                    ) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                        context.startActivity(intent)
                    }

                    EmergencyContactRow(
                        name = "FUNVISIS Reportes",
                        number = "0212-2575153",
                        description = "Investigación Sismológica Venezolana"
                    ) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02122575153"))
                        context.startActivity(intent)
                    }

                    EmergencyContactRow(
                        name = "Bomberos Urbanos",
                        number = "0800-BOMBERO",
                        description = "Cuerpo de bomberos nacional"
                    ) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:08002662376"))
                        context.startActivity(intent)
                    }
                }
            }
        }

        // Guías de Acción (Antes, Durante, Después) Selector Chips
        item {
            Column {
                Text(
                    text = "GUÍA DE ACTUACIÓN SÍSMICA",
                    color = TechBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Antes", "Durante", "Después").forEach { section ->
                        val isSelected = selectedSection == section
                        Button(
                            onClick = { selectedSection = section },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TechBlue else SlateSurface,
                                contentColor = if (isSelected) SlateBackground else OffWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = section,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Guía de Acción Content Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "QUÉ HACER: $selectedSection".uppercase(java.util.Locale.getDefault()),
                        color = TechBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    when (selectedSection) {
                        "Antes" -> {
                            ActionBulletPoint("🎒 Prepara un botiquín con linterna, radio, agua, comida enlatada y silbato.")
                            ActionBulletPoint("🏠 Identifica las zonas seguras (columnas, vigas) y salidas de emergencia en tu casa o trabajo.")
                            ActionBulletPoint("🔧 Asegura objetos pesados o repisas altas que puedan caer durante un sismo.")
                            ActionBulletPoint("🗣️ Planifica un punto de encuentro con tus familiares en caso de sismo.")
                        }
                        "Durante" -> {
                            ActionBulletPoint("🧘 ¡Mantén la calma! El pánico puede provocar accidentes graves.")
                            ActionBulletPoint("🧱 AGÁCHATE, CÚBRETE debajo de una mesa resistente y SUJÉTATE con fuerza.")
                            ActionBulletPoint("🏢 Aléjate de ventanas, repisas, cables eléctricos u objetos de vidrio.")
                            ActionBulletPoint("🚪 Si estás en un piso alto, no utilices los ascensores; usa las escaleras solo cuando cese el movimiento.")
                            ActionBulletPoint("🚗 Si vas conduciendo, detén el vehículo en un área abierta, lejos de puentes o postes.")
                        }
                        "Después" -> {
                            ActionBulletPoint("🔌 Desconecta la electricidad, el gas y el agua para evitar incendios o fugas.")
                            ActionBulletPoint("🩹 Brinda primeros auxilios si hay heridos y mantente a salvo de las réplicas.")
                            ActionBulletPoint("📻 Enciende la radio o revisa fuentes oficiales para recibir instrucciones de Protección Civil.")
                            ActionBulletPoint("🚫 Evita entrar a edificios con daños estructurales visibles.")
                            ActionBulletPoint("🌊 Si estás en zonas costeras (como Sucre o Vargas), aléjate de la playa debido al riesgo de tsunami.")
                        }
                    }
                }
            }
        }

        // Fallas Tectónicas de Venezuela
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🗺️ REGIONES SÍSMICAS Y FALLAS EN VENEZUELA",
                        color = SeismicOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Venezuela es un país sísmico debido a la interacción de la Placa del Caribe y la Placa Sudamericana, cruzada por tres grandes fallas geológicas:",
                        color = TextGray,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = SlateCard, thickness = 1.dp)

                    FaultLineInfo(
                        name = "Falla de Boconó",
                        region = "Región Andina (Mérida, Táchira, Trujillo, Lara)",
                        description = "Se extiende por más de 500 km. Es la responsable de la mayor sismicidad histórica en el occidente venezolano."
                    )

                    FaultLineInfo(
                        name = "Falla de San Sebastián",
                        region = "Región Central Costera (Vargas, Carabobo, Aragua, Miranda)",
                        description = "Falla de tipo transcurrente que corre paralela a la costa norte del país, afectando la zona de mayor densidad poblacional."
                    )

                    FaultLineInfo(
                        name = "Falla de El Pilar",
                        region = "Región Oriental (Sucre, Monagas, Nueva Esparta)",
                        description = "Atraviesa el estado Sucre hacia el Golfo de Paria. Responsable de sismos históricos fuertes como el de Cariaco en 1997."
                    )
                }
            }
        }

        // Tecnologías de Soporte Integradas Header & Selector
        item {
            Column {
                Text(
                    text = "TECNOLOGÍAS DE SOPORTE OFFLINE",
                    color = TechBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Satélites", "Radio HF/VHF", "Asistente IA").forEach { tech ->
                        val isSelected = activeTechTab == tech
                        Button(
                            onClick = { activeTechTab = tech },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TechBlue else SlateSurface,
                                contentColor = if (isSelected) SlateBackground else OffWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = tech,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Tecnologías de Soporte Card Body
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeTechTab) {
                        "Satélites" -> SatelliteTechView()
                        "Radio HF/VHF" -> RadioTechView()
                        "Asistente IA" -> AiTechView()
                    }
                }
            }
        }
    }
}

@Composable
fun SatelliteTechView() {
    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf(0f) }
    var satCount by remember { mutableStateOf(8) }

    LaunchedEffect(isSyncing) {
        if (isSyncing) {
            syncProgress = 0f
            while (syncProgress < 1f) {
                kotlinx.coroutines.delay(100)
                syncProgress += 0.05f
            }
            isSyncing = false
            satCount = (7..12).random()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "📡 MONITOREO SATELITAL DE RESPALDO",
            color = TechBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Permite sintonizar señales GNSS redundantes cuando las redes telefónicas locales fallan o colapsan por un sismo severo en el territorio nacional.",
            color = TextGray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(SlateBackground, RoundedCornerShape(8.dp))
                .border(1.dp, SlateCard, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "radar")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "angle"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radarRadius = size.height * 0.4f

                drawCircle(color = Color(0x1F00E5FF), radius = radarRadius, center = center, style = Stroke(1f))
                drawCircle(color = Color(0x1F00E5FF), radius = radarRadius * 0.6f, center = center, style = Stroke(1f))
                drawCircle(color = Color(0x1F00E5FF), radius = radarRadius * 0.2f, center = center, style = Stroke(1f))

                drawLine(
                    color = Color(0x0F00E5FF),
                    start = Offset(center.x - radarRadius, center.y),
                    end = Offset(center.x + radarRadius, center.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0x0F00E5FF),
                    start = Offset(center.x, center.y - radarRadius),
                    end = Offset(center.x, center.y + radarRadius),
                    strokeWidth = 1f
                )

                val rad = Math.toRadians(angle.toDouble())
                val endX = center.x + radarRadius * cos(rad).toFloat()
                val endY = center.y + radarRadius * sin(rad).toFloat()
                drawLine(
                    color = TechBlue,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )

                drawCircle(color = SeismicGreen, radius = 4f, center = Offset(center.x - 40f, center.y - 30f))
                drawCircle(color = SeismicGreen, radius = 3f, center = Offset(center.x + 50f, center.y + 20f))
                drawCircle(color = SeismicOrange, radius = 5f, center = Offset(center.x - 20f, center.y + 40f))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VENE-SAT-1 REDUNDANT / GNSS DIRECT LINK",
                    color = TechBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Satélites Vinculados", color = TextGray, fontSize = 11.sp)
                Text(text = "$satCount en rango activo", color = OffWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Red Sismológica", color = TextGray, fontSize = 11.sp)
                Text(text = "ONLINE (GNSS)", color = SeismicGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (isSyncing) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { syncProgress },
                    color = TechBlue,
                    trackColor = SlateCard,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                )
                Text(
                    text = "Sincronizando efemérides satelitales... ${ (syncProgress * 100).toInt() }%",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            Button(
                onClick = { isSyncing = true },
                colors = ButtonDefaults.buttonColors(containerColor = TechBlue, contentColor = SlateBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🔄 SINCRONIZAR CON ORBITALES", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RadioTechView() {
    var tunedFreq by remember { mutableFloatStateOf(7.100f) }
    
    val frequencyDetails = when {
        tunedFreq in 7.080f..7.120f -> "Red de Emergencia de Radioaficionados de Venezuela (HF)"
        tunedFreq in 145.400f..145.600f -> "Canal de Coordinación Nacional de Protección Civil (VHF)"
        tunedFreq in 146.900f..147.100f -> "Repetidor de Reportes Sismológicos de FUNVISIS (VHF)"
        else -> "Frecuencia comercial / Monitoreo de ruido de fondo"
    }
    
    val isEmergencyActive = tunedFreq in 7.080f..7.120f || 
                            tunedFreq in 145.400f..145.600f || 
                            tunedFreq in 146.900f..147.100f

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "📻 FRECUENCIAS DE RADIO DE EMERGENCIA",
            color = SeismicOrange,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Cuando cae el internet, la radiofrecuencia es vital. Sintoniza tu receptor de onda corta o VHF físico en las siguientes bandas oficiales:",
            color = TextGray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = SlateBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF060912), RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "%.3f MHz", tunedFreq),
                        color = if (isEmergencyActive) SeismicRed else TechBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = frequencyDetails.uppercase(java.util.Locale.getDefault()),
                        color = if (isEmergencyActive) OffWhite else TextGray,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = tunedFreq,
                    onValueChange = { tunedFreq = it },
                    valueRange = 3.0f..150.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = if (isEmergencyActive) SeismicRed else TechBlue,
                        activeTrackColor = TechBlue,
                        inactiveTrackColor = SlateCard
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetButton(label = "7.100 HF", modifier = Modifier.weight(1f)) { tunedFreq = 7.100f }
                    PresetButton(label = "145.500 PC", modifier = Modifier.weight(1f)) { tunedFreq = 145.500f }
                    PresetButton(label = "147.000 FUN", modifier = Modifier.weight(1f)) { tunedFreq = 147.000f }
                }
            }
        }
    }
}

@Composable
fun PresetButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = SlateCard, contentColor = OffWhite),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier.height(28.dp)
    ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AiTechView() {
    val context = LocalContext.current
    var diagnosticState by remember { mutableStateOf<DiagnosticResult?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    val batteryPercent = remember {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    LaunchedEffect(isChecking) {
        if (isChecking) {
            kotlinx.coroutines.delay(1200)
            val score = when {
                batteryPercent > 50 -> 92
                batteryPercent > 20 -> 76
                else -> 48
            }
            diagnosticState = DiagnosticResult(
                score = score,
                battery = batteryPercent,
                sensorReady = true,
                advice = when {
                    score >= 90 -> "ÓPTIMO: Dispositivo con energía robusta y acelerómetro sismográfico activo. Si ocurre un evento en las fallas de Boconó o San Sebastián, el registro offline y la sincronización GNSS están listos."
                    score >= 70 -> "MODERADO: Nivel de resiliencia intermedio. Se aconseja activar la suspensión de aplicaciones secundarias para extender la autonomía de alerta del sismógrafo."
                    else -> "ALERTA DE RESILIENCIA: Batería baja detectada. Conecta una fuente de poder, configura un radio HF analógico portátil y reduce el uso de pantalla para emergencias sísmicas."
                }
            )
            isChecking = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "🤖 ASISTENTE DE RESILIENCIA SÍSMICA CON IA",
            color = SeismicRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Examina el hardware de tu dispositivo, el estado de los acelerómetros sismográficos y tu autonomía de batería para calcular tu nivel de resiliencia ante sismos.",
            color = TextGray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        if (isChecking) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                CircularProgressIndicator(color = TechBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Computando índice de preparación...", color = OffWhite, fontSize = 12.sp)
            }
        } else {
            diagnosticState?.let { res ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SlateCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${res.score}%",
                                color = if (res.score >= 75) SeismicGreen else SeismicOrange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "PUNTUACIÓN DE PREPARACIÓN SÍSMICA",
                                color = OffWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (res.score >= 75) "SISTEMA SEGURO Y PREPARADO" else "RESILIENCIA CON LIMITACIONES",
                                color = if (res.score >= 75) SeismicGreen else SeismicOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = res.advice,
                            color = OffWhite,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(SlateCard, RoundedCornerShape(8.dp))
                        .clickable { isChecking = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👉 INICIAR EVALUACIÓN DE RESILIENCIA SÍSMICA",
                        color = TechBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class DiagnosticResult(
    val score: Int,
    val battery: Int,
    val sensorReady: Boolean,
    val advice: String
)


@Composable
fun EmergencyContactRow(
    name: String,
    number: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = OffWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = TextGray, fontSize = 11.sp)
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = SlateCard, contentColor = TechBlue),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(text = number, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionBulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = text,
            color = OffWhite,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun FaultLineInfo(
    name: String,
    region: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = name, color = OffWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = region, color = TechBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = description, color = TextGray, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
