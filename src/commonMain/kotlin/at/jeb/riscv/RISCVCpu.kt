package at.jeb.riscv

import at.jeb.riscv.components.Memory
import at.jeb.riscv.components.Register
import at.jeb.riscv.extensions.plus
import at.jeb.riscv.instructions.*

class RISCVCpu {
    val memory = Memory(32 * 1024) // 64MB Memory
    val instructionMemory = InstructionMemory()
    val register = Register()

    var pc: UInt = 0u
        private set
    var instrCount: UInt = 0u
        private set

    init {
        register.write(Register.RegisterName.SP, memory.size)
    }

    var executedInstructionHistory = mutableListOf<String>()
        private set

    private var decodedInst: Decoder.InstructionValue = Decoder.decodeInstruction(0x13u) // Default NOOP instruction (ADDI x0, x0, 0)

    fun step() {
        loadInstruction()
        instrCount += 1u
        executeInstruction()
    }

    private fun loadInstruction() {
        val instruction = instructionMemory.fetch(pc)
        decodedInst = Decoder.decodeInstruction(instruction)

        pc += 4u
    }

    fun loadProgram(program: UIntArray) {
        instructionMemory.load(program)
    }

    fun printCurrentState() {
        println("PC: $pc")
        println("Registers:")
        register.printRegisters()
        println("Memory:")
        memory.printMemoryDump()
    }


    // Read from registers rs1 and rs2, perform ALU operation, write result to rd
    private fun executeInstruction() {
        when (decodedInst.type.ftmType) {
            FMTType.R_TYPE -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.data.rs1),
                    register.read(decodedInst.data.rs2)
                )
                executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.data.rd}, x${decodedInst.data.rs1}, x${decodedInst.data.rs2} => ${result.result}")
                register.write(decodedInst.data.rd, result.result)
            }

            FMTType.I_TYPE -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.data.rs1),
                    decodedInst.data.imm.toUInt()
                )

                executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.data.rd}, x${decodedInst.data.rs1}, ${decodedInst.data.imm} => ${result.result.toInt()}")
                register.write(decodedInst.data.rd, result.result)
            }

            FMTType.S_TYPE -> {
                when (decodedInst.type) {
                    SInstructionTypes.SW -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = register.read(decodedInst.data.rs2)
                        memory.writeWord(address, value)

                        executedInstructionHistory.add("sw x${decodedInst.data.rs2}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => Mem[${address}] = $value")
                    }

                    SInstructionTypes.SH -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = register.read(decodedInst.data.rs2)
                        memory.writeHalfWord(address, value.toUShort())

                        executedInstructionHistory.add("sh x${decodedInst.data.rs2}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => Mem[${address}] = ${value and 0xFFFFu}")
                    }

                    SInstructionTypes.SB -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = register.read(decodedInst.data.rs2)
                        memory.writeByte(address, value.toUByte())

                        executedInstructionHistory.add("sb x${decodedInst.data.rs2}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => Mem[${address}] = ${value and 0xFFu}")
                    }
                }
            }

            FMTType.SB_TYPE -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.data.rs1),
                    register.read(decodedInst.data.rs2)
                )

                if (result.zero) {
                    val targetAddress = pc.toInt() - 4 + decodedInst.data.imm
                    pc = targetAddress.toUInt()

                    executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.data.rs1}, x${decodedInst.data.rs2}, ${decodedInst.data.imm} => PC = $targetAddress")
                } else {
                    executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.data.rs1}, x${decodedInst.data.rs2}, ${decodedInst.data.imm} => No branch taken")
                }
            }

            FMTType.I_TYPE_LOAD -> {
                when (decodedInst.type) {
                    ITypeLoaders.LW -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = memory.readWord(address)
                        register.write(decodedInst.data.rd, value)

                        executedInstructionHistory.add("lw x${decodedInst.data.rd}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => x${decodedInst.data.rd} = Mem[${address}] = $value")
                    }

                    ITypeLoaders.LH -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = memory.readHalfWord(address).toUInt()
                        register.write(decodedInst.data.rd, value)

                        executedInstructionHistory.add("lh x${decodedInst.data.rd}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => x${decodedInst.data.rd} = Mem[${address}] = ${value}")
                    }

                    ITypeLoaders.LB -> {
                        val address = register.read(decodedInst.data.rs1) + decodedInst.data.imm
                        val value = memory.readByte(address).toUInt()
                        register.write(decodedInst.data.rd, value)

                        executedInstructionHistory.add("lb x${decodedInst.data.rd}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => x${decodedInst.data.rd} = Mem[${address}] = ${value}")
                    }

                    ITypeLoaders.JALR -> {
                        val targetAddress = (register.read(decodedInst.data.rs1) + decodedInst.data.imm) and 0xFFFFFFFEu
                        val returnAddress = pc
                        register.write(decodedInst.data.rd, returnAddress)
                        pc = targetAddress

                        executedInstructionHistory.add("jalr x${decodedInst.data.rd}, ${decodedInst.data.imm}(x${decodedInst.data.rs1}) => x${decodedInst.data.rd} = $returnAddress, PC = $targetAddress")
                    }

                    ITypeLoaders.MRET -> {
                        // For simplicity, we assume MRET just sets PC to a fixed address (e.g., 0x00000000)
                        val returnAddress = 0u
                        pc = returnAddress

                        executedInstructionHistory.add("mret => PC = $returnAddress")
                    }
                }
            }

            FMTType.U_TYPE -> {
                when (decodedInst.type) {
                    UTypeInstructionTypes.LUI -> {
                        val value = decodedInst.data.imm.toUInt() shl 12
                        register.write(decodedInst.data.rd, value)

                        executedInstructionHistory.add("lui x${decodedInst.data.rd}, ${decodedInst.data.imm} => x${decodedInst.data.rd} = $value")
                    }

                    UTypeInstructionTypes.AUIPC -> {
                        val value = pc - 4u + (decodedInst.data.imm.toUInt() shl 12)
                        register.write(decodedInst.data.rd, value)

                        executedInstructionHistory.add("auipc x${decodedInst.data.rd}, ${decodedInst.data.imm} => x${decodedInst.data.rd} = $value")
                    }
                }
            }

            FMTType.UJ_TYPE -> {
                when (decodedInst.type) {
                    UJTypeInstructionTypes.JAL -> {
                        val targetAddress = pc.toInt() - 4 + decodedInst.data.imm
                        val returnAddress = pc
                        register.write(decodedInst.data.rd, returnAddress)
                        pc = targetAddress.toUInt()

                        executedInstructionHistory.add("jal x${decodedInst.data.rd}, ${decodedInst.data.imm} => x${decodedInst.data.rd} = $returnAddress, PC = $targetAddress")
                    }
                }
            }
        }
    }
}