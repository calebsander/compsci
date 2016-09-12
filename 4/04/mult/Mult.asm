// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

@R2
M=0 //R2 = 0
@bit
M=0 //bit = 0
@bitmask
M=1 //bitmask = 1
(LOOP_BIT)
	@bit
	D=M
	@15
	D=D-A
	@LOOP_BIT_END
	D;JGT //LOOP_BIT_END if bit > 15

	@bitmask
	D=M //D = bitmask
	@R0
	A=M //A = R0
	D=A&D //D = R0 & bitmask
	@ITERATE
	D;JEQ //skip adding if bit is 0

	//Shift R1 left bit bits and add the result to the sum
	@shift_count
	M=0
	@R1
	D=M //D = R1
	@shift_result
	M=D //shift_result = R1
	(LOOP_SHIFT)
		@bit
		D=M //D = bit
		@shift_count
		D=D-M //D = bit - shift_count
		@LOOP_SHIFT_END
		D;JEQ //LOOP_SHIFT_END if shift_count == bit

		@shift_result
		D=M
		M=M+D //shift_result <<= 1
		@shift_count
		M=M+1
		@LOOP_SHIFT
		0;JMP
	(LOOP_SHIFT_END)
	@R2
	D=M //D = R2
	@shift_result
	D=D+M //D = R2 + shift_result
	@R2
	M=D //R2 = R2 + shift_result

	(ITERATE)
	@bit
	M=M+1 //bit++
	@bitmask
	D=M
	M=M+D //bitmask <<= 1
	@LOOP_BIT
	0;JMP
(LOOP_BIT_END)
@LOOP_BIT_END
0;JMP