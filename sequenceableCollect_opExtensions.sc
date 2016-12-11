+SequenceableCollection {
	// extended unary ops
	cpsstr {arg operand=0, adverb; ^this.performBinaryOp(\cpsstr,operand,adverb)}
	cpsstr48 {arg operand=0, adverb; ^this.performBinaryOp(\cpsstr48,operand,adverb)}
	cpsstr36 {arg operand=0, adverb; ^this.performBinaryOp(\cpsstr36,operand,adverb)}
	midistr {arg operand=0, adverb; ^this.performBinaryOp(\midistr,operand,adverb)}
	midistr48 {arg operand=0, adverb; ^this.performBinaryOp(\midistr48,operand,adverb)}
	midistr36 {arg operand=0, adverb; ^this.performBinaryOp(\midistr36,operand,adverb)}

	strcps { ^this.performUnaryOp(\strcps)}
	strmidi { ^this.performUnaryOp(\strmidi)}

	round12tet { ^this.performUnaryOp(\round12tet)}
	round24tet { ^this.performUnaryOp(\round24tet)}
	roundNtet {arg operand, adverb; ^this.performBinaryOp(\roundNtet, operand, adverb)}
}