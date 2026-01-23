@file:Suppress("SpellCheckingInspection")

package at.jeb.riscv.instructions

import at.jeb.riscv.components.ALU
import at.jeb.riscv.components.ALU_FUNCTION


enum class FMTType {
    R_TYPE,
    I_TYPE,
    I_TYPE_LOAD,
    S_TYPE,
    SB_TYPE,
    U_TYPE,
    UJ_TYPE,
}

interface Instruction {
    val aluFunction: ALU_FUNCTION
    val instructionName: String

    val ftmType: FMTType
        get() = when (this) {
            is RInstructionTypes -> FMTType.R_TYPE
            is IInstructionTypes -> FMTType.I_TYPE
            is ITypeLoaders      -> FMTType.I_TYPE_LOAD
            is SInstructionTypes -> FMTType.S_TYPE
            is SBInstructionTypes -> FMTType.SB_TYPE
            is UTypeInstructionTypes -> FMTType.U_TYPE
            is UJTypeInstructionTypes -> FMTType.UJ_TYPE

            else -> throw IllegalArgumentException("Unknown instruction type")
        }
}

enum class RInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    ADD(ALU::add),
    SUB(ALU::sub),
    SLL(ALU::sll),
    SLT(ALU::slt),
    SLTU(ALU::sltu),
    SRL(ALU::srl),
    SRA(ALU::sra),
    AND(ALU::and),
    OR(ALU::or),
    XOR(ALU::xor),
    MUL(ALU::mul),
    MULH(ALU::mulh),
    MULHSU(ALU::mulhsu),
    MULHU(ALU::mulhu),
    DIV(ALU::div),
    DIVU(ALU::divu),
    REM(ALU::rem),
    REMU(ALU::remu);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class IInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    ADDI(ALU::add),
    ANDI(ALU::and),
    ORI(ALU::or),
    XORI(ALU::xor),
    SLTI(ALU::slt),
    SLTIU(ALU::sltu),
    SRLI(ALU::srl);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class ITypeLoaders(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    LB(ALU::nop),
    LH(ALU::nop),
    LW(ALU::nop),
    LBU(ALU::nop),
    LHU(ALU::nop),
    JALR(ALU::nop),
    MRET(ALU::nop),
    FENCE(ALU::nop),
    FENCE_I(ALU::nop);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class SInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    SB(ALU::nop),
    SH(ALU::nop),
    SW(ALU::nop);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class SBInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    // Uses the zero result of the ALU to determine branch outcome
    BEQ(ALU::and),
    BNE(ALU::or),
    BLT(ALU::slt),
    BGE(ALU::sltu),
    BLTU(ALU::srl),
    BGEU(ALU::sra);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class UTypeInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    LUI(ALU::nop),
    AUIPC(ALU::nop);

    override val instructionName: String
        get() = this.name.lowercase()
}

enum class UJTypeInstructionTypes(
    override val aluFunction: ALU_FUNCTION,
): Instruction {
    JAL(ALU::nop);

    override val instructionName: String
        get() = this.name.lowercase()
}
