package at.jeb.riscv.instructions

interface InstructionData {
    val opcode: UInt
    val rd: UInt
    val funct3: UInt
    val rs1: UInt
    val rs2: UInt
    val funct7: UInt
    val imm: Int

}

data class RTypeInstruction(val instruction: UInt) : InstructionData {
    override val opcode  = instruction          and 0x7Fu // bits 0-6
    override val rd      = (instruction shr 7)  and 0x1Fu // bits 7-11
    override val funct3  = (instruction shr 12) and 0x7u  // bits 12-14
    override val rs1     = (instruction shr 15) and 0x1Fu // bits 15-19
    override val rs2     = (instruction shr 20) and 0x1Fu // bits 20-24
    override val funct7  = (instruction shr 25) and 0x7Fu // bits 25-31

    // R-Type instructions do not have an immediate value
    override val imm get() = throw UnsupportedOperationException("R-Type instructions do not have an immediate value.")
}

data class ITypeInstruction(val instruction: UInt): InstructionData {
    override val opcode  = instruction          and 0x7Fu  // bits 0-6
    override val rd      = (instruction shr 7)  and 0x1Fu  // bits 7-11
    override val funct3  = (instruction shr 12) and 0x7u   // bits 12-14
    override val rs1     = (instruction shr 15) and 0x1Fu  // bits 15-19
    override val imm     = signImm((instruction shr 20) and 0xFFFu, 0x800u) // bits 20-31 with sign extension

    // I-Type instructions do not have rs2 or funct7 fields
    override val rs2    get() = throw UnsupportedOperationException("I-Type instructions do not have rs2 field.")
    override val funct7 get() = throw UnsupportedOperationException("I-Type instructions do not have funct7 field.")
}

data class STypeInstruction(val instruction: UInt) : InstructionData {
    override val opcode  = instruction          and 0x7Fu  // bits 0-6
    override val funct3  = (instruction shr 12) and 0x7u   // bits 12-14
    override val rs1     = (instruction shr 15) and 0x1Fu  // bits 15-19
    override val rs2     = (instruction shr 20) and 0x1Fu  // bits 20-24

    private val imm1 = (instruction shr 7)  and 0x1Fu  // bits 7-11
    private val imm2 = (instruction shr 25) and 0x7Fu  // bits 25-31

    override val imm = signImm((imm2 shl 5) or imm1, 0x800u)

    // S-Type instructions do not have rd or funct7 fields
    override val rd     get() = throw UnsupportedOperationException("S-Type instructions do not have rd field.")
    override val funct7 get() = throw UnsupportedOperationException("S-Type instructions do not have funct7 field.")
}

data class UTypeInstruction(val instruction: UInt) : InstructionData {
    override val opcode  = instruction          and 0x7Fu  // bits 0-6
    override val rd      = (instruction shr 7)  and 0x1Fu  // bits 7-11
    override val imm     = signImm(instruction and 0xFFFFF000u, 0x80000000u) // bits 12-31 with sign extension

    // U-Type instructions do not have funct3, rs1, rs2, or funct7 fields
    override val funct3 get() = throw UnsupportedOperationException("U-Type instructions do not have funct3 field.")
    override val rs1    get() = throw UnsupportedOperationException("U-Type instructions do not have rs1 field.")
    override val rs2    get() = throw UnsupportedOperationException("U-Type instructions do not have rs2 field.")
    override val funct7 get() = throw UnsupportedOperationException("U-Type instructions do not have funct7 field.")
}

@Suppress("PrivatePropertyName")
data class UJTypeInstruction(val instruction: UInt) : InstructionData {
    override val opcode  = instruction          and 0x7Fu  // bits 0-6
    override val rd      = (instruction shr 7)  and 0x1Fu  // bits 7-11

    private val imm20   = (instruction shr 31) and 0x1u    // bit 31
    private val imm10_1 = (instruction shr 21) and 0x3FFu  // bits 21-30
    private val imm11   = (instruction shr 20) and 0x1u    // bit 20
    private val imm19_12= (instruction shr 12) and 0xFFu   // bits 12-19

    override val imm = signImm((imm20 shl 20) or (imm19_12 shl 12) or (imm11 shl 11) or (imm10_1 shl 1), 0x100000u)

    // UJ-Type instructions do not have funct3, rs1, rs2, or funct7 fields
    override val funct3 get() = throw UnsupportedOperationException("UJ-Type instructions do not have funct3 field.")
    override val rs1    get() = throw UnsupportedOperationException("UJ-Type instructions do not have rs1 field.")
    override val rs2    get() = throw UnsupportedOperationException("UJ-Type instructions do not have rs2 field.")
    override val funct7 get() = throw UnsupportedOperationException("UJ-Type instructions do not have funct7 field.")
}

private fun signImm(imm: UInt, mask: UInt): Int {
    // If sign bit is not set, return the immediate as positive Int
    if ((imm and mask) == 0u) return imm.toInt()

    // Determine the width (number of bits) covered by the mask (e.g. mask=0x800u -> width=12)
    var width = 0
    var tmp = mask
    while (tmp != 0u) {
        width++
        tmp = tmp shr 1
    }

    // Create a full mask with ones in the immediate bits: (1 << width) - 1
    val fullMask = if (width >= 32) {
        0xFFFFFFFFu
    } else {
        (1u shl width) - 1u
    }

    // Invert fullMask to get ones above the immediate field, OR with imm to set high bits,
    // then convert to signed Int to get the correct negative value.
    return ((imm or (fullMask.inv()))).toInt()
}