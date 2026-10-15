package com.example.gestordeproyectis.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gestordeproyectis.data.Actividad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActividadCard(
    actividad: Actividad,
    onCompletar: () -> Unit,
    onClick: () -> Unit
) {

    val prioridadColor = when (actividad.prioridad) {

        "ALTA" -> Color(0xFFE53935)

        "MEDIA" -> Color(0xFFF9A825)

        "BAJA" -> Color(0xFF43A047)

        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .height(70.dp)
                    .background(
                        color = prioridadColor,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium
                )

                if (actividad.descripcion.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "${actividad.categoria} • ${actividad.prioridad}",
                    style = MaterialTheme.typography.labelMedium,
                    color = prioridadColor
                )

                actividad.recordatorio?.let {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "🔔 ${formatearFecha(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Checkbox(
                checked = false,
                onCheckedChange = {
                    onCompletar()
                }
            )
        }
    }
}

private fun formatearFecha(fecha: Long): String {

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formato.format(Date(fecha))
}