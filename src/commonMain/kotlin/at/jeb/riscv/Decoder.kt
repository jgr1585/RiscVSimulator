package at.jeb.riscv

import at.jeb.riscv.instructions.InstructionFormat
import at.jeb.riscv.instructions.InstructionType


private const val MASK_OPCODE = 0x7Fu // 7 bits

private const val R_TYPE_OPCODE       = 0x33u // 0110011
private const val I_TYPE_OPCODE       = 0x13u // 0010011
private const val S_TYPE_OPCODE       = 0x23u // 0100011
private const val SB_TYPE_OPCODE      = 0x63u // 1100011
private const val I_TYPE_LOAD_OPCODE  = 0x03u // 0000011
private const val I_TYPE_JALR_OPCODE  = 0x67u // 1100111
private const val I_TYPE_FENCE_OPCODE = 0x0Fu // 0001111
private const val I_TYPE_MRET_OPCODE  = 0x10u // 0010000
private const val U_TYPE_OPCODE       = 0x37u // 0110111
private const val U_TYPE_AUIPC_OPCODE = 0x17u // 0010111
private const val UJ_TYPE_OPCODE      = 0x6Fu // 1101111

object Decoder {
    fun decodeInstruction(instruction: UInt): DecoderReturn {
        return when (instruction and MASK_OPCODE) { // bits 0-6

            R_TYPE_OPCODE -> { // R-type instructions
                val inst = InstructionFormat.R(instruction)

                when (inst.funct7) {
                    0x00u, 0x20u -> { // RV32 arithmetic operations
                        when (inst.funct3) {
                            0x0u -> { // ADD or SUB
                                when (inst.funct7) {
                                    0x00u -> InstructionValue(inst, InstructionType.R.ADD) // ADD
                                    0x20u -> InstructionValue(inst, InstructionType.R.SUB) // SUB
                                    else -> throw IllegalArgumentException("Invalid funct7 for ADD/SUB: ${inst.funct7}")
                                }
                            }
                            0x1u -> InstructionValue(inst, InstructionType.R.SLL) // SLL
                            0x2u -> InstructionValue(inst, InstructionType.R.SLT) // SLT
                            0x3u -> InstructionValue(inst, InstructionType.R.SLTU) // SLTU
                            0x4u -> InstructionValue(inst, InstructionType.R.XOR) // XOR
                            0x5u -> { // SRL or SRA
                                when (inst.funct7) {
                                    0x00u -> InstructionValue(inst, InstructionType.R.SRL) // SRL
                                    0x20u -> InstructionValue(inst, InstructionType.R.SRA) // SRA
                                    else -> throw IllegalArgumentException("Invalid funct7 for SRL/SRA: ${inst.funct7}")
                                }
                            }
                            0x6u -> InstructionValue(inst, InstructionType.R.OR)  // OR
                            0x7u -> InstructionValue(inst, InstructionType.R.AND) // AND

                            else -> throw UnsupportedOperationException("Should not happen: Invalid funct3 for RV32I: ${inst.funct3}")
                        }
                    }

                    0x01u -> { // RV32M Multiply extension
                        when (inst.funct3) {
                            0x0u -> InstructionValue(inst, InstructionType.R.MUL)    // MUL
                            0x1u -> InstructionValue(inst, InstructionType.R.MULH)   // MULH
                            0x2u -> InstructionValue(inst, InstructionType.R.MULHSU) // MULHSU
                            0x3u -> InstructionValue(inst, InstructionType.R.MULHU)  // MULHU
                            0x4u -> InstructionValue(inst, InstructionType.R.DIV)    // DIV
                            0x5u -> InstructionValue(inst, InstructionType.R.DIVU)   // DIVU
                            0x6u -> InstructionValue(inst, InstructionType.R.REM)    // REM
                            0x7u -> InstructionValue(inst, InstructionType.R.REMU)   // REMU

                            else -> throw IllegalArgumentException("Invalid funct3 for RV32M: ${inst.funct3}")
                        }
                    }

                    else -> throw IllegalArgumentException("Invalid funct7 for R-type: ${inst.funct7}")
                }
            }

            I_TYPE_OPCODE -> { // I-type instructions
                val inst = InstructionFormat.I(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, InstructionType.I.ADDI)  // ADDI
                    0x2u -> InstructionValue(inst, InstructionType.I.SLTI)  // SLTI
                    0x3u -> InstructionValue(inst, InstructionType.I.SLTIU) // SLTIU
                    0x4u -> InstructionValue(inst, InstructionType.I.XORI)  // XORI
                    0x5u -> InstructionValue(inst, InstructionType.I.SRLI)  // SRLI
                    0x6u -> InstructionValue(inst, InstructionType.I.ORI)   // ORI
                    0x7u -> InstructionValue(inst, InstructionType.I.ANDI)  // ANDI

                    else -> throw UnsupportedOperationException("Should not happen: Invalid funct3 for I-type: ${inst.funct3}")
                }
            }

            S_TYPE_OPCODE -> { // S-type instructions
                val inst = InstructionFormat.S(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, InstructionType.S.SB) // SB
                    0x1u -> InstructionValue(inst, InstructionType.S.SH) // SH
                    0x2u -> InstructionValue(inst, InstructionType.S.SW) // SW

                    else -> throw IllegalArgumentException("Invalid funct3 for S-type: ${inst.funct3}")
                }
            }

