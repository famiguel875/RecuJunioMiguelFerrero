package gestordatos

class GestorOperaciones(private val lineas: List<String>, private val ruta: String): IGestorOperaciones {

    val obtenerDatos = ObtenerDatos(lineas, ruta)

    /**
     * Este método obtiene los criterios de evaluación y los instrumentos de evaluación,
     * y luego calcula las notas para cada criterio. Para cada criterio, se suman las notas
     * ponderadas de los instrumentos de evaluación que lo contienen.
     */

    override fun calcularNotasCriteriosEvaluacion(): Map<String, List<Double>> {
        /** Realiza un mapa donde las claves son los identificadores de los criterios de evaluación y los valores son las listas de notas correspondientes para cada alumno.*/

        val criteriosEvaluacion = obtenerDatos.obtenerCriteriosEvaluacion()
        val instrumentosEvaluacion = obtenerDatos.obtenerInstrumentosEvaluacion()
        val notasCriterios = mutableMapOf<String, List<Double>>()

        /** Verificar si hay instrumentos de evaluación disponibles */

        if (instrumentosEvaluacion.isNotEmpty()) {

            /** Número de alumnos según el tamaño de las notas del primer instrumento */

            val numAlumnos = instrumentosEvaluacion.first().notasInstrumento.size

            /** Calcular notas para cada criterio de evaluación */

            for (criterio in criteriosEvaluacion) {
                val notas = MutableList(numAlumnos) { 0.0 }
                for (instrumento in instrumentosEvaluacion) {
                    /** Verificar si el criterio actual está presente en el instrumento */
                    if (instrumento.criteriosInstrumento.contains(criterio.idCE)) {
                        val porcentaje = instrumento.porcentajeInstrumento / 100
                        for (i in notas.indices) {
                            if (i < instrumento.notasInstrumento.size) {
                                /** Sumar nota ponderada del instrumento a la nota del criterio */
                                notas[i] += instrumento.notasInstrumento[i] * porcentaje
                            }
                        }
                    }
                }
                notasCriterios[criterio.idCE] = notas
            }
        }

        return notasCriterios
    }

    /**
     * Este método obtiene los criterios de evaluación, calcula las notas por criterios
     * y luego usa estas notas para calcular las notas de los resultados de aprendizaje.
     * Para cada resultado de aprendizaje, se suman las notas ponderadas de los criterios
     * de evaluación que lo componen.
     */

    override fun calcularNotasResultadosAprendizaje(): List<Double> {
        /** Obtener criterios de evaluación y calcular notas por criterios */
        val criteriosEvaluacion = obtenerDatos.obtenerCriteriosEvaluacion()
        val notasCriterios = calcularNotasCriteriosEvaluacion()
        val resultadosAprendizaje = obtenerDatos.obtenerResultadosAprendizaje()

        /** Número de alumnos según las notas de los criterios de evaluación */
        val numAlumnos = notasCriterios.values.firstOrNull()?.size ?: 0
        val notasRA = MutableList(numAlumnos) { 0.0 }

        /** Calcular notas para cada resultado de aprendizaje */
        for (criterio in criteriosEvaluacion) {
            val notasCriterio = notasCriterios[criterio.idCE] ?: continue
            val porcentajeCE = criterio.porcentajeCE / 100
            for (i in notasRA.indices) {
                /** Sumar nota ponderada del criterio a la nota del resultado de aprendizaje */
                notasRA[i] += notasCriterio[i] * porcentajeCE
            }
        }

        return notasRA
    }

    /**
     * Calcula la nota del módulo considerando todas las notas de resultados de aprendizaje ajustadas por sus porcentajes.
     */

    override fun calcularNotaModulo(moduloId: String): List<Double> {
        /** Obtener las notas RA para cada archivo y los porcentajes ajustados */
        val todasNotasRA = mutableListOf<Pair<Double, List<Double>>>()
        var totalPorcentajeRA = 0.0

        FicheroRuta.rutasArchivos.filter { it.contains(moduloId) }.forEach { ruta ->
            val lineasArchivo = FicheroRuta.leerFicheroTexto(ruta)
            val obtenerDatosArchivo = ObtenerDatos(lineasArchivo, ruta)
            val resultadosRA = obtenerDatosArchivo.obtenerResultadosAprendizaje()
            val notasRA = GestorOperaciones(lineasArchivo, ruta).calcularNotasResultadosAprendizaje()

            /** Ajustar los porcentajes de RA */
            resultadosRA.forEach { resultado ->
                todasNotasRA.add(Pair(resultado.porcentajeRA, notasRA))
                totalPorcentajeRA += resultado.porcentajeRA
            }
        }

        /** Validar que la suma de los porcentajes no sea cero */
        if (totalPorcentajeRA == 0.0) {
            throw IllegalArgumentException("El total de los porcentajes de los RA no puede ser cero")
        }

        /** Ajustar las notas RA al porcentaje total */
        val notasModulo = MutableList(todasNotasRA.first().second.size) { 0.0 }
        todasNotasRA.forEach { (porcentajeRA, notasRA) ->
            val porcentajeAjustado = porcentajeRA / totalPorcentajeRA
            notasRA.forEachIndexed { index, notaRA ->
                notasModulo[index] += notaRA * porcentajeAjustado
            }
        }

        return notasModulo
    }
}