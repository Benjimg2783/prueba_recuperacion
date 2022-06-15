import java.sql.Connection
import java.sql.SQLException
import kotlin.properties.Delegates

class InventarioDao(val c:Connection) {
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "INVENTARIO"
        private const val TRUNCATE_TABLE_INVENTARIO_SQL = "TRUNCATE TABLE INVENTARIO"
        private const val CREATE_TABLE_INVENTARIO_SQL =
            "CREATE TABLE INVENTARIO (ID_ARTICULO NUMBER(10,0) CONSTRAINT PK_ID_ARTICULO PRIMARY KEY, NOMBRE VARCHAR2(50) UNIQUE, COMENTARIO VARCHAR2(200) NOT\n" +
                    "NULL, PRECIO NUMBER(10,2) CHECK(PRECIO>0), ID_TIENDA NUMBER(10,0) CONSTRAINT FK_ID_TIENDA REFERENCES TIENDA(\n" +
                    "ID_TIENDA) )"
        private const val INSERT_INVENTARIO_SQL = "INSERT INTO INVENTARIO" + "  (ID_ARTICULO, NOMBRE, COMENTARIO,PRECIO,ID_TIENDA) VALUES " + " (?, ?, ?,?,?);"
        private const val SELECT_INVENTARIO_BY_ID = "select ID_ARTICULO, NOMBRE, COMENTARIO,PRECIO,ID_TIENDA from INVENTARIO where ID_ARTICULO =?"
        private const val SELECT_ALL_INVENTARIO = "select * from INVENTARIO"
        private const val DELETE_INVENTARIO_SQL = "delete from INVENTARIO where ID_ARTICULO = ?;"
        private const val UPDATE_INVENTARIO_SQL = "update INVENTARIO set NOMBRE= ?, COMENTARIO =?,PRECIO=?,ID_TIENDA=? where ID_ARTICULO = ?;"
        private const val UPDATE_PRECIO = "update INVENTARIO set precio= precio + (precio * 0.15) where precio > ?;"
        private const val INVENTARIO_POR_TIENDA = "select * from INVENTARIO where ID_TIENDA=?;"

    }
    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next()) truncateTable() else createTable()
    }

    private fun truncateTable() {
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_INVENTARIO_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_INVENTARIO_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun insert(inventario: Inventario) {
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_INVENTARIO_SQL).use { st ->
                st.setInt(1, inventario.idArticulo)
                st.setString(2, inventario.nombre)
                st.setString(3, inventario.comentario)
                st.setDouble(4, inventario.precio)
                st.setInt(5, inventario.idTienda)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: Int): Inventario? {
        var inventario: Inventario? = null
        try {
            c.prepareStatement(SELECT_INVENTARIO_BY_ID).use { st ->
                st.setInt(1, id)
                val rs = st.executeQuery()
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE")
                    val comentario = rs.getString("COMENTARIO")
                    val precio = rs.getDouble("PRECIO")
                    val idTienda = rs.getInt("IDTIENDA")
                    inventario = Inventario(id, nombre, comentario,precio,idTienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventario
    }

    fun selectAll(): List<Inventario> {

        val inventarios: MutableList<Inventario> = ArrayList()
        try {
            c.prepareStatement(SELECT_ALL_INVENTARIO).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("ID_ARTICULO")
                    val nombre = rs.getString("NOMBRE")
                    val comentario = rs.getString("COMENTARIO")
                    val precio = rs.getDouble("PRECIO")
                    val idTienda = rs.getInt("ID_TIENDA")
                    inventarios.add(Inventario(id, nombre, comentario,precio,idTienda))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventarios
    }

    fun deleteById(id: Int): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_INVENTARIO_SQL).use { st ->
                st.setInt(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun update(inventario: Inventario): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_INVENTARIO_SQL).use { st ->
                st.setString(2, inventario.nombre)
                st.setString(3, inventario.comentario)
                st.setDouble(4, inventario.precio)
                st.setInt(5, inventario.idTienda)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    private fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }
    }
    private fun updatePrecios(precio:Int): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_PRECIO).use { st ->
                st.setInt(1, precio)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }
    fun selectByTienda(id: Int): Inventario? {
        var inventario: Inventario? = null
        try {
            c.prepareStatement(INVENTARIO_POR_TIENDA).use { st ->
                st.setInt(1, id)
                val rs = st.executeQuery()
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE")
                    val comentario = rs.getString("COMENTARIO")
                    val precio = rs.getDouble("PRECIO")
                    val idArticulo = rs.getInt("ID_ARTICULO")
                    inventario = Inventario(idArticulo, nombre, comentario,precio,id)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventario
    }
    fun selectLastId():Int{
        var id_articulo by Delegates.notNull<Int>()
        try {
            c.prepareStatement(SELECT_ALL_INVENTARIO).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    id_articulo = rs.getInt("ID_ARTICULO")
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return id_articulo
    }
    fun selectAllIdTienda(): MutableList<Int> {
        val idTienda=mutableListOf<Int>()
        try {
            c.prepareStatement(SELECT_ALL_INVENTARIO).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("ID_TIENDA")
                    idTienda.add(id)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return idTienda
    }
}