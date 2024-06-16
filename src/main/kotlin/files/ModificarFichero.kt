package files

import entity.CriterioEvaluacion
import entity.ResultadoAprendizaje
import entity.Unidad
import gestordatos.LimpiarDatos
import java.io.File

class ModificarFichero {

    fun actualizarFichero(
        ruta: String,
        criterios: List<CriterioEvaluacion>,
        resultados: List<ResultadoAprendizaje>,
        notasCE: Map<String, List<Double>>,
        notasRA: List<Double>
    ) {
        val archivo = File(ruta)
        val contenidoOriginal = archivo.readLines().toMutableList()

        /** Actualizar las notas de los criterios de evaluación */
        criterios.forEach { criterio ->
            val lineaCEIndex = encontrarLineaCriterioEvaluacion(contenidoOriginal, criterio)
            if (lineaCEIndex != -1) {
                val partes = contenidoOriginal[lineaCEIndex].split(";").toMutableList()
                actualizarNotas(partes, notasCE[criterio.idCE])
                contenidoOriginal[lineaCEIndex] = partes.joinToString(";")
            }
        }

        /** Actualizar las notas de los resultados de aprendizaje */
        resultados.forEach { resultado ->
            val lineaRAIndex = encontrarLineaResultadoAprendizaje(contenidoOriginal)
            if (lineaRAIndex != -1) {
                val partes = contenidoOriginal[lineaRAIndex].split(";").toMutableList()
                actualizarNotas(partes, notasRA)
                contenidoOriginal[lineaRAIndex] = partes.joinToString(";")
            }
        }

        /** Escribir las líneas actualizadas al archivo */
        archivo.writeText(contenidoOriginal.joinToString("\n"))
    }

    /**
     * Encuentra la línea correspondiente al criterio de evaluación en el contenido del archivo.
     */
    private fun encontrarLineaCriterioEvaluacion(
        contenido: MutableList<String>,
        criterio: CriterioEvaluacion
    ): Int {
        val unidad = obtenerUnidadArchivo(contenido)
        return contenido.indexOfFirst { it.startsWith(";UD${unidad.idUnidad}.${criterio.idCE};") }
    }

    /**
     * Encuentra la línea correspondiente al resultado de aprendizaje en el contenido del archivo.
     */
    private fun encontrarLineaResultadoAprendizaje(
        contenido: MutableList<String>
    ): Int {
        val unidad = obtenerUnidadArchivo(contenido)
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
    private fun obtenerUnidadArchivo(contenido: List<String>): Unidad {
        val condicionUnidad = contenido.firstOrNull { it.startsWith("UD") }
            ?: throw IllegalArgumentException("No se encontró ninguna unidad en el archivo")

        val partes = condicionUnidad.split(";")
        val idUnidad = LimpiarDatos().limpiarIdUnidad(partes[0])
        return Unidad(idUnidad)
    }
}