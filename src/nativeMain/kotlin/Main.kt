import at.jeb.riscv.RISCVCpu
import at.jeb.riscv.extensions.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.EOFException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem


@OptIn(ExperimentalForeignApi::class)
fun main() {
    val cpu = RISCVCpu()

    val programFile = "/home/julian/Downloads/riscvsimtesthex.sec"
    val programPath = Path(programFile)
    val memoryBuffer = mutableMapOf<UInt, UInt>()

    // Load program from binary file
    SystemFileSystem.source(programPath).use { rawSource ->
        rawSource.buffered().use { buffer ->
            while (true) {
                try {
                    if (buffer.readByte().toInt().toChar() == ':') {
                        var checksum = 0u
                        val byteCount = buffer.readHexByte().also { checksum += it }
                        val address = buffer.readHexBytes(2)
                            .also { bytes -> bytes.forEach { checksum += it } }
                            .fold(0u) { acc, byte ->
                                (acc shl 8) or (byte.toUInt() and 0xFFu)
                            }
                        val recordType = buffer.readHexByte().also { checksum += it }
                        val data = buffer.readHexBytes(byteCount)
                            .also { bytes -> bytes.forEach { checksum += it } }
                        val recordChecksum = buffer.readHexByte()


                        if (((checksum + recordChecksum) and 0xFFu) != 0u) {
                            throw Exception("Checksum mismatch")
                        }

                        when (recordType.toUInt()) {
                            0x00u -> { // Data record
                                data.forEachIndexed { index, byte ->
                                    val addr = address + index
                                    memoryBuffer[addr] = byte
                                }
                            }
                            0x01u -> { // End Of File record
                                break
                            }
                            else -> {
                                // Ignore other record types for now
                            }
                        }
                    }
                } catch (_: EOFException) {
                    break
                }
            }
        }
    }

    // Convert memory buffer to program buffer (assuming word-aligned addresses)
    val program = mutableListOf<UInt>()

    memoryBuffer.forEach { (addr, data) ->
        val wordIndex = addr / 4u // Assuming word-aligned addresses
        while (wordIndex >= program.size) {
            program.add(0u)
        }
        program[wordIndex.toInt()] = program[wordIndex] or (data and 0xFFu shl ((addr % 4u) * 8u))
    }

    cpu.loadProgram(program.toUIntArray())

    try {
        while (true) {
            cpu.step()
        }
    } catch (e: Exception) {
        println("CPU halted with exception: ${e.message}")
    }

    cpu.printCurrentState()

    println("Executed Instructions:")
    cpu.executedInstructionHistory.forEach(::println)
}