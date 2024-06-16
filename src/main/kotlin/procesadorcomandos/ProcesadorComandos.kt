package procesadorcomandos

import entity.CriterioEvaluacion
import entity.ResultadoAprendizaje
import entity.Unidad
import gestordatos.FicheroRuta
import gestordatos.GestorOperaciones
import gestordatos.LimpiarDatos
import gestordatos.ObtenerDatos
import output.Console
import service.GestorOperacionesService
import service.ObtenerDatosService
import java.io.File

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

                actualizarFichero(ruta, criterios, resultados, notasCE, notasRA)
            } catch (e: Exception) {
                consola.showMessage("Error al procesar el archivo $ruta: ${e.message}")
            }
        }
    }

    private fun actualizarFichero(
        ruta: String,
        criterios: List<CriterioEvaluacion>,
        resultados: List<ResultadoAprendizaje>,
        notasCE: Map<String, List<Double>>,
        notasRA: List<Double>
    ) {
        val archivo = File(ruta)
        val contenidoOriginal = archivo.readLines().toMutableList()

        // Actualizar las notas de los criterios de evaluación
        criterios.forEach { criterio ->
            val lineaCEIndex = encontrarLineaCriterioEvaluacion(contenidoOriginal, criterio)
            if (lineaCEIndex != -1) {
                val partes = contenidoOriginal[lineaCEIndex].split(";").toMutableList()
                actualizarNotas(partes, notasCE[criterio.idCE])
                contenidoOriginal[lineaCEIndex] = partes.joinToString(";")
            }
        }

        // Actualizar las notas de los resultados de aprendizaje
        resultados.forEach { resultado ->
            val lineaRAIndex = encontrarLineaResultadoAprendizaje(contenidoOriginal)
            if (lineaRAIndex != -1) {
                val partes = contenidoOriginal[lineaRAIndex].split(";").toMutableList()
                actualizarNotas(partes, notasRA)
                contenidoOriginal[lineaRAIndex] = partes.joinToString(";")
            }
        }

        // Escribir las líneas actualizadas al archivo
        archivo.writeText(contenidoOriginal.joinToString("\n"))
    }

    /**
     * Encuentra la línea correspondiente al criterio de evaluación en el contenido del archivo.
     * Retorna el índice de la línea o -1 si no se encuentra.
     */
    private fun encontrarLineaCriterioEvaluacion(
        contenido: MutableList<String>,
        criterio: CriterioEvaluacion
    ): Int {
        val unidad = obtenerUnidad(contenido)
        return contenido.indexOfFirst { it.startsWith(";UD${unidad.idUnidad}.${criterio.idCE};") }
    }

    /**
     * Encuentra la línea correspondiente al resultado de aprendizaje en el contenido del archivo.
     * Retorna el índice de la línea o -1 si no se encuentra.
     */
    private fun encontrarLineaResultadoAprendizaje(
        contenido: MutableList<String>
    ): Int {
        val unidad = obtenerUnidad(contenido)
        return contenido.indexOfFirst { it.startsWith("UD${unidad.idUnidad}") }
    }

    /**
     * Actualiza las notas en las partes de la línea según las notas proporcionadas.
     */
    private fun actualizarNotas(partes: MutableList<String>, notas: List<Double>?) {
        notas?.let {
            for (i in it.indices) {
                if (7 + i < partes.size) {
                    partes[7 + i] = it[i].toString()
                } else {
                    partes.add(it[i].toString())
                }
            }
        }
    }

    /**
     * Obtiene la unidad educativa del contenido del archivo.
     */
    private fun obtenerUnidad(contenido: List<String>): Unidad {
        val condicionUnidad = contenido.firstOrNull { it.startsWith("UD") }
            ?: throw IllegalArgumentException("No se encontró ninguna unidad en el archivo")

        val partes = condicionUnidad.split(";")
        val idUnidad = LimpiarDatos().limpiarIdUnidad(partes[0])
        return Unidad(idUnidad)
    }
}
