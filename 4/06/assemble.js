#!/usr/bin/env node
const fs = require('fs')

if (process.argv.length !== 3) throw new Error('Incorrect syntax. Use: node assemble.js FILE[.asm]')

class LineParseState {}
const AT_START = new LineParseState,
	CONSTANT = new LineParseState,
	LABEL = new LineParseState,
	AFTER_LABEL = new LineParseState,
	COMPUTE = new LineParseState,
	JMP_CODE = new LineParseState
const WHITESPACE = /^\s$/
const DIGIT = /^\d$/
const ALLOCATED_REGISTERS = 16
class AInstruction {
	constructor(value) {
		this.value = value
		this.isNumber = true
		for (const char of value) {
			if (!DIGIT.test(char)) {
				this.isNumber = false
				break
			}
		}
		if (this.isNumber) this.value = Number(this.value)
	}
	resolveSymbol({SYMBOL_MAP, firstUnusedRegister}) {
		if (this.isNumber) return
		let newSymbol
		let value = SYMBOL_MAP.get(this.value)
		if (value === undefined) {
			value = firstUnusedRegister
			SYMBOL_MAP.set(this.value, value)
			newSymbol = true
		}
		else newSymbol = false
		this.value = value
		this.isNumber = true
		return newSymbol
	}
	toUint16() {
		return this.value
	}
}
const JLT = 1 << 2, JEQ = 1 << 1, JGT = 1
const COMPUTE_CODES = new Map()
	.set('0', 0b101010)
	.set('1', 0b111111)
	.set('-1', 0b111010)
	.set('D', 0b001100)
	.set('A', 0b110000)
	.set('!D', 0b001101)
	.set('!A', 0b110001)
	.set('-D', 0b001111)
	.set('-A', 0b110011)
	.set('D+1', 0b011111)
	.set('A+1', 0b110111)
	.set('D-1', 0b001110)
	.set('A-1', 0b110010)
	.set('D+A', 0b000010)
	.set('A+D', 0b000010)
	.set('D-A', 0b010011)
	.set('A-D', 0b000111)
	.set('D&A', 0b000000)
	.set('A&D', 0b000000)
	.set('D|A', 0b010101)
	.set('A|D', 0b010101)
class CInstruction {
	constructor({expression, jmpExpression}) {
		const equalIndex = expression.indexOf('=')
		let destinations, computation
		if (equalIndex === -1) {
			destinations = ''
			computation = expression
		}
		else {
			destinations = expression.substring(0, equalIndex)
			computation = expression.substring(equalIndex + 1)
		}
		this.destinations = new Set
		for (const destination of destinations) this.destinations.add(destination)
		this.computation = computation
		this.jmpExpression = jmpExpression
	}
	parseCompute() {
		const value = COMPUTE_CODES.get(this.computation.replace('M', 'A'))
		if (value === undefined) throw new Error('Could not parse: ' + this.computation)
		return value
	}
	parseJump() {
		switch (this.jmpExpression) {
			case null:
				return 0
			case 'JGT':
				return JGT
			case 'JEQ':
				return JEQ
			case 'JGE':
				return JGT | JEQ
			case 'JLT':
				return JLT
			case 'JNE':
				return JLT | JGT
			case 'JLE':
				return JLT | JEQ
			case 'JMP':
				return JLT | JEQ | JGT
		}
	}
	toUint16() {
		return 1 << 15 |
			1 << 14 |
			1 << 13 |

			Number(this.computation.indexOf('M') !== -1) << 12 |
			this.parseCompute() << 6 |

			this.destinations.has('A') << 5 |
			this.destinations.has('D') << 4 |
			this.destinations.has('M') << 3 |

			this.parseJump()
	}
}
function pad16(str) {
	return '0'.repeat(16 - str.length) + str
}
const SYMBOL_MAP = new Map()
	.set('SP', 0)
	.set('LCL', 1)
	.set('ARG', 2)
	.set('THIS', 3)
	.set('THAT', 4)
	.set('SCREEN', 16384)
	.set('KBD', 24576)
for (let i = 0; i < ALLOCATED_REGISTERS; i++) SYMBOL_MAP.set('R' + String(i), i)

function getLines(stream, lineCallback, endCallback) {
	let residual = ''
	stream.on('data', chunk => {
		chunk = chunk.toString()
		let lastConsumed = 0
		for (let i = 0; i < chunk.length; i++) {
			if (chunk[i] === '\n') {
				lineCallback(residual + chunk.substring(lastConsumed, i))
				residual = ''
				lastConsumed = i + 1
			}
		}
		residual += chunk.substring(lastConsumed)
	})
	stream.on('end', () => {
		lineCallback(residual)
		endCallback()
	})
}

const ASM = '.asm'
const HACK = '.hack'
const file = process.argv[2]
let rootFile
if (file.endsWith(ASM)) rootFile = file.substring(0, file.length - ASM.length)
else rootFile = file
const inStream = fs.createReadStream(rootFile + ASM)
inStream.on('error', err => {
	throw new Error('Could not find file: ' + rootFile + ASM)
})
const instructions = []
getLines(inStream, line => {
	let parseState = AT_START
	let expression = null, jmpExpression = null
	for (let i = 0; i < line.length; i++) {
		const char = line[i]
		if (WHITESPACE.test(char)) continue
		if (char === '/' && line[i + 1] === '/') break
		switch (parseState) {
			case AT_START: {
				if (char === '@') {
					parseState = CONSTANT
					expression = ''
				}
				else if (char === '(') {
					parseState = LABEL
					expression = ''
				}
				else {
					parseState = COMPUTE
					expression = char
				}
				break
			}
			case CONSTANT: {
				expression += char
				break
			}
			case LABEL: {
				if (char === ')') parseState = AFTER_LABEL
				else expression += char
				break
			}
			case COMPUTE: {
				if (char === ';') {
					parseState = JMP_CODE
					jmpExpression = ''
				}
				else expression += char
				break
			}
			case JMP_CODE: {
				jmpExpression += char
			}
		}
	}
	switch (parseState) {
		case AT_START: {
			break
		}
		case CONSTANT: {
			instructions.push(new AInstruction(expression))
			break
		}
		case AFTER_LABEL: {
			SYMBOL_MAP.set(expression, instructions.length)
			break
		}
		case COMPUTE:
		case JMP_CODE: {
			instructions.push(new CInstruction({expression, jmpExpression}))
			break
		}
		default:
			throw new Error('Could not parse line: ' + line)
	}
}, () => {
	let firstUnusedRegister = ALLOCATED_REGISTERS
	for (const instruction of instructions) {
		if (instruction instanceof AInstruction) {
			if (instruction.resolveSymbol({SYMBOL_MAP, firstUnusedRegister})) firstUnusedRegister++
		}
	}

	const outStream = fs.createWriteStream(rootFile + HACK)
	for (const instruction of instructions) {
		const uint16 = instruction.toUint16()
		outStream.write(pad16(uint16.toString(2)))
		outStream.write('\n')
	}
	outStream.end()
})