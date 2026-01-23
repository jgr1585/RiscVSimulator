package at.jeb.riscv.extensions

import kotlinx.io.Source

fun Source.readHexByte(): UByte {
    return (readByte().toInt().toChar() + readByte().toInt().toChar())
        .toUByte(16)
}

fun Source.readHexBytes(count: UInt): UByteArray {
    return (0u until count).map {
        readHexByte()
    }.toUByteArray()
}

fun Source.readHexBytes(count: Int): UByteArray {
    if (count < 0) {
        throw IllegalArgumentException("Count must be non-negative: $count")
    }
    return readHexBytes(count.toUInt())
}

fun Source.readHexBytes(count: UByte): UByteArray {
    return readHexBytes(count.toUInt())
}