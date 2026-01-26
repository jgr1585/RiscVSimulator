package at.jeb.riscv.extensions

import kotlinx.io.EOFException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun readHexFromIntelFile(path: Path): UIntArray {
    val memoryBuffer = mutableMapOf<UInt, UInt>()

    // Load program from binary file
    SystemFileSystem.source(path).use { rawSource ->
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

                        if(!applyRecordToMemory(data, address, recordType, memoryBuffer))
                            break // End Of File record encountered
                    }
                } catch (_: EOFException) {
                    break
                }
            }
        }
    }

    // Convert memory buffer to program buffer (assuming word-aligned addresses)
    return convertMemoryBufferToProgram(memoryBuffer)
}

fun readIntelHexFromString(hexString: String): UIntArray {
    val memoryBuffer = mutableMapOf<UInt, UInt>()
    val lines = hexString.lines()

    for (line in lines) {
        if (line.isNotBlank() && line[0] == ':') {
            var checksum = 0u
            val byteCount = line.substring(1, 3).toUByte(16).also { checksum += it.toUInt() }
            val address = line.substring(3, 7).chunked(2)
                .map { it.toUByte(16).also { byte -> checksum += byte.toUInt() } }
                .fold(0u) { acc, byte ->
                    (acc shl 8) or (byte.toUInt() and 0xFFu)
                }

            val recordType = line.substring(7, 9).toUByte(16).also { checksum += it.toUInt() }
            val data = line.substring(9, 9 + (byteCount.toInt() * 2)).chunked(2)
                .map { it.toUByte(16).also { byte -> checksum += byte.toUInt() } }

            val recordChecksum = line.substring(9 + (byteCount.toInt() * 2), 11 + (byteCount.toInt() * 2)).toUByte(16)

            if (((checksum + recordChecksum.toUInt()) and 0xFFu) != 0u) {
                throw Exception("Checksum mismatch")
            }

            if(!applyRecordToMemory(data.toUByteArray(), address, recordType, memoryBuffer))
                break // End Of File record encountered
        }
    }

    // Convert memory buffer to program buffer (assuming word-aligned addresses)
    return convertMemoryBufferToProgram(memoryBuffer)
}

private fun applyRecordToMemory(data: UByteArray, address: UInt, recordType: UByte, memoryBuffer: MutableMap<UInt, UInt>): Boolean {
    return when (recordType.toUInt()) {
        0x00u -> { // Data record
            data.forEachIndexed { index, byte ->
                val addr = address + index.toUInt()
                memoryBuffer[addr] = byte and 0xFFu
            }
            true
        }
        0x01u -> { // End Of File record
            false
        }
        else -> {
            // Ignore other record types for now
            true
        }
    }
}

private fun convertMemoryBufferToProgram(memoryBuffer: Map<UInt, UInt>): UIntArray {
    val program = mutableListOf<UInt>()

    memoryBuffer.forEach { (addr, data) ->
        val wordIndex = addr / 4u // Assuming word-aligned addresses
        while (wordIndex >= program.size) {
            program.add(0u)
        }
        program[wordIndex.toInt()] = program[wordIndex] or (data and 0xFFu shl ((addr % 4u) * 8u))
    }

    return program.toUIntArray()
}