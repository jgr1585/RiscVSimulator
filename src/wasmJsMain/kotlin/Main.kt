import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.components.Register
import at.jeb.riscv.extensions.readIntelHexFromString
import kotlinx.browser.document
import kotlin.js.Promise
import kotlin.time.Duration
import kotlin.time.measureTime

@ExperimentalWasmJsInterop
external fun fetchProgram(url: String): Promise<JsString>


@ExperimentalWasmJsInterop
fun main() {
    fetchProgram("/riscvsimtest.hex").then { program ->
        val programData = readIntelHexFromString(program.toString())
        val cpu = RISCVCpu()

        cpu.loadProgram(programData)

        try {
            val time = measureTime {
                repeat (1000) {
                    // Limit to 1000 instructions or until halted
                    if(cpu.step()) return@repeat
                }
            }
            printToBody(cpu, time)
        } catch (e: Exception) {
            println("CPU halted with exception: ${e.message}")
        }

        cpu.printCurrentState()

        program // return program for chaining
    }
}

fun printToBody(cpu: RISCVCpu, time: Duration) {
    val body = document.body!!
    val pre = document.createElement("pre")

    pre.textContent = buildString {
        appendLine("Final CPU State:")
        appendLine("PC: ${cpu.pc}")
        appendLine("Instruction Count: ${cpu.instrCount}")
        appendLine("Registers:")
        Register.RegisterName.entries.forEach { regName ->
            appendLine("${regName.name}: ${cpu.register.read(regName.ordinal.toUInt())}")
        }
        appendLine("Memory Dump:")
        //appendLine(cpu.memory.getMemoryDump())
        appendLine("Executed Instructions:")
        cpu.executedInstructionHistory.forEachIndexed { index, string -> appendLine("${index + 1}: $string") }

        appendLine("Execution Time: ${time}")
    }
    body.appendChild(pre)
}

