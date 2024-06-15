package gestordatos

import entity.*

class ObtenerDatos(private val lineas: List<String>, private val ruta: String): IObtenerDatos {
    /**
     * Obtiene la unidad educativa del archivo.
     */
    override fun obtenerUnidad(): Unidad {
        val condicionUnidad = lineas.filter { it.startsWith("UD") }
        if (condicionUnidad.isNotEmpty()) {
            val partes = condicionUnidad[0].split(";")
            val idUnidad = LimpiarDatos().limpiarIdUnidad(partes[0])
            return Unidad(idUnidad)
        }
        throw IllegalArgumentException("No se encontró ninguna unidad en el archivo")
    }
    /**
     * Obtiene la lista de alumnos del archivo.
     */
    override fun obtenerAlumnos(): List<Alumno> {
        val alumnos = mutableListOf<Alumno>()
        val alumnosIniciales = lineas[0].split(";").drop(7)
        val nombresAlumnos = lineas[1].split(";").drop(7)

        for (i in alumnosIniciales.indices) {
            val inicial = alumnosIniciales[i]
            val nombre = nombresAlumnos.getOrNull(i)
            if (nombre != null) {
                alumnos.add(Alumno(inicial, nombre))
            }
        }
        return alumnos
    }
    /**
     * Obtiene los criterios de evaluación del archivo.
     */
    override fun obtenerCriteriosEvaluacion(): List<CriterioEvaluacion> {
        val unidad = obtenerUnidad()
        val condicionCE = lineas.filter { it.startsWith(";UD${unidad.idUnidad}.") }
        val criterios = mutableListOf<CriterioEvaluacion>()

        var i = 0
        while (i < condicionCE.size) {
            val partes = condicionCE[i].split(";")
            val idCE = LimpiarDatos().limpiarIdCE(partes[1], this)
            val descripcionCE = partes[3]
            val porcentajeCE = LimpiarDatos().limpiarPorcentaje(partes[5])
            criterios.add(CriterioEvaluacion(idCE, descripcionCE, porcentajeCE))
            i++
        }
        return criterios
    }
    /**
     * Obtiene los resultados de aprendizaje del archivo.
     */
    override fun obtenerResultadosAprendizaje(): List<ResultadoAprendizaje> {
        val unidad = obtenerUnidad()
        val condicionRA = lineas.filter { it.startsWith("UD${unidad.idUnidad}") }
        val resultados = mutableListOf<ResultadoAprendizaje>()

        if (condicionRA.isNotEmpty()) {
            val partes = lineas[2].split(";")
            val idRA = LimpiarDatos().limpiarIdRA(partes[1])
            val descripcionRA = partes[3]
            val porcentajeRA = LimpiarDatos().limpiarPorcentaje(partes[6])
            resultados.add(ResultadoAprendizaje(idRA, descripcionRA, porcentajeRA))
        }
        return resultados
    }
    /**
     * Obtiene los instrumentos de evaluación del archivo.
     */
    override fun obtenerInstrumentosEvaluacion(): List<InstrumentoEvaluacion> {
        val criteriosEvaluacion = obtenerCriteriosEvaluacion()
        val instrumentos = mutableListOf<InstrumentoEvaluacion>()

        for (criterio in criteriosEvaluacion) {
            val condicionInstrumento = lineas.filter { it.startsWith(";${criterio.idCE}") }

            for (i in condicionInstrumento.indices) {
                val partes = condicionInstrumento[i].split(";")
                val criteriosInstrumento = partes[1]
                val descripcionInstrumento = partes[3]
                val porcentajeInstrumento = LimpiarDatos().limpiarPorcentaje(partes[6])
                val notasInstrumento = partes.drop(7).map { it.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                instrumentos.add(InstrumentoEvaluacion(criteriosInstrumento, descripcionInstrumento, porcentajeInstrumento, notasInstrumento))
            }
        }
        return instrumentos
    }
    /**
     * Obtiene el módulo educativo del archivo.
     */
    override fun obtenerModulo(): Modulo {
        val partesRuta = ruta.split("/")
        val nombreArchivo = partesRuta.last()
        val partesNombre = nombreArchivo.split(" ")
        val idModulo = partesNombre[1]
        return Modulo(idModulo)
    }
}