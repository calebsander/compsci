// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

//Initialize variables
@R2
M=0 //R2 = 0
@bit
M=0 //bit = 0
@bitmask
M=1 //bitmask = 1
@R1
D=M //D = R1
@shifted_for_add
M=D //shifted_for_add = R1

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

	//Add (R1 << bit) to the sum
	@R2
	D=M //D = R2
	@shifted_for_add
	D=D+M //D = R2 + shifted_for_add
	@R2
	M=D //R2 += shifted_for_add

	(ITERATE)
	@bit
	M=M+1 //bit++
	@bitmask
	D=M
	M=M+D //bitmask <<= 1 (== 1 << bit)
	@shifted_for_add
	D=M
	M=M+D //shifted_for_add <<= 1 (== R1 << bit)
	@LOOP_BIT
	0;JMP
(LOOP_BIT_END)
@LOOP_BIT_END
0;JMP