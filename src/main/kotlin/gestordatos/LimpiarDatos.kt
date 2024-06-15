package gestordatos

class LimpiarDatos : ILimpiarDatos {
    /**
     * Convierte un porcentaje en formato cadena a un valor Double.
     */
    override fun limpiarPorcentaje(valor: String): Double {
        return valor.replace("%", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    }
    /**
     * Extrae el identificador de unidad de una cadena.
     */
    override fun limpiarIdUnidad(valor: String): Int {
        return (valor.replace("UD", "").toIntOrNull() ?: 0).toInt()
    }
    /**
     * Extrae el identificador de resultado de aprendizaje de una cadena.
     */
    override fun limpiarIdRA(valor: String): Int {
        return (valor.replace("RA", "").toIntOrNull() ?: 0).toInt()
    }
    /**
     * Limpia y obtiene el identificador de criterio de evaluación.
     */
    override fun limpiarIdCE(valor: String, obtenerDatos: IObtenerDatos): String {
        val unidad = obtenerDatos.obtenerUnidad()
        return valor.replace("UD${unidad.idUnidad}.", "")
    }
}