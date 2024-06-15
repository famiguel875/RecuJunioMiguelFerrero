package gestordatos

interface IGestorOperaciones {
    fun calcularNotasCriteriosEvaluacion(): Map<String, List<Double>>
    fun calcularNotasResultadosAprendizaje(): List<Double>
    fun calcularNotaModulo(moduloId: String): List<Double>
}