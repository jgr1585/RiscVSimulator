import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.components.Register
import at.jeb.riscv.extensions.readIntelHexFromString
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLElement
import org.w3c.fetch.Request
import kotlin.time.Duration
import kotlin.time.measureTime

@OptIn(DelicateCoroutinesApi::class)
fun main() {

    window.fetch(Request("/riscvsimtest.hex")).then(onFulfilled = {
        it.text().then(onFulfilled = { program ->
            val programData = readIntelHexFromString(program)
            val cpu = RISCVCpu()

            cpu.loadProgram(programData)

            GlobalScope.launch {
                try {
                    val time = measureTime {
                        repeat (1000) {
                            // Limit to 1000 instructions or until halted
                            if(cpu.step()) return@repeat
                        }
                    }
                    printToBody(cpu, time)
                } catch (e: Exception) {
                    console.log("CPU halted with exception: ${e.message}")
                }
            }
        })
    })
}

fun printToBody(cpu: RISCVCpu, time: Duration) {
    val body = document.body as HTMLBodyElement
    val pre = document.createElement("pre") as HTMLElement

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

        appendLine("Execution Time: $time")
    }

    body.clear()
    body.appendChild(pre)
}