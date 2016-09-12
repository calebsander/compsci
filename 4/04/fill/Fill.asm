// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

@KBD
D=M //D = KBD
@last_key
M=D //last_key = KBD
@SCREEN
D=A //D = &SCREEN
@8192
D=D+A //D = &SCREEN + 8192
@LAST_PIXEL
M=D //LAST_PIXEL = &SCREEN + 8192
(LOOP)
	@KBD
	D=M //D = KBD
	@this_key
	M=D //this_key = KBD
	@last_key
	D=D-M //D = KBD - last_key
	@LOOP
	D;JEQ //jump back to LOOP if the key hasn't changed

	//Set pixels
	@SCREEN
	D=A //D = &SCREEN
	@pixel
	M=D //pixel = &SCREEN
	(PIXEL_LOOP)
		@pixel
		D=M //D = pixel
		@LAST_PIXEL
		D=D-M
		@PIXEL_END
		D;JEQ //jump if pixel == LAST_PIXEL

		@pixel
		A=M //A = *pixel
		M=!M //*pixel = ~(*pixel)

		@pixel
		M=M+1
		@PIXEL_LOOP
		0;JMP
	(PIXEL_END)

	@this_key
	D=M //D = this_key
	@last_key
	M=D //last_key = this_key
	@LOOP
	0;JMP