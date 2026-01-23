package at.jeb.ricsv

import at.jeb.riscv.Decoder
import at.jeb.riscv.instructions.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DecoderTest {

    @Test
    fun decoderAddiTest() {
        // given
        val instruction = 0b000000000001_00000_000_00001_0010011u // addi x1, x0, 1
        val expectedInstruction = IInstructionTypes.ADDI
        val expectedOpcode = 0b0010011u
        val expectedRd = 0b00001u
        val expectedFunct3 = 0b000u
        val expectedRs1 = 0b00000u
        val expectedImm = 0b000000000001u

        // when
        val actual = Decoder.decodeInstruction(instruction)

        // then
        assertEquals(expectedInstruction, actual.type)
        assertEquals(expectedOpcode, actual.data.opcode)
        assertEquals(expectedRd, actual.data.rd)
        assertEquals(expectedFunct3, actual.data.funct3)
        assertEquals(expectedRs1, actual.data.rs1)
        assertEquals(expectedImm.toInt(), actual.data.imm)
    }

    @Test
    fun decoderAddiWithNegativeImmediateTest() {
        // given
        val instruction = 0b111111100000_00001_000_00010_0010011u // addi x2, x1, -16
        val expectedInstruction = IInstructionTypes.ADDI
        val expectedOpcode = 0b0010011u
        val expectedRd = 0b00010u
        val expectedFunct3 = 0b000u
        val expectedRs1 = 0b00001u
        val expectedImm = -32

        // when
        val actual = Decoder.decodeInstruction(instruction)

        // then
        assertEquals(expectedInstruction, actual.type)
        assertEquals(expectedOpcode, actual.data.opcode)
        assertEquals(expectedRd, actual.data.rd)
        assertEquals(expectedFunct3, actual.data.funct3)
        assertEquals(expectedRs1, actual.data.rs1)
        assertEquals(expectedImm, actual.data.imm)
    }


    @Test
    fun decoderAddTest() {
        // given
        val instruction = 0b0000000_00010_00001_000_00011_0110011u // add x3, x1, x2

        val expected = Decoder.InstructionValue(
            type = RInstructionTypes.ADD,
            data = RTypeInstruction(instruction)
        )

        // when
        val actual = Decoder.decodeInstruction(instruction)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun decoderStoreTest() {
        // given
        val instruction = 0b0000000_00010_00001_010_00100_0100011u // sw x2, 4(x1)
        val expectedInstruction = SInstructionTypes.SW
        val expectedOpcode = 0b0100011u
        val expectedFunct3 = 0b010u
        val expectedRs1 = 0b00001u
        val expectedRs2 = 0b00010u
        val expectedImm = 4

        // when
        val actual = Decoder.decodeInstruction(instruction)

        // then
        assertEquals(expectedInstruction, actual.type)
        assertEquals(expectedOpcode, actual.data.opcode)
        assertEquals(expectedFunct3, actual.data.funct3)
        assertEquals(expectedRs1, actual.data.rs1)
        assertEquals(expectedRs2, actual.data.rs2)
        assertEquals(expectedImm, actual.data.imm)
    }

    @Test
    fun decoderTestNegativeJumpAndLinkImmediate() {
        // given
        val instruction = 0b1111111_11110_00000_000_00001_1101111u // jal x1, -2
        val expectedInstruction = UJTypeInstructionTypes.JAL
        val expectedOpcode = 0b1101111u
        val expectedRd = 0b00001u
        val expectedImm = -2

        // when
        val actual = Decoder.decodeInstruction(instruction)
        println("Decoded instruction: ${actual.data.imm.toUInt().toString(2)}")
        println("Expected immediate : ${expectedImm.toUInt().toString(2)}")

        // then
        assertEquals(expectedInstruction, actual.type)
        assertEquals(expectedOpcode, actual.data.opcode)
        assertEquals(expectedRd, actual.data.rd)
        assertEquals(expectedImm, actual.data.imm)
    }
}