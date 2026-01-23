package at.jeb.riscv.extensions

infix operator fun Char.plus(other: Char): String {
    return this.toString() + other.toString()
}