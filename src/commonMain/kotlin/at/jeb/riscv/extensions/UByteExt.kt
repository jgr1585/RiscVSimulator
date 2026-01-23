package at.jeb.riscv.extensions

infix fun UByte.shl(bitCount: Int): UByte = (this.toInt() shl bitCount).toUByte()

infix operator fun UByte.compareTo(other: Int): Int {
    return this.toInt().compareTo(other)
}

operator fun UByteArray.set(index: UInt, value: UByte) {
    if (index >= Int.MAX_VALUE) {
        throw IndexOutOfBoundsException("Index too large: $index")
    }

    this[index.toInt()] = value
}

operator fun UByteArray.get(index: UInt): UByte {
    if (index >= Int.MAX_VALUE) {
        throw IndexOutOfBoundsException("Index too large: $index")
    }

    return this[index.toInt()]
}

infix fun UByte.compareTo(other: UInt): Int {
    return this.toUInt().compareTo(other)
}