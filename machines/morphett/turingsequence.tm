; This is a translation of a program given by Alan Turing in his 1936
; paper 'On computable numbers, with an application to the Entscheidungsproblem'
;
; The program writes the digits of the sequence 0010110111011110... in the even cells of the tape.
; Run it with a blank initial tape.
; Note: This program does not halt.


; Start in state 'b'
0 * * * b0

; Turing's state 'b'
b0 * e r b1
b1 * e r b2
b2 * 0 r b3
b3 * * r b4
b4 * 0 l b5
b5 * * l o0

; Turing's state 'o'
o0 1 * r o1
o1 * x l o2
o2 * * l o3
o3 * * l o0
o0 0 0 * q0

; Turing's state 'q'
q0 _ 1 l p0
q0 * * r q1
q1 * * r q0

; Turing's state 'p'
p0 x _ r q0
p0 e * r f0
p0 _ * l p1
p1 * * l p0

; Turing's state 'f'
f0 _ 0 l f1
f1 * * l o0
f0 * * r f2
f2 * * r f0


; Run with a blank initial tape: $INITIAL_TAPE: 
