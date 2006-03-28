#!/usr/local/bin/python
# $Id: matrix.py,v 1.1 2004/09/16 17:14:58 hoffmann Exp $
# http://www.bagley.org/~doug/shootout/

# This program based on the original from:
# "The What, Why, Who, and Where of Python" By Aaron R. Watters
# http://www.networkcomputing.com/unixworld/tutorial/005/005.html

# modified to pass rows and cols, and avoid matrix size checks
# and added one optimization to reduce subscripted references in
# inner loop.

import sys

size = 30

def mkmatrix(rows, cols):
    count = 1
    mx = [ None ] * rows
    for i in range(rows):
        mx[i] = [0] * cols
        for j in range(cols):
            mx[i][j] = count
            count += 1
    return mx

def mmult(rows, cols, m1, m2):
    m3 = [ None ] * rows
    for i in range( rows ):
        m3[i] = [0] * cols
        for j in range( cols ):
            val = 0
            for k in range( cols ):
                val += m1[i][k] * m2[k][j]
            m3[i][j] = val
    return m3

def mxprint(m):
    for i in range(size):
        for j in range(size):
            print m[i][j],
        print ""

def main():
    iter = int(sys.argv[1])
    m1 = mkmatrix(size, size)
    m2 = mkmatrix(size, size)
    for i in xrange(iter):
        mm = mmult(size, size, m1, m2)
    print mm[0][0], mm[2][3], mm[3][2], mm[4][4]

main()
