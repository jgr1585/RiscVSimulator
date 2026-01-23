package at.jeb.riscv.components

import at.jeb.riscv.extensions.compareTo
import at.jeb.riscv.extensions.get
import at.jeb.riscv.extensions.set

class Register {
    private val registers = UIntArray(32) { 0u }

    fun read(registerIndex: UInt): UInt {
        if (registerIndex >= registers.size) {
            throw IndexOutOfBoundsException("Register index out of bounds: $registerIndex")
        }

        return registers[registerIndex]
    }

    fun write(registerIndex: UInt, value: UInt) {
        if (registerIndex == 0u) {
            // Register x0 is hardwired to zero
            return
        } else if (registerIndex >= registers.size) {
            throw IndexOutOfBoundsException("Register index out of bounds: $registerIndex")
        }
        registers[registerIndex] = value
    }

    fun write(registerName: RegisterName, value: Int) {
        this.write(registerName.ordinal.toUInt(), value.toUInt())
    }

    fun reset() {
        for (i in registers.indices) {
            registers[i] = 0u
        }
    }

    fun printRegisters() {
        RegisterName.entries.forEach { entry ->
            val index = entry.ordinal

            if (entry.ordinal >= registers.size) {
                throw IndexOutOfBoundsException("Register index out of bounds: $entry")
            }

            val value = registers[index]

            println("${entry.name} (x$entry): 0x${value.toString(16).padStart(8, '0')} (${value})")
        }
    }


    enum class RegisterName {
        ZERO, RA, SP, GP, TP,
        T0, T1, T2,
        S0, S1,
        A0, A1, A2, A3, A4, A5, A6, A7,
        S2, S3, S4, S5, S6, S7, S8, S9, S10, S11,
        T3, T4, T5, T6
    }
}