package at.jeb.riscv

import at.jeb.riscv.components.Memory
import at.jeb.riscv.components.Register
import at.jeb.riscv.extensions.plus
import at.jeb.riscv.instructions.InstructionType

class RISCVCpu {
    val memory = Memory(32 * 1024) // 64MB Memory
    val instructionMemory = Memory()
    val register = Register()

    var pc: UInt = 0u
        private set
    var instrCount: UInt = 0u
        private set
    var isHalted: Boolean = true
        private set

    init {
        register.write(Register.RegisterName.SP, memory.size)
    }

    var executedInstructionHistory = mutableListOf<String>()
        private set

    private var decodedInst = Decoder.decodeInstruction(0x13u) // Default NOOP instruction (ADDI x0, x0, 0)

    fun step(): Boolean {
        if (isHalted) return false

        loadInstruction()
        instrCount += 1u
        executeInstruction()
        return true
    }

    private fun loadInstruction() {
        val instruction = instructionMemory.readWord(pc)
        decodedInst = Decoder.decodeInstruction(instruction)

        pc += 4u
    }

    fun loadProgram(program: UIntArray) {
        instructionMemory.load(program)
        isHalted = false
    }

    fun printCurrentState() {
        println("PC: $pc")
        println("Instruction Count: $instrCount")
        println("Registers:")
        register.printRegisters()
        println("Memory:")
        memory.printMemoryDump()
    }


    // Read from registers rs1 and rs2, perform ALU operation, write result to rd
    private fun executeInstruction() {
        when (decodedInst.type) {
            is InstructionType.R -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.format.rs1),
                    register.read(decodedInst.format.rs2)
                )
                executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.format.rd}, x${decodedInst.format.rs1}, x${decodedInst.format.rs2} => ${result.result}")
                register.write(decodedInst.format.rd, result.result)
            }

            is InstructionType.I -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.format.rs1),
                    decodedInst.format.imm.toUInt()
                )

                executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.format.rd}, x${decodedInst.format.rs1}, ${decodedInst.format.imm} => ${result.result.toInt()}")
                register.write(decodedInst.format.rd, result.result)
            }

            is InstructionType.S -> {
                when (decodedInst.type) {
                    InstructionType.S.SW -> {
                        val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                        val value = register.read(decodedInst.format.rs2)
                        memory.writeWord(address, value)

                        executedInstructionHistory.add("sw x${decodedInst.format.rs2}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => Mem[${address}] = $value")
                    }

                    InstructionType.S.SH -> {
                        val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                        val value = register.read(decodedInst.format.rs2)
                        memory.writeHalfWord(address, value.toUShort())

                        executedInstructionHistory.add("sh x${decodedInst.format.rs2}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => Mem[${address}] = ${value and 0xFFFFu}")
                    }

                    InstructionType.S.SB -> {
                        val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                        val value = register.read(decodedInst.format.rs2)
                        memory.writeByte(address, value.toUByte())

                        executedInstructionHistory.add("sb x${decodedInst.format.rs2}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => Mem[${address}] = ${value and 0xFFu}")
                    }
                }
            }

            is InstructionType.SB -> {
                val result = decodedInst.type.aluFunction.invoke(
                    register.read(decodedInst.format.rs1),
                    register.read(decodedInst.format.rs2)
                )

                if (result.zero) {
                    val targetAddress = pc.toInt() - 4 + decodedInst.format.imm
                    pc = targetAddress.toUInt()

                    executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.format.rs1}, x${decodedInst.format.rs2}, ${decodedInst.format.imm} => PC = $targetAddress")
                } else {
                    executedInstructionHistory.add("${decodedInst.type.instructionName} x${decodedInst.format.rs1}, x${decodedInst.format.rs2}, ${decodedInst.format.imm} => No branch taken")
                }
            }


            // Implementation on not generalizable instructions below
            InstructionType.ILoad.LW -> {
                val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                val value = memory.readWord(address)
                register.write(decodedInst.format.rd, value)

                executedInstructionHistory.add("lw x${decodedInst.format.rd}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => x${decodedInst.format.rd} = Mem[${address}] = $value")
            }

            InstructionType.ILoad.LH -> {
                val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                val value = memory.readHalfWord(address).toUInt()
                register.write(decodedInst.format.rd, value)

                executedInstructionHistory.add("lh x${decodedInst.format.rd}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => x${decodedInst.format.rd} = Mem[${address}] = $value")
            }

            InstructionType.ILoad.LB -> {
                val address = register.read(decodedInst.format.rs1) + decodedInst.format.imm
                val value = memory.readByte(address).toUInt()
                register.write(decodedInst.format.rd, value)

                executedInstructionHistory.add("lb x${decodedInst.format.rd}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => x${decodedInst.format.rd} = Mem[${address}] = $value")
            }

            InstructionType.ILoad.JALR -> {
                val targetAddress = (register.read(decodedInst.format.rs1) + decodedInst.format.imm) and 0xFFFFFFFEu
                val returnAddress = pc
                register.write(decodedInst.format.rd, returnAddress)
                pc = targetAddress

                if (targetAddress == 0u) {
                    isHalted = true
                    executedInstructionHistory.add("jalr x${decodedInst.format.rd}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => HALT triggered")
                } else {
                    val written = register.read(decodedInst.format.rd)
                    executedInstructionHistory.add("jalr x${decodedInst.format.rd}, ${decodedInst.format.imm}(x${decodedInst.format.rs1}) => x${decodedInst.format.rd} = $written, PC = $targetAddress")
                }
            }

            InstructionType.ILoad.MRET -> {
                // For simplicity, we assume MRET just sets PC to a fixed address (e.g., 0x00000000)
                val returnAddress = 0u
                pc = returnAddress

                executedInstructionHistory.add("mret => PC = $returnAddress")
            }

            InstructionType.U.LUI -> {
                val value = decodedInst.format.imm.toUInt() shl 12
                register.write(decodedInst.format.rd, value)

                executedInstructionHistory.add("lui x${decodedInst.format.rd}, ${decodedInst.format.imm} => x${decodedInst.format.rd} = $value")
            }

            InstructionType.U.AUIPC -> {
                val value = pc - 4u + (decodedInst.format.imm.toUInt() shl 12)
                register.write(decodedInst.format.rd, value)

                executedInstructionHistory.add("auipc x${decodedInst.format.rd}, ${decodedInst.format.imm} => x${decodedInst.format.rd} = $value")
            }

            InstructionType.UJ.JAL -> {
                val targetAddress = pc.toInt() - 4 + decodedInst.format.imm
                val returnAddress = pc
                register.write(decodedInst.format.rd, returnAddress)
                pc = targetAddress.toUInt()

                    val written = register.read(decodedInst.format.rd)
                    executedInstructionHistory.add("jal x${decodedInst.format.rd}, ${decodedInst.format.imm} => x${decodedInst.format.rd} = $written, PC = $targetAddress")
            }

            else -> {
                throw IllegalArgumentException("Unsupported instruction type: ${decodedInst.type}")
            }
        }
    }
}