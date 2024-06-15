package service

import entity.*

interface IObtenerDatosService {
    fun obtenerUnidad(): Unidad
    fun obtenerAlumnos(): List<Alumno>
    fun obtenerCriteriosEvaluacion(): List<CriterioEvaluacion>
    fun obtenerResultadosAprendizaje(): List<ResultadoAprendizaje>
    fun obtenerInstrumentosEvaluacion(): List<InstrumentoEvaluacion>
    fun obtenerModulo(): Modulo
}