            SB_TYPE_OPCODE -> { // SB-type instructions
                val inst = InstructionFormat.SB(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, InstructionType.SB.BEQ)  // BEQ
                    0x1u -> InstructionValue(inst, InstructionType.SB.BNE)  // BNE
                    0x4u -> InstructionValue(inst, InstructionType.SB.BLT)  // BLT
                    0x5u -> InstructionValue(inst, InstructionType.SB.BGE)  // BGE
                    0x6u -> InstructionValue(inst, InstructionType.SB.BLTU) // BLTU
                    0x7u -> InstructionValue(inst, InstructionType.SB.BGEU) // BGEU

                    else -> throw IllegalArgumentException("Invalid funct3 for SB-type: ${inst.funct3}")
                }
            }


            I_TYPE_LOAD_OPCODE -> { // I-type load instructions
                val inst = InstructionFormat.I(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, InstructionType.ILoad.LB)  // LB
                    0x1u -> InstructionValue(inst, InstructionType.ILoad.LH)  // LH
                    0x2u -> InstructionValue(inst, InstructionType.ILoad.LW)  // LW
                    0x4u -> InstructionValue(inst, InstructionType.ILoad.LBU) // LBU
                    0x5u -> InstructionValue(inst, InstructionType.ILoad.LHU) // LHU

                    else -> throw IllegalArgumentException("Invalid funct3 for load: ${inst.funct3}")
                }
            }

            I_TYPE_JALR_OPCODE -> { // JALR instruction
                val inst = InstructionFormat.I(instruction)

                if (inst.funct3 == 0x0u) {
                    InstructionValue(inst, InstructionType.ILoad.JALR) // JALR
                } else {
                    throw IllegalArgumentException("Invalid funct3 for JALR: ${inst.funct3}")
                }
            }


            I_TYPE_FENCE_OPCODE -> { // FENCE instruction

                val inst = InstructionFormat.I(instruction)
                when (inst.funct3) {
                    0x0u -> { // FENCE
                        InstructionValue(inst, InstructionType.ILoad.FENCE) // FENCE
                    }
                    0x1u -> { // FENCE.I
                        InstructionValue(inst, InstructionType.ILoad.FENCE_I) // FENCE.I
                    }
                    else -> throw IllegalArgumentException("Invalid funct3 for FENCE: ${inst.funct3}")
                }
            }


            I_TYPE_MRET_OPCODE -> { // MRET instruction
                val inst = InstructionFormat.I(instruction)
                // MRET has funct3 == 0
                if (inst.funct3 == 0x0u) {
                    InstructionValue(inst, InstructionType.ILoad.MRET) // MRET
                } else {
                    throw IllegalArgumentException("Invalid funct3 for MRET: ${inst.funct3}")
                }
            }

            U_TYPE_OPCODE -> {
                val inst = InstructionFormat.U(instruction)
                InstructionValue(inst, InstructionType.U.LUI) // LUI
            }

            U_TYPE_AUIPC_OPCODE -> {
                val inst = InstructionFormat.U(instruction)
                InstructionValue(inst, InstructionType.U.AUIPC) // AUIPC
            }

            UJ_TYPE_OPCODE -> {
                val inst = InstructionFormat.UJ(instruction)
                InstructionValue(inst, InstructionType.UJ.JAL) // JAL
            }



            else -> throw IllegalArgumentException("Invalid opcode: ${(instruction and MASK_OPCODE).toString(2).padStart(7, '0')}. Instuction: ${instruction.toString(16)}") // Print opcode in binary
        }
    }

    data class InstructionValue<out InstructionFormatT : InstructionFormat.FMT, out InstructionTypeT : InstructionType.Type> (
        val format: InstructionFormatT,
        val type: InstructionTypeT
    )

    typealias DecoderReturn = InstructionValue<InstructionFormat.FMT, InstructionType.Type>
}