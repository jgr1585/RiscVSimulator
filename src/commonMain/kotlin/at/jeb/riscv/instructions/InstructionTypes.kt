@file:Suppress("SpellCheckingInspection")

package at.jeb.riscv.instructions

import at.jeb.riscv.components.ALU
import at.jeb.riscv.components.ALU_FUNCTION


object InstructionType {

    interface Type {
        val aluFunction: ALU_FUNCTION
        val instructionName: String
    }

    enum class R(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
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

    enum class I(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
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

    enum class ILoad(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
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

    enum class S(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
        SB(ALU::nop),
        SH(ALU::nop),
        SW(ALU::nop);

        override val instructionName: String
            get() = this.name.lowercase()
    }

    enum class SB(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
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

    enum class U(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
        LUI(ALU::nop),
        AUIPC(ALU::nop);

        override val instructionName: String
            get() = this.name.lowercase()
    }

    enum class UJ(
        override val aluFunction: ALU_FUNCTION,
    ) : Type {
        JAL(ALU::nop);

        override val instructionName: String
            get() = this.name.lowercase()
    }

}