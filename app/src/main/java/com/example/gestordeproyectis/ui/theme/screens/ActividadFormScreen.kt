package com.example.gestordeproyectis.ui.theme.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gestordeproyectis.data.Actividad
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ActividadFormScreen(
    actividad: Actividad?,
    onGuardar: (
        titulo: String,
        descripcion: String,
        prioridad: String,
        categoria: String,
        recordatorio: Long?
    ) -> Unit,
    onCancelar: () -> Unit
) {

    val context = LocalContext.current

    var titulo by remember {
        mutableStateOf(
            actividad?.titulo ?: ""
        )
    }

    var descripcion by remember {
        mutableStateOf(
            actividad?.descripcion ?: ""
        )
    }

    var prioridad by remember {
        mutableStateOf(
            actividad?.prioridad ?: "MEDIA"
        )
    }

    var categoria by remember {
        mutableStateOf(
            actividad?.categoria ?: "Personal"
        )
    }

    var recordatorio by remember {
        mutableStateOf(
            actividad?.recordatorio
        )
    }

    var error by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        TextButton(
            onClick = onCancelar
        ) {
            Text("← Regresar")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = if (actividad == null)
                "Nueva actividad"
            else
                "Editar actividad"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                error = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Título")
            },
            singleLine = true
        )

        if (error.isNotEmpty()) {

            Text(
                text = error
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Descripción")
            },
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text("Prioridad")

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "ALTA",
                "MEDIA",
                "BAJA"
            ).forEach { opcion ->

                FilterChip(
                    selected = prioridad == opcion,
                    onClick = {
                        prioridad = opcion
                    },
                    label = {
                        Text(opcion)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text("Categoría")

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "Escuela",
                "Trabajo",
                "Personal"
            ).forEach { opcion ->

                FilterChip(
                    selected = categoria == opcion,
                    onClick = {
                        categoria = opcion
                    },
                    label = {
                        Text(opcion)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
        Text("Agregar recordatorio")
        Button(
            onClick = {

                val calendar = Calendar.getInstance()

                val dateDialog = DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        val selectedCalendar =
                            Calendar.getInstance()

                        selectedCalendar.set(
                            year,
                            month,
                            day
                        )

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->

                                selectedCalendar.set(
                                    Calendar.HOUR_OF_DAY,
                                    hour
                                )

                                selectedCalendar.set(
                                    Calendar.MINUTE,
                                    minute
                                )

                                selectedCalendar.set(
                                    Calendar.SECOND,
                                    0
                                )

                                recordatorio =
                                    selectedCalendar.timeInMillis

                            },
                            calendar.get(
                                Calendar.HOUR_OF_DAY
                            ),
                            calendar.get(
                                Calendar.MINUTE
                            ),
                            true
                        ).show()

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                dateDialog.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (recordatorio == null)
                    "🔔  recordar"
                else
                    "🔔 Cambiar recordatorio"
            )
        }

        recordatorio?.let {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Programado: ${
                    SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(it)
                }"
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Button(
            onClick = {

                if (titulo.trim().isEmpty()) {

                    error = "El título es obligatorio"

                } else {

                    onGuardar(
                        titulo.trim(),
                        descripcion.trim(),
                        prioridad,
                        categoria,
                        recordatorio
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (actividad == null)
                    "Guardar actividad"
                else
                    "Guardar cambios"
            )
        }
    }
}