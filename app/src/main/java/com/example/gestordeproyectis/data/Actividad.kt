package com.example.gestordeproyectis.data

data class Actividad(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String = "",
    val fecha: Long? = null,
    val recordatorio: Long? = null,
    val prioridad: String = "MEDIA",
    val categoria: String = "Personal",
    val completada: Boolean = false
)
