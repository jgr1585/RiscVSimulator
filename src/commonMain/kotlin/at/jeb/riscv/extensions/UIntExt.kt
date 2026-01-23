package at.jeb.riscv.extensions

infix fun UInt.shr(bitCount: UInt): UInt {
    if (bitCount >= 32u) { // UInt has 32 bits
        return 0u
    }

    return (this.toLong() shr bitCount.toInt()).toUInt()
}

infix fun UInt.shl(bitCount: UInt): UInt {
    if (bitCount >= 32u) {  // UInt has 32 bits
        return 0u
    }

    return (this.toLong() shl bitCount.toInt()).toUInt()
}

infix operator fun UInt.plus(other: Int): UInt {
    return this + other.toUInt()
}

infix operator fun UInt.compareTo(other: Int): Int {
    return this.toLong().compareTo(other.toLong())
}

operator fun UIntArray.set(index: UInt, value: UInt) {
    if (index >= Int.MAX_VALUE) {
        throw IndexOutOfBoundsException("Index too large: $index")
    }

    this[index.toInt()] = value
}

operator fun UIntArray.get(index: UInt): UInt {
    if (index >= Int.MAX_VALUE) {
        throw IndexOutOfBoundsException("Index too large: $index")
    }

    return this[index.toInt()]
}

operator fun MutableMap<UInt, UInt>.set(index: UInt, value: UByte) {
    this[index] = value.toUInt()
}

operator fun MutableList<UInt>.get(index: UInt): UInt {
    if (index >= Int.MAX_VALUE) {
        throw IndexOutOfBoundsException("Index too large: $index")
    }

    return this[index.toInt()]
}