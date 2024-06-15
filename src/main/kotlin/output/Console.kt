package output

import entity.*
import gestordatos.GestorOperaciones

class Console(private val lineas: List<String>, private val ruta: String) : IOutputInfo {

    /** Imprime un mensaje */
    override fun showMessage(message: String) {
        println(message)
    }

    /** Imprime la tabla en consola de los alumnos con sus calificaciones */
    override fun imprimirTabla(alumnos: List<Alumno>, criterios: List<CriterioEvaluacion>, resultados: List<ResultadoAprendizaje>) {
        val gestorOperaciones = GestorOperaciones(lineas, ruta)
        val modulo = gestorOperaciones.obtenerDatos.obtenerModulo()
        val notasModulo = gestorOperaciones.calcularNotaModulo(modulo.idModulo)

        /** Imprimir encabezado para los alumnos */
        println(String.format("%-30s %-30s", "Inicial alumno", "Nombre Alumno"))

        /** Iterar sobre cada alumno */
        alumnos.forEachIndexed { index, alumno ->
            println(String.format("%-30s %-30s", alumno.iniciales, alumno.nombre))

            /** Imprimir nota del módulo para el alumno actual */
            val notaModulo = notasModulo.getOrNull(index) ?: 0.0
            println(String.format("%-30s %-30s %-30.2f", modulo.idModulo, "Nota del módulo", notaModulo))

            println()

            /** Imprimir resultados de aprendizaje para el alumno actual */
            resultados.forEach { resultado ->
                val notasRA = gestorOperaciones.calcularNotasResultadosAprendizaje()
                val notaRA = notasRA.getOrNull(index) ?: 0.0
                println(String.format("%-30s %-20s %-30s %-20.2f", "RA${resultado.idRA}", "${resultado.porcentajeRA}%", "nota", notaRA))
            }

            println()

            /** Imprimir criterios de evaluación para el alumno actual */
            criterios.forEach { criterio ->
                val notasCriterio = gestorOperaciones.calcularNotasCriteriosEvaluacion()[criterio.idCE] ?: List(alumnos.size) { 0.0 }
                val notaCriterio = notasCriterio[index]
                println(String.format("%-30s %-20s %-30s %-20.2f", "CE${criterio.idCE}", "${criterio.porcentajeCE}%", "nota", notaCriterio))
            }

            println()
        }
    }
}