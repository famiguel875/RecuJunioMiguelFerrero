package procesadorcomandos

import entity.CriterioEvaluacion
import entity.ResultadoAprendizaje
import gestordatos.*
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
                val obtenerDatosService = ObtenerDatosService(ObtenerDatos(lineas, ruta))
                val gestorOperacionesService = GestorOperacionesService(GestorOperaciones(lineas, ruta))

                val alumnos = obtenerDatosService.obtenerAlumnos()
                val criterios = obtenerDatosService.obtenerCriteriosEvaluacion()
                val resultados = obtenerDatosService.obtenerResultadosAprendizaje()

                Console(lineas, ruta).imprimirTabla(alumnos, criterios, resultados)
                actualizarFichero(ruta, criterios, resultados, gestorOperacionesService.calcularNotasCriteriosEvaluacion(), gestorOperacionesService.calcularNotasResultadosAprendizaje())
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

        /** Actualizar las notas de los criterios de evaluación en la misma línea donde se referencian */
        criterios.forEach { criterio ->
            val notas = notasCE[criterio.idCE]
            if (notas != null) {
                val lineaCE = contenidoOriginal.indexOfFirst { it.startsWith(";${criterio.idCE}") }
                if (lineaCE != -1) {
                    // Actualizar la línea existente
                    val partes = contenidoOriginal[lineaCE].split(";").toMutableList()
                    for (i in 7 until partes.size) {
                        if (i - 7 < notas.size) {
                            partes[i] = notas[i - 7].toString()
                        }
                    }
                    contenidoOriginal[lineaCE] = partes.joinToString(";")
                }
            }
        }

        /** Actualizar las notas de los resultados de aprendizaje en la misma línea donde se referencian */
        resultados.forEach { resultado ->
            val lineaRA = contenidoOriginal.indexOfFirst { it.startsWith("UD${resultado.idRA}") }
            if (lineaRA != -1) {
                /** Actualizar la línea existente */
                val partes = contenidoOriginal[lineaRA].split(";").toMutableList()
                for (i in 7 until partes.size) {
                    if (i - 7 < notasRA.size) {
                        partes[i] = notasRA[i - 7].toString()
                    }
                }
                contenidoOriginal[lineaRA] = partes.joinToString(";")
            }
        }

        /** Escribir las notas actualizadas al archivo */
        archivo.writeText(contenidoOriginal.joinToString("\n"))
    }
}