; Binary addition - adds two binary numbers
; Input: two binary numbers, separated by a single space, eg '100 1110'

0 _ _ r 1
0 * * r 0
1 _ _ l 2
1 * * r 1
2 0 _ l 3x
2 1 _ l 3y
2 _ _ l 7
3x _ _ l 4x
3x * * l 3x
3y _ _ l 4y
3y * * l 3y
4x 0 x r 0
4x 1 y r 0
4x _ x r 0
4x * * l 4x    ; skip the x/y's
4y 0 1 * 5
4y 1 0 l 4y
4y _ 1 * 5
4y * * l 4y    ; skip the x/y's
5 x x l 6
5 y y l 6
5 _ _ l 6
5 * * r 5
6 0 x r 0
6 1 y r 0

7 x 0 l 7
7 y 1 l 7
7 _ _ r halt
7 * * l 7


; This is a hack to load an appropriate initial tape. $INITIAL_TAPE: 110110 101011
