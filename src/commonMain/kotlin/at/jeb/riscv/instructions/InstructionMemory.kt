package at.jeb.riscv.instructions

import at.jeb.riscv.extensions.compareTo

class InstructionMemory(size: Int = 1024) {
    private val memory = UIntArray(size)

    fun load(program: UIntArray) {
        if (program.size > memory.size) {
            throw IndexOutOfBoundsException("Program size exceeds memory bounds")
        }

        program.forEachIndexed { index, i -> memory[index] = i }

    }

    fun fetch(address: UInt): UInt {
        if (address >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        return memory[address.toInt() / 4]
    }

}