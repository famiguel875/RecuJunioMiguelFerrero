package service

import entity.*
import gestordatos.ObtenerDatos

class ObtenerDatosService(private val obtenerDatos: ObtenerDatos) : IObtenerDatosService {
    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerUnidad(): Unidad {
        return try {
            obtenerDatos.obtenerUnidad()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener unidad: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerAlumnos(): List<Alumno> {
        return try {
            obtenerDatos.obtenerAlumnos()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener alumnos: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerCriteriosEvaluacion(): List<CriterioEvaluacion> {
        return try {
            obtenerDatos.obtenerCriteriosEvaluacion()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener criterios de evaluación: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerResultadosAprendizaje(): List<ResultadoAprendizaje> {
        return try {
            obtenerDatos.obtenerResultadosAprendizaje()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener resultados de aprendizaje: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerInstrumentosEvaluacion(): List<InstrumentoEvaluacion> {
        return try {
            obtenerDatos.obtenerInstrumentosEvaluacion()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener instrumentos de evaluación: ${e.message}", e)
        }
    }

    /** Control de errores para la función calcularNotaModulo */
    override fun obtenerModulo(): Modulo {
        return try {
            obtenerDatos.obtenerModulo()
        } catch (e: Exception) {
            throw IllegalStateException("Error al obtener módulo: ${e.message}", e)
        }
    }
}