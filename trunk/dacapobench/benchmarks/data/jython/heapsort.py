#!/usr/local/bin/python -O
# $Id: heapsort.py,v 1.1 2004/09/16 17:14:58 hoffmann Exp $
# http://www.bagley.org/~doug/shootout/

import sys

IM = 139968
IA =   3877
IC =  29573

LAST = 42
def gen_random(max):
    global LAST
    LAST = (LAST * IA + IC) % IM
    return( (max * LAST) / IM )

def heapsort(n, ra):
    rra = i = j = 0
    l = (n >> 1) + 1
    ir = n

    while (1):
        if (l > 1):
            l -= 1
            rra = ra[l]
        else:
            rra = ra[ir]
            ra[ir] = ra[1]
            ir -= 1
            if (ir == 1):
                ra[1] = rra
                return
        i = l
        j = l << 1
        while (j <= ir):
            if ((j < ir) and (ra[j] < ra[j+1])):
                j += 1
            if (rra < ra[j]):
                ra[i] = ra[j]
                i = j
                j += i
            else:
                j = ir + 1
        ra[i] = rra

def main():
    N = int(sys.argv[1])
    if N < 1:
        N = 1

    ary = range(N+1)
    for i in xrange(1,N+1):
        ary[i] = gen_random(1.0)

    heapsort(N, ary)

    print "%.10f" % ary[N]

main()
