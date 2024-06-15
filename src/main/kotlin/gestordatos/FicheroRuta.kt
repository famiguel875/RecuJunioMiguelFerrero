package gestordatos

import java.io.File

class FicheroRuta {
    companion object {
        /**
         * Lista que contiene los ficheros junto con la ruta a la carpeta.
         */

        val rutasArchivos = listOf(
            "csvfiles/un1-PlanificaciónyDiario PRO - 2223 -v1 - 3EV Actividades_Instrumentos.csv",
            "csvfiles/un2-PlanificaciónyDiario PRO - 2223 -v1 - 3EV Actividades_Instrumentos.csv",
            "csvfiles/un1-PlanificaciónyDiario BBDD - 2223 -v1 - 3EV Actividades_Instrumentos.csv"
        )

        /**
         * Función que lee un archivo de texto y devuelve sus líneas como una lista de cadenas.
         */

        fun leerFicheroTexto(ruta: String): List<String> = File(ruta).readLines()

        /**
         * Función que devuelve las rutas de los archivos en una carpeta que contienen un identificador de módulo específico en su nombre.
         */

        fun obtenerRutasArchivos(carpeta: String, moduloId: String): List<String> {
            val carpetaArchivo = File(carpeta)
            return if (carpetaArchivo.exists() && carpetaArchivo.isDirectory) {
                carpetaArchivo.listFiles { _, name -> name.contains(moduloId) }
                    ?.map { it.absolutePath }
                    ?: listOf()
            } else {
                listOf()
            }
        }
    }
}