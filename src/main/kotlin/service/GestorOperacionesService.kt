package service

import gestordatos.GestorOperaciones

class GestorOperacionesService(private val gestorOperaciones: GestorOperaciones) : IGestorOperacionesService {
    /** Control de errores para la función calcularNotasCriteriosEvaluacion */
    override fun calcularNotasCriteriosEvaluacion(): Map<String, List<Double>> {
        return try {
            gestorOperaciones.calcularNotasCriteriosEvaluacion()
        } catch (e: Exception) {
            throw IllegalStateException("Error al calcular notas de criterios de evaluación: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotasResultadosAprendizaje */
    override fun calcularNotasResultadosAprendizaje(): List<Double> {
        return try {
            gestorOperaciones.calcularNotasResultadosAprendizaje()
        } catch (e: Exception) {
            throw IllegalStateException("Error al calcular notas de resultados de aprendizaje: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun calcularNotaModulo(moduloId: String): List<Double> {
        return try {
            gestorOperaciones.calcularNotaModulo(moduloId)
        } catch (e: Exception) {
            throw IllegalStateException("Error al calcular nota de módulo: ${e.message}", e)
        }
    }
}