package at.jeb.riscv

import at.jeb.riscv.instructions.*


private val MASK_OPCODE = 0x7Fu // 7 bits

private val R_TYPE_OPCODE       = 0x33u // 0110011
private val I_TYPE_OPCODE       = 0x13u // 0010011
private val S_TYPE_OPCODE       = 0x23u // 0100011
private val SB_TYPE_OPCODE      = 0x63u // 1100011
private val I_TYPE_LOAD_OPCODE  = 0x03u // 0000011
private val I_TYPE_JALR_OPCODE  = 0x67u // 1100111
private val I_TYPE_FENCE_OPCODE = 0x0Fu // 0001111
private val I_TYPE_MRET_OPCODE  = 0x10u // 0010000
private val U_TYPE_OPCODE       = 0x37u // 0110111
private val U_TYPE_AUIPC_OPCODE = 0x17u // 0010111
private val UJ_TYPE_OPCODE      = 0x6Fu // 1101111

object Decoder {
    fun decodeInstruction(instruction: UInt): InstructionValue {
        return when (instruction and MASK_OPCODE) { // bits 0-6

            R_TYPE_OPCODE -> { // R-type instructions
                val inst = RTypeInstruction(instruction)

                when (inst.funct7) {
                    0x00u, 0x20u -> { // RV32 arithmetic operations
                        when (inst.funct3) {
                            0x0u -> { // ADD or SUB
                                when (inst.funct7) {
                                    0x00u -> InstructionValue(inst, RInstructionTypes.ADD) // ADD
                                    0x20u -> InstructionValue(inst, RInstructionTypes.SUB) // SUB
                                    else -> throw IllegalArgumentException("Invalid funct7 for ADD/SUB: ${inst.funct7}")
                                }
                            }
                            0x1u -> InstructionValue(inst, RInstructionTypes.SLL) // SLL
                            0x2u -> InstructionValue(inst, RInstructionTypes.SLT) // SLT
                            0x3u -> InstructionValue(inst, RInstructionTypes.SLTU) // SLTU
                            0x4u -> InstructionValue(inst, RInstructionTypes.XOR) // XOR
                            0x5u -> { // SRL or SRA
                                when (inst.funct7) {
                                    0x00u -> InstructionValue(inst, RInstructionTypes.SRL) // SRL
                                    0x20u -> InstructionValue(inst, RInstructionTypes.SRA) // SRA
                                    else -> throw IllegalArgumentException("Invalid funct7 for SRL/SRA: ${inst.funct7}")
                                }
                            }
                            0x6u -> InstructionValue(inst, RInstructionTypes.OR)  // OR
                            0x7u -> InstructionValue(inst, RInstructionTypes.AND) // AND

                            else -> throw UnsupportedOperationException("Should not happen: Invalid funct3 for RV32I: ${inst.funct3}")
                        }
                    }

                    0x01u -> { // RV32M Multiply extension
                        when (inst.funct3) {
                            0x0u -> InstructionValue(inst, RInstructionTypes.MUL)    // MUL
                            0x1u -> InstructionValue(inst, RInstructionTypes.MULH)   // MULH
                            0x2u -> InstructionValue(inst, RInstructionTypes.MULHSU) // MULHSU
                            0x3u -> InstructionValue(inst, RInstructionTypes.MULHU)  // MULHU
                            0x4u -> InstructionValue(inst, RInstructionTypes.DIV)    // DIV
                            0x5u -> InstructionValue(inst, RInstructionTypes.DIVU)   // DIVU
                            0x6u -> InstructionValue(inst, RInstructionTypes.REM)    // REM
                            0x7u -> InstructionValue(inst, RInstructionTypes.REMU)   // REMU

                            else -> throw IllegalArgumentException("Invalid funct3 for RV32M: ${inst.funct3}")
                        }
                    }

                    else -> throw IllegalArgumentException("Invalid funct7 for R-type: ${inst.funct7}")
                }
            }

            I_TYPE_OPCODE -> { // I-type instructions
                val inst = ITypeInstruction(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, IInstructionTypes.ADDI)  // ADDI
                    0x2u -> InstructionValue(inst, IInstructionTypes.SLTI)  // SLTI
                    0x3u -> InstructionValue(inst, IInstructionTypes.SLTIU) // SLTIU
                    0x4u -> InstructionValue(inst, IInstructionTypes.XORI)  // XORI
                    0x5u -> InstructionValue(inst, IInstructionTypes.SRLI)  // SRLI
                    0x6u -> InstructionValue(inst, IInstructionTypes.ORI)   // ORI
                    0x7u -> InstructionValue(inst, IInstructionTypes.ANDI)  // ANDI

                    else -> throw UnsupportedOperationException("Should not happen: Invalid funct3 for I-type: ${inst.funct3}")
                }
            }

            S_TYPE_OPCODE -> { // S-type instructions
                val inst = STypeInstruction(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, SInstructionTypes.SB) // SB
                    0x1u -> InstructionValue(inst, SInstructionTypes.SH) // SH
                    0x2u -> InstructionValue(inst, SInstructionTypes.SW) // SW

                    else -> throw IllegalArgumentException("Invalid funct3 for S-type: ${inst.funct3}")
                }
            }

