package com.example.gestordeproyectis.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestordeproyectis.data.Actividad
import com.example.gestordeproyectis.ui.theme.components.ActividadCard
import kotlinx.coroutines.launch

@Composable
fun InicioScreen(
    actividades: List<Actividad>,
    categoriaSeleccionada: String,
    onCategoriaChange: (String) -> Unit,
    onAgregar: () -> Unit,
    onCompletar: (Actividad) -> Unit,
    onEditar: (Actividad) -> Unit
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    val categorias = listOf(
        "Todas",
        "Escuela",
        "Trabajo",
        "Personal"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onAgregar,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar actividad"
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = 20.dp
                )
        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // --------------------------------
            // ENCABEZADO
            // --------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Mis pendientes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = obtenerMensajePendientes(
                            actividades.size
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Icono decorativo
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme
                                .primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme
                            .onPrimaryContainer
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // --------------------------------
            // RESUMEN
            // --------------------------------

            ResumenCard(
                cantidad = actividades.size
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // --------------------------------
            // FILTROS
            // --------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme
                        .onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(categorias) { categoria ->

                    FilterChip(
                        selected =
                            categoriaSeleccionada == categoria,

                        onClick = {
                            onCategoriaChange(categoria)
                        },

                        label = {
                            Text(categoria)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // --------------------------------
            // LISTA
            // --------------------------------

            if (actividades.isEmpty()) {

                EstadoVacio()

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    contentPadding = androidx.compose.foundation.layout
                        .PaddingValues(
                            bottom = 100.dp
                        )
                ) {

                    items(
                        items = actividades,
                        key = {
                            it.id
                        }
                    ) { actividad ->

                        ActividadCard(

                            actividad = actividad,

                            onCompletar = {

                                onCompletar(actividad)

                                scope.launch {

                                    snackbarHostState
                                        .showSnackbar(
                                            "✓ Actividad completada"
                                        )
                                }
                            },

                            onClick = {
                                onEditar(actividad)
                            }
                        )
                    }
                }
            }
        }
    }
}


// ======================================================
// TARJETA DE RESUMEN
// ======================================================

@Composable
private fun ResumenCard(
    cantidad: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme
                            .primary
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription = null,

                    tint =
                        MaterialTheme.colorScheme
                            .onPrimary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography
                        .titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (cantidad == 1)
                        "actividad pendiente"
                    else
                        "actividades pendientes",

                    style = MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


// ======================================================
// ESTADO VACÍO
// ======================================================

@Composable
private fun ColumnScope.EstadoVacio() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme
                            .secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,

                    contentDescription = null,

                    modifier = Modifier.size(36.dp),

                    tint =
                        MaterialTheme.colorScheme
                            .onSecondaryContainer
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Todo al día",
                style = MaterialTheme.typography
                    .titleMedium,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "No tienes actividades pendientes.",
                style = MaterialTheme.typography.bodyMedium,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}


// ======================================================
// MENSAJE DEL ENCABEZADO
// ======================================================

private fun obtenerMensajePendientes(
    cantidad: Int
): String {

    return when {

        cantidad == 0 ->
            "Todo está en orden"

        cantidad == 1 ->
            "Tienes 1 actividad pendiente"

        cantidad <= 3 ->
            "Tienes $cantidad actividades pendientes"

        else ->
            "Tienes varias actividades por completar"
    }
}
