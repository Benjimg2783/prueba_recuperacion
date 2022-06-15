// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


@Composable
@Preview
fun App() {
    val tiendaDao = TiendaDao(ConnectionBuilder().connection)
    val inventarioDao = InventarioDao(ConnectionBuilder().connection)
    var imprimirTexto by remember { mutableStateOf(false) }
    var tiendas by remember { mutableStateOf(listOf(Tienda(0, "", ""))) }
    var inventario by remember { mutableStateOf(listOf(Inventario(0, "", "", 0.0, 0))) }
    var nombreTienda by remember { mutableStateOf("") }
    var direccionTienda by remember { mutableStateOf("") }
    var introducirTienda by remember { mutableStateOf(false) }
    var introducirObjeto by remember { mutableStateOf(false) }
    var nombreArticulo by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var idTienda by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        if (!imprimirTexto) {

            if (!introducirTienda && !introducirObjeto) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        tiendas = tiendaDao.selectAll().toList()
                        imprimirTexto = true
                    }) {
                        Text(text = "info tiendas")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 50.dp)
                ) {
                    Button(onClick = {
                        inventario = inventarioDao.selectAll().toList()
                        imprimirTexto = true
                    }) {
                        Text(text = "info inventario")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 100.dp)
                ) {
                    Button(onClick = {
                        introducirTienda = true
                    }) { Text("InsertarTienda") }
                }
            }
            if (!introducirObjeto && introducirTienda) {
                Row(horizontalArrangement = Arrangement.Center) {
                    TextField(
                        value = nombreTienda,
                        onValueChange = { nombreTienda = it },
                        label = { Text("nombre de la tienda") }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 60.dp)
                ) {
                    TextField(
                        value = direccionTienda,
                        onValueChange = { direccionTienda = it },
                        label = { Text("direccion de la tienda") }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 120.dp)
                ) {
                    Button(
                        onClick = {
                            tiendaDao.insert(Tienda(tiendaDao.selectLastId() + 1, nombreTienda, direccionTienda))
                            introducirTienda = false
                        }

                    ) {
                        Text("Submit")
                    }
                }
            }
            if(introducirObjeto&&!introducirTienda){
                TextField(
                    value = nombreArticulo,
                    onValueChange = { nombreArticulo = it },
                    label = { Text("nombre del articulo") }
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 60.dp)
                ) {
                    TextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("desea añadir un comentario?") }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 120.dp)
                ) {
                    TextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("precio") },
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 180.dp)
                ) {
                    TextField(
                        value = idTienda,
                        onValueChange = { idTienda = it },
                        label = { Text("id de la tienda a la que pertenece") },
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 240.dp)
                ) {
                    Button(
                        onClick = {
                            if (inventarioDao.selectAllIdTienda().contains(idTienda.toInt())){
                                inventarioDao.insert(Inventario(inventarioDao.selectLastId()+1,nombreArticulo,comentario,precio.toDouble(),idTienda.toInt()))
                                introducirTienda = false
                            }
                        }

                    ) {
                        Text("Submit")
                    }
                }
            }
        }

        if (imprimirTexto) {
            if (inventario[0].idArticulo != 0) {
                for (i in inventario.indices) {
                    val altura = i * 50
                    Row(modifier = Modifier.padding(top = altura.dp)) { Text(inventario[i].toString()) }
                }
            }
            if (tiendas[0].idTienda != 0) {
                for (i in tiendas.indices) {
                    val altura = i * 50
                    Row(modifier = Modifier.padding(top = altura.dp)) { Text(tiendas[i].toString()) }
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    imprimirTexto = false
                    tiendas = listOf(Tienda(0, "", ""))
                    inventario = listOf(Inventario(0, "", "", 0.0, 0))
                }) { Text("Volver") }
            }
        }
    }
}


fun main() = application {
    val tiendadao = TiendaDao(ConnectionBuilder().connection)
    val inventarioDao = InventarioDao(ConnectionBuilder().connection)
    tiendadao.prepareTable()
    inventarioDao.prepareTable()
    tiendadao.insert(Tienda(1, "La Nena", "Callejon de la Nena #123, Colonia Dulce Amor"))
    tiendadao.insert(Tienda(2, "La Virgen", "Calle Rosa de Guadalupe #2, Colonia Bajo del Cerro"))
    tiendadao.insert(Tienda(3, "La Piscina", "Avenida de los Charcos #78, Colonia El Mojado"))
    tiendadao.insert(Tienda(4, "El Churro", "Calle el Pason #666, Colonia El Viaje"))
    tiendadao.insert(Tienda(5, "Don Pancho", "Avenida del Reboso #1521, Colonia El Burro"))
    inventarioDao.insert(Inventario(1, "CD-DVD", "900 MB DE ESPACIO", 35.50, 5))
    inventarioDao.insert(Inventario(2, "USB-HP", "32GB, USB 3.0", 155.90, 4))
    inventarioDao.insert(Inventario(3, "Laptop SONY", "4GB RAM, 300 HDD, i5 2.6 GHz.", 13410.07, 3))
    inventarioDao.insert(Inventario(4, "Mouse Optico", "700 DPI", 104.40, 2))
    inventarioDao.insert(Inventario(5, "Disco Duro", "200 TB, HDD, USB 3.0", 2300.00, 1))
    inventarioDao.insert(Inventario(6, "Proyector TSHB", "TOSHIBA G155", 5500.00, 5))
    Window(
        title = "Gestor DB:",
        onCloseRequest = ::exitApplication,
        icon = painterResource("db.png")
    ) {
        App()
    }
}

data class Tienda(val idTienda: Int, val nombreTienda: String, val direccionTienda: String)
data class Inventario(
    val idArticulo: Int,
    val nombre: String,
    val comentario: String,
    val precio: Double,
    val idTienda: Int
)