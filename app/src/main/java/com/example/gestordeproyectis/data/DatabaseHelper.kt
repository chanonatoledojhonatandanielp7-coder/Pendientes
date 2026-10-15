package com.example.gestordeproyectis.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "gestor_pendientes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val sql = """
            CREATE TABLE actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                descripcion TEXT,
                fecha INTEGER,
                recordatorio INTEGER,
                prioridad TEXT NOT NULL,
                categoria TEXT NOT NULL,
                completada INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS actividades")
        onCreate(db)
    }

    fun insertarActividad(actividad: Actividad): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("titulo", actividad.titulo)
            put("descripcion", actividad.descripcion)
            put("fecha", actividad.fecha)
            put("recordatorio", actividad.recordatorio)
            put("prioridad", actividad.prioridad)
            put("categoria", actividad.categoria)
            put("completada", if (actividad.completada) 1 else 0)
        }

        return db.insert("actividades", null, valores)
    }

    fun obtenerActividades(): List<Actividad> {

        val lista = mutableListOf<Actividad>()

        val db = readableDatabase

        val cursor = db.query(
            "actividades",
            null,
            "completada = ?",
            arrayOf("0"),
            null,
            null,
            """
                CASE prioridad
                    WHEN 'ALTA' THEN 1
                    WHEN 'MEDIA' THEN 2
                    WHEN 'BAJA' THEN 3
                    ELSE 4
                END
            """.trimIndent()
        )

        cursor.use {

            while (it.moveToNext()) {

                lista.add(
                    Actividad(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        titulo = it.getString(
                            it.getColumnIndexOrThrow("titulo")
                        ),
                        descripcion = it.getString(
                            it.getColumnIndexOrThrow("descripcion")
                        ) ?: "",
                        fecha = if (
                            it.isNull(it.getColumnIndexOrThrow("fecha"))
                        ) {
                            null
                        } else {
                            it.getLong(
                                it.getColumnIndexOrThrow("fecha")
                            )
                        },
                        recordatorio = if (
                            it.isNull(
                                it.getColumnIndexOrThrow("recordatorio")
                            )
                        ) {
                            null
                        } else {
                            it.getLong(
                                it.getColumnIndexOrThrow("recordatorio")
                            )
                        },
                        prioridad = it.getString(
                            it.getColumnIndexOrThrow("prioridad")
                        ),
                        categoria = it.getString(
                            it.getColumnIndexOrThrow("categoria")
                        ),
                        completada = it.getInt(
                            it.getColumnIndexOrThrow("completada")
                        ) == 1
                    )
                )
            }
        }

        return lista
    }

    fun obtenerActividad(id: Int): Actividad? {

        val db = readableDatabase

        val cursor = db.query(
            "actividades",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                return Actividad(
                    id = it.getInt(
                        it.getColumnIndexOrThrow("id")
                    ),
                    titulo = it.getString(
                        it.getColumnIndexOrThrow("titulo")
                    ),
                    descripcion = it.getString(
                        it.getColumnIndexOrThrow("descripcion")
                    ) ?: "",
                    fecha = if (
                        it.isNull(
                            it.getColumnIndexOrThrow("fecha")
                        )
                    ) null else it.getLong(
                        it.getColumnIndexOrThrow("fecha")
                    ),
                    recordatorio = if (
                        it.isNull(
                            it.getColumnIndexOrThrow("recordatorio")
                        )
                    ) null else it.getLong(
                        it.getColumnIndexOrThrow("recordatorio")
                    ),
                    prioridad = it.getString(
                        it.getColumnIndexOrThrow("prioridad")
                    ),
                    categoria = it.getString(
                        it.getColumnIndexOrThrow("categoria")
                    ),
                    completada = it.getInt(
                        it.getColumnIndexOrThrow("completada")
                    ) == 1
                )
            }
        }

        return null
    }

    fun actualizarActividad(actividad: Actividad): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("titulo", actividad.titulo)
            put("descripcion", actividad.descripcion)
            put("fecha", actividad.fecha)
            put("recordatorio", actividad.recordatorio)
            put("prioridad", actividad.prioridad)
            put("categoria", actividad.categoria)
        }

        return db.update(
            "actividades",
            valores,
            "id = ?",
            arrayOf(actividad.id.toString())
        )
    }

    fun completarActividad(id: Int): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("completada", 1)
        }

        return db.update(
            "actividades",
            valores,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun obtenerPorCategoria(categoria: String): List<Actividad> {

        if (categoria == "Todas") {
            return obtenerActividades()
        }

        val lista = mutableListOf<Actividad>()

        val db = readableDatabase

        val cursor = db.query(
            "actividades",
            null,
            "completada = ? AND categoria = ?",
            arrayOf("0", categoria),
            null,
            null,
            """
                CASE prioridad
                    WHEN 'ALTA' THEN 1
                    WHEN 'MEDIA' THEN 2
                    WHEN 'BAJA' THEN 3
                    ELSE 4
                END
            """.trimIndent()
        )

        cursor.use {

            while (it.moveToNext()) {

                lista.add(
                    Actividad(
                        id = it.getInt(
                            it.getColumnIndexOrThrow("id")
                        ),
                        titulo = it.getString(
                            it.getColumnIndexOrThrow("titulo")
                        ),
                        descripcion = it.getString(
                            it.getColumnIndexOrThrow("descripcion")
                        ) ?: "",
                        fecha = if (
                            it.isNull(
                                it.getColumnIndexOrThrow("fecha")
                            )
                        ) null else it.getLong(
                            it.getColumnIndexOrThrow("fecha")
                        ),
                        recordatorio = if (
                            it.isNull(
                                it.getColumnIndexOrThrow("recordatorio")
                            )
                        ) null else it.getLong(
                            it.getColumnIndexOrThrow("recordatorio")
                        ),
                        prioridad = it.getString(
                            it.getColumnIndexOrThrow("prioridad")
                        ),
                        categoria = it.getString(
                            it.getColumnIndexOrThrow("categoria")
                        ),
                        completada = false
                    )
                )
            }
        }

        return lista
    }
}