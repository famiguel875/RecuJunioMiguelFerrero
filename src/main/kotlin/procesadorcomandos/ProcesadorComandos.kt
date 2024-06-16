package procesadorcomandos

import files.ModificarFichero
import gestordatos.FicheroRuta
import gestordatos.GestorOperaciones
import gestordatos.ObtenerDatos
import output.Console
import service.GestorOperacionesService
import service.ObtenerDatosService

class ProcesadorComandos(private val args: Array<String>) {
    /** Procesa los comandos de entrada para ejecutar la aplicación. */
    fun procesar() {
        val consola = Console(emptyList(), "")
        val carpetaOrigen = obtenerArgumento("-pi") ?: "csvfiles"
        val moduloId = obtenerArgumento("-mo") ?: "PRO"

        if (carpetaOrigen.isNotEmpty()) {
            procesarArchivos(carpetaOrigen, moduloId)
        } else {
            consola.showMessage("Uso: -pi <carpetaOrigen> [-mo <moduloId>]")
        }
    }

    private fun obtenerArgumento(parametro: String): String? {
        val index = args.indexOf(parametro)
        return if (index != -1 && index + 1 < args.size) args[index + 1] else null
    }

    /** Procesa los archivos. */
    private fun procesarArchivos(carpetaOrigen: String, moduloId: String) {
        val consola = Console(emptyList(), "")
        val rutasArchivos = if (moduloId.isEmpty()) {
            FicheroRuta.obtenerRutasArchivos(carpetaOrigen, "")
        } else {
            FicheroRuta.obtenerRutasArchivos(carpetaOrigen, moduloId)
        }

        for (ruta in rutasArchivos) {
            try {
                val lineas = FicheroRuta.leerFicheroTexto(ruta)

                // Crear una instancia de ObtenerDatos con las líneas y la ruta
                val obtenerDatos = ObtenerDatos(lineas, ruta)
                val obtenerDatosService = ObtenerDatosService(obtenerDatos)

                val gestorOperaciones = GestorOperaciones(lineas, ruta)
                val gestorOperacionesService = GestorOperacionesService(gestorOperaciones)

                val alumnos = obtenerDatosService.obtenerAlumnos()
                val criterios = obtenerDatosService.obtenerCriteriosEvaluacion()
                val resultados = obtenerDatosService.obtenerResultadosAprendizaje()

                Console(lineas, ruta).imprimirTabla(alumnos, criterios, resultados)

                val notasCE = gestorOperacionesService.calcularNotasCriteriosEvaluacion()
                val notasRA = gestorOperacionesService.calcularNotasResultadosAprendizaje()

                val modificarFichero = ModificarFichero()
                modificarFichero.actualizarFichero(ruta, criterios, resultados, notasCE, notasRA)
            } catch (e: Exception) {
                consola.showMessage("Error al procesar el archivo $ruta: ${e.message}")
            }
        }
    }
}
