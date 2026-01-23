package at.jeb.riscv.extensions

infix fun UShort.shr(bitCount: Int): UShort = (this.toInt() shr bitCount).toUShort()