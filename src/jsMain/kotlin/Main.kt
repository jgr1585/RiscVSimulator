import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.components.Register
import at.jeb.riscv.extensions.readIntelHexFromString
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLElement
import org.w3c.fetch.Request

@OptIn(DelicateCoroutinesApi::class)
fun main() {

    window.fetch(Request("/riscvsimtest.hex")).then(onFulfilled = {
        it.text().then(onFulfilled = { program ->
            val programData = readIntelHexFromString(program)
            val cpu = RISCVCpu()

            cpu.loadProgram(programData)

            GlobalScope.launch {
                try {
                    while (true) {
                        cpu.step()
                        printToBody(cpu)
                        delay(1000L)
                    }
                } catch (e: Exception) {
                    console.log("CPU halted with exception: ${e.message}")
                }
            }
        })
    })
}

fun printToBody(cpu: RISCVCpu) {
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
        cpu.executedInstructionHistory.forEach { appendLine(it) }
    }

    body.clear()
    body.appendChild(pre)
}