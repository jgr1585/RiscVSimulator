package at.jeb.riscv.components

import at.jeb.riscv.extensions.*
import kotlin.math.min

class Memory(val size: Int = 1024) {
    private val memory = UByteArray(size)

    fun readByte(address: UInt): UByte {
        if (address >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        return memory[address]
    }

    fun readHalfWord(address: UInt): UShort {
        if (address + 1 >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        return (memory[address].toUShort() or (memory[address + 1] shl 8).toUShort())
    }

    fun readWord(address: UInt): UInt {
        if (address + 3 >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        return (memory[address].toUInt() or
                (memory[address + 1].toUInt() shl 8) or
                (memory[address + 2].toUInt() shl 16) or
                (memory[address + 3].toUInt() shl 24))
    }

    fun writeByte(address: UInt, value: UByte) {
        if (address >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        memory[address] = value
    }

    fun writeHalfWord(address: UInt, value: UShort) {
        if (address + 1 >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        memory[address] = (value and 0xFFu).toUByte()
        memory[address + 1] = ((value shr 8) and 0xFFu).toUByte()
    }

    fun writeWord(address: UInt, value: UInt) {
        if (address + 3 >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        memory[address] = (value and 0xFFu).toUByte()
        memory[address + 1] = ((value shr 8) and 0xFFu).toUByte()
        memory[address + 2] = ((value shr 16) and 0xFFu).toUByte()
        memory[address + 3] = ((value shr 24) and 0xFFu).toUByte()
    }

    fun load(program: UIntArray) {
        if (program.size > memory.size) {
            throw IndexOutOfBoundsException("Program size exceeds memory bounds")
        }

        program.forEachIndexed { index, i ->
            memory[index * 4] = (i and 0xFFu).toUByte()
            memory[index * 4 + 1] = ((i shr 8) and 0xFFu).toUByte()
            memory[index * 4 + 2] = ((i shr 16) and 0xFFu).toUByte()
            memory[index * 4 + 3] = ((i shr 24) and 0xFFu).toUByte()
        }
    }

    fun printMemoryDump(startAddress: Int = 0, endAddress: Int = memory.size - 1) {
        var isLastLineEmpty = false
        for (address in startAddress..endAddress step 16) {
            val isLineEmpty = memory.slice(address until min(address + 16, memory.size)).all { it == 0.toUByte() }

            isLastLineEmpty = if (isLineEmpty && address + 16 <= endAddress) {
                if (isLastLineEmpty) {
                    continue
                } else {
                    true
                }
            } else {
                if (isLastLineEmpty) {
                    println("...")
                }
                false
            }

            print("${address.toString(16).padStart(8, '0')}: ")
            for (offset in 0 until 16) {
                val currentAddress = address + offset
                if (currentAddress <= endAddress) {
                    print("${memory[currentAddress].toString(16).padStart(2, '0')} ")
                } else {
                    print("   ")
                }
            }
            print(" | ")
            for (offset in 0 until 16) {
                val currentAddress = address + offset
                if (currentAddress <= endAddress) {
                    val byte = memory[currentAddress]
                    val char = if (byte in 32u..126u) byte.toInt().toChar() else '.'
                    print(char)
                }
            }
            println()
        }
    }
}