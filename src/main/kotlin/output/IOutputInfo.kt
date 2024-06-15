package output

import entity.Alumno
import entity.CriterioEvaluacion
import entity.ResultadoAprendizaje

interface IOutputInfo {
    fun showMessage(message: String)
    fun imprimirTabla(alumnos: List<Alumno>, criterios: List<CriterioEvaluacion>, resultados: List<ResultadoAprendizaje>)
}