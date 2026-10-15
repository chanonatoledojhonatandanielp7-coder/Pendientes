package com.example.gestordeproyectis

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.gestordeproyectis.data.Actividad
import com.example.gestordeproyectis.data.DatabaseHelper
import com.example.gestordeproyectis.notificaciones.ReminderReceiver
import com.example.gestordeproyectis.ui.theme.screens.ActividadFormScreen
import com.example.gestordeproyectis.ui.theme.screens.InicioScreen

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseHelper

    private val permisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseHelper(this)

        solicitarPermisoNotificaciones()

        setContent {

            MaterialTheme {

                App()
            }
        }
    }

    private fun solicitarPermisoNotificaciones() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                permisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    @Composable
    private fun App() {

        var pantalla by remember {
            mutableStateOf("inicio")
        }

        var actividadSeleccionada by remember {
            mutableStateOf<Actividad?>(null)
        }

        var categoriaSeleccionada by remember {
            mutableStateOf("Todas")
        }

        var actividades by remember {
            mutableStateOf(
                database.obtenerActividades()
            )
        }

        fun recargarActividades() {

            actividades =
                if (categoriaSeleccionada == "Todas") {
                    database.obtenerActividades()
                } else {
                    database.obtenerPorCategoria(
                        categoriaSeleccionada
                    )
                }
        }

        when (pantalla) {

            "inicio" -> {

                InicioScreen(

                    actividades = actividades,

                    categoriaSeleccionada =
                        categoriaSeleccionada,

                    onCategoriaChange = {

                        categoriaSeleccionada = it

                        actividades =
                            if (it == "Todas") {
                                database.obtenerActividades()
                            } else {
                                database.obtenerPorCategoria(it)
                            }
                    },

                    onAgregar = {

                        actividadSeleccionada = null

                        pantalla = "formulario"
                    },

                    onCompletar = { actividad ->

                        database.completarActividad(
                            actividad.id
                        )

                        cancelarRecordatorio(
                            actividad.id
                        )

                        recargarActividades()
                    },

                    onEditar = { actividad ->

                        actividadSeleccionada = actividad

                        pantalla = "formulario"
                    }
                )
            }

            "formulario" -> {

                ActividadFormScreen(

                    actividad =
                        actividadSeleccionada,

                    onCancelar = {

                        pantalla = "inicio"
                    },

                    onGuardar = {
                            titulo,
                            descripcion,
                            prioridad,
                            categoria,
                            recordatorio ->

                        if (
                            actividadSeleccionada == null
                        ) {

                            val nuevaActividad =
                                Actividad(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    prioridad = prioridad,
                                    categoria = categoria,
                                    recordatorio = recordatorio
                                )

                            val nuevoId =
                                database.insertarActividad(
                                    nuevaActividad
                                )

                            if (
                                recordatorio != null &&
                                nuevoId > 0
                            ) {

                                programarRecordatorio(
                                    nuevoId.toInt(),
                                    titulo,
                                    recordatorio
                                )
                            }

                        } else {

                            val actualizada =
                                actividadSeleccionada!!.copy(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    prioridad = prioridad,
                                    categoria = categoria,
                                    recordatorio = recordatorio
                                )

                            database.actualizarActividad(
                                actualizada
                            )

                            cancelarRecordatorio(
                                actualizada.id
                            )

                            if (
                                recordatorio != null
                            ) {

                                programarRecordatorio(
                                    actualizada.id,
                                    titulo,
                                    recordatorio
                                )
                            }
                        }

                        recargarActividades()

                        pantalla = "inicio"
                    }
                )
            }
        }
    }

    private fun programarRecordatorio(
        actividadId: Int,
        titulo: String,
        tiempo: Long
    ) {

        val alarmManager =
            getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent = Intent(
            this,
            ReminderReceiver::class.java
        ).apply {

            putExtra(
                "actividadId",
                actividadId
            )

            putExtra(
                "titulo",
                titulo
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                actividadId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        try {

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S
            ) {

                if (
                    alarmManager.canScheduleExactAlarms()
                ) {

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tiempo,
                        pendingIntent
                    )

                } else {

                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tiempo,
                        pendingIntent
                    )
                }

            } else {

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempo,
                    pendingIntent
                )
            }

        } catch (e: SecurityException) {

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tiempo,
                pendingIntent
            )
        }
    }

    private fun cancelarRecordatorio(
        actividadId: Int
    ) {

        val intent = Intent(
            this,
            ReminderReceiver::class.java
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                actividadId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            pendingIntent
        )
    }
}