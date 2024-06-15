import procesadorcomandos.ProcesadorComandos

fun main(args: Array<String>) {
    val procesadorComandos = ProcesadorComandos(args)
    procesadorComandos.procesar()
}