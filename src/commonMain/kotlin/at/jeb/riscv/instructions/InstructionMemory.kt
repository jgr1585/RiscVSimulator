package at.jeb.riscv.instructions

class InstructionMemory(size: Int = 1024) {
    private val memory = UByteArray(size)

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

    fun fetch(address: UInt): UInt {
        val address = address.toInt()
        if (address + 3 >= memory.size) {
            throw IndexOutOfBoundsException("Address out of bounds: $address")
        }

        return (memory[address].toUInt() or
                (memory[address + 1].toUInt() shl 8) or
                (memory[address + 2].toUInt() shl 16) or
                (memory[address + 3].toUInt() shl 24))
    }

}