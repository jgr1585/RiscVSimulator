package at.jeb.riscv.components

import at.jeb.riscv.extensions.shl
import at.jeb.riscv.extensions.shr

typealias ALU_FUNCTION = (UInt, UInt) -> ALU.Result

object ALU {

    fun add(operand1: UInt, operand2: UInt): Result {
        val result = operand1 + operand2

        return Result(
            result = result,
            zero = result == 0u,
        )
    }

    fun sub(operand1: UInt, operand2: UInt): Result {
        val result = operand1 - operand2

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun and(operand1: UInt, operand2: UInt): Result {
        val result = operand1 and operand2

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun or(operand1: UInt, operand2: UInt): Result {
        val result = operand1 or operand2

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun xor(operand1: UInt, operand2: UInt): Result {
        val result = operand1 xor operand2

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun slt(operand1: UInt, operand2: UInt): Result {
        val result = if (operand1 < operand2) 1u else 0u

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun sltu(operand1: UInt, operand2: UInt): Result {
        val result = if (operand1 < operand2) 1u else 0u

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun sll(operand1: UInt, operand2: UInt): Result {
        val shiftAmount = operand2 and 0x1Fu // Mask to 5 bits

        val result = operand1 shl shiftAmount
        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun srl(operand1: UInt, operand2: UInt): Result {
        val shiftAmount = operand2 and 0x1Fu // Mask to 5 bits

        val result = operand1 shr shiftAmount
        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun sra(operand1: UInt, operand2: UInt): Result {
        val shiftAmount = operand2 and 0x1Fu // Mask to 5 bits
        val result = operand1 shr shiftAmount

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun mul(operand1: UInt, operand2: UInt): Result {
        val result = operand1 * operand2.toULong()

        return Result(
            result = result.toUInt(),
            zero = result.toUInt() == 0u
        )
    }

    fun mulh(operand1: UInt, operand2: UInt): Result {
        // Perform signed multiplication and extract the high 32 bits
        val result = ((operand1.toInt().toLong() * operand2.toInt().toLong()) shr 32).toUInt()

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun mulhu(operand1: UInt, operand2: UInt): Result {
        val result = ((operand1.toULong() * operand2.toULong()) shr 32).toUInt()

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun mulhsu(operand1: UInt, operand2: UInt): Result {
        // Perform mixed signed and unsigned multiplication and extract the high 32 bits
        val result = ((operand1.toInt().toLong() * operand2.toLong()) shr 32).toUInt()

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun div(operand1: UInt, operand2: UInt): Result {
        val a = operand1.toInt()
        val b = operand2.toInt()

        val result = when {
            b == 0 -> -1
            a == Int.MIN_VALUE && b == -1 -> Int.MIN_VALUE
            else -> a / b
        }.toUInt()

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun divu(operand1: UInt, operand2: UInt): Result {
        val result = when {
            operand2 == 0u -> UInt.MAX_VALUE
            else -> (operand1 / operand2)
        }

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun rem(operand1: UInt, operand2: UInt): Result {
        val a = operand1.toInt()
        val b = operand2.toInt()

        val result = when {
            b == 0 -> a
            a == Int.MIN_VALUE && b == -1 -> 0
            else -> a % b
        }.toUInt()

        return Result(
            result = result,
            zero = result == 0u
        )
    }

    fun remu(operand1: UInt, operand2: UInt): Result {

        val result = when {
            operand2 == 0u -> operand1
            else -> (operand1 % operand2)
        }

        return Result(
            result = result,
            zero = result == 0u
        )
    }


    @Suppress("unused")
    fun nop(operand1: UInt, operand2: UInt): Result {
        return Result(
            result = 0u,
            zero = true
        )
    }

    data class Result(
        val result: UInt,
        val zero: Boolean
    )
}