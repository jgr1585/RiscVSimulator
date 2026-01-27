import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.extensions.readHexFromIntelFile
import kotlinx.io.files.Path
import kotlin.time.measureTime

fun main() {
    val cpu = RISCVCpu()

    val programFile = "./resources/riscvsimtest.hex"
    val programPath = Path(programFile)
    val program = readHexFromIntelFile(programPath)

    cpu.loadProgram(program)

    val time = try {
        measureTime {
            repeat (1000) {
                // Limit to 1000 instructions or until halted
                if(!cpu.step()) return@repeat
            }
        }
    } catch (e: Exception) {
        println("CPU halted with exception: ${e.message}")
    }

    cpu.printCurrentState()

    println("Executed Instructions:")
    cpu.executedInstructionHistory.forEach(::println)

    println("Execution Time: $time")
}