            SB_TYPE_OPCODE -> { // SB-type instructions
                val inst = STypeInstruction(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, SBInstructionTypes.BEQ)  // BEQ
                    0x1u -> InstructionValue(inst, SBInstructionTypes.BNE)  // BNE
                    0x4u -> InstructionValue(inst, SBInstructionTypes.BLT)  // BLT
                    0x5u -> InstructionValue(inst, SBInstructionTypes.BGE)  // BGE
                    0x6u -> InstructionValue(inst, SBInstructionTypes.BLTU) // BLTU
                    0x7u -> InstructionValue(inst, SBInstructionTypes.BGEU) // BGEU

                    else -> throw IllegalArgumentException("Invalid funct3 for SB-type: ${inst.funct3}")
                }
            }


            I_TYPE_LOAD_OPCODE -> { // I-type load instructions
                val inst = ITypeInstruction(instruction)

                when (inst.funct3) {
                    0x0u -> InstructionValue(inst, ITypeLoaders.LB)  // LB
                    0x1u -> InstructionValue(inst, ITypeLoaders.LH)  // LH
                    0x2u -> InstructionValue(inst, ITypeLoaders.LW)  // LW
                    0x4u -> InstructionValue(inst, ITypeLoaders.LBU) // LBU
                    0x5u -> InstructionValue(inst, ITypeLoaders.LHU) // LHU

                    else -> throw IllegalArgumentException("Invalid funct3 for load: ${inst.funct3}")
                }
            }

            I_TYPE_JALR_OPCODE -> { // JALR instruction
                val inst = ITypeInstruction(instruction)

                if (inst.funct3 == 0x0u) {
                    InstructionValue(inst, ITypeLoaders.JALR) // JALR
                } else {
                    throw IllegalArgumentException("Invalid funct3 for JALR: ${inst.funct3}")
                }
            }


            I_TYPE_FENCE_OPCODE -> { // FENCE instruction

                val inst = ITypeInstruction(instruction)
                when (inst.funct3) {
                    0x0u -> { // FENCE
                        InstructionValue(inst, ITypeLoaders.FENCE) // FENCE
                    }
                    0x1u -> { // FENCE.I
                        InstructionValue(inst, ITypeLoaders.FENCE_I) // FENCE.I
                    }
                    else -> throw IllegalArgumentException("Invalid funct3 for FENCE: ${inst.funct3}")
                }
            }


            I_TYPE_MRET_OPCODE -> { // MRET instruction
                val inst = ITypeInstruction(instruction)
                // MRET has funct3 == 0
                if (inst.funct3 == 0x0u) {
                    InstructionValue(inst, ITypeLoaders.MRET) // MRET
                } else {
                    throw IllegalArgumentException("Invalid funct3 for MRET: ${inst.funct3}")
                }
            }

            U_TYPE_OPCODE -> {
                val inst = UTypeInstruction(instruction)
                InstructionValue(inst, UTypeInstructionTypes.LUI) // LUI
            }

            U_TYPE_AUIPC_OPCODE -> {
                val inst = UTypeInstruction(instruction)
                InstructionValue(inst, UTypeInstructionTypes.AUIPC) // AUIPC
            }

            UJ_TYPE_OPCODE -> {
                val inst = UJTypeInstruction(instruction)
                InstructionValue(inst, UJTypeInstructionTypes.JAL) // JAL
            }



            else -> throw IllegalArgumentException("Invalid opcode: ${(instruction and MASK_OPCODE).toString(2).padStart(7, '0')}") // Print opcode in binary
        }
    }

    data class InstructionValue(
        val data: InstructionData,
        val type: Instruction
    )
}