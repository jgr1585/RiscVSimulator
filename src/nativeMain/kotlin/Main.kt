import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.extensions.readHexFromIntelFile
import kotlinx.io.files.Path


fun main() {
    val cpu = RISCVCpu()

    val programFile = "./resources/square.hex"
    val programPath = Path(programFile)
    val program = readHexFromIntelFile(programPath)

    cpu.loadProgram(program)

    try {
        repeat (1000) {
            cpu.step()
        }
    } catch (e: Exception) {
        println("CPU halted with exception: ${e.message}")
    }

    cpu.printCurrentState()

    println("Executed Instructions:")
    cpu.executedInstructionHistory.forEach(::println)
}