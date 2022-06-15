import java.sql.Connection
import java.sql.SQLException
import kotlin.properties.Delegates

class TiendaDao(val c:Connection) {
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "TIENDA"
        private const val TRUNCATE_TABLE_TIENDA_SQL = "TRUNCATE TABLE TIENDA"
        private const val CREATE_TABLE_TIENDA_SQL =
            "CREATE TABLE TIENDA (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200))"
        private const val INSERT_TIENDA_SQL = "INSERT INTO TIENDA" + "  (ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA) VALUES " + " (?, ?, ?);"
        private const val SELECT_TIENDA_BY_ID = "select ID_TIENDA,NOMBRE_TIENDA,DIRECCION_TIENDA from TIENDA where ID_TIENDA =?"
        private const val SELECT_ALL_TIENDA = "select * from TIENDA"
        private const val DELETE_TIENDA_SQL = "delete from TIENDA where ID_TIENDA = ?;"
        private const val UPDATE_TIENDA_SQL = "update TIENDA set ID_TIENDA= ?, puntuacion =? where cftid = ?;"
    }
    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next())  truncateTable() else createTable()
    }

    private fun truncateTable() {
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_TIENDA_SQL)
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
                st.execute(CREATE_TABLE_TIENDA_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun insert(tienda: Tienda) {
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_TIENDA_SQL).use { st ->
                st.setInt(1, tienda.idTienda)
                st.setString(2, tienda.nombreTienda)
                st.setString(3, tienda.direccionTienda)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: Int): Tienda? {
        var tienda: Tienda? = null
        try {
            c.prepareStatement(SELECT_TIENDA_BY_ID).use { st ->
                st.setInt(1, id)
                val rs = st.executeQuery()
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tienda = Tienda(id, nombre, direccion)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tienda
    }

    fun selectAll(): List<Tienda> {

        val tiendas: MutableList<Tienda> = ArrayList()
        try {
            c.prepareStatement(SELECT_ALL_TIENDA).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("ID_TIENDA")
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tiendas.add(Tienda(id, nombre, direccion))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tiendas
    }

    fun deleteById(id: Int): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_TIENDA_SQL).use { st ->
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

    fun update(tienda: Tienda): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_TIENDA_SQL).use { st ->
                st.setInt(1, tienda.idTienda)
                st.setString(2, tienda.nombreTienda)
                st.setString(3, tienda.direccionTienda)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }
    fun selectLastId():Int{
        var id_tienda by Delegates.notNull<Int>()
        try {
            c.prepareStatement(SELECT_ALL_TIENDA).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    id_tienda = rs.getInt("ID_TIENDA")
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return id_tienda
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
}