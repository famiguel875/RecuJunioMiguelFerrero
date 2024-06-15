package gestordatos

interface ILimpiarDatos {
    fun limpiarPorcentaje(valor: String): Double
    fun limpiarIdUnidad(valor: String): Int
    fun limpiarIdRA(valor: String): Int
    fun limpiarIdCE(valor: String, obtenerDatos: IObtenerDatos): String
}