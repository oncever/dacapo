#!/usr/local/bin/python
# http://www.bagley.org/~doug/shootout/

import sys

class Toggle:
    def __init__(self, start_state):
        self.bool = start_state
    def value(self):
        return(self.bool)
    def activate(self):
        self.bool = not self.bool
        return(self)

class NthToggle(Toggle):
    def __init__(self, start_state, max_counter):
        Toggle.__init__(self, start_state)
        self.count_max = max_counter
        self.counter = 0
    def activate(self):
        self.counter += 1
        if (self.counter >= self.count_max):
            self.bool = not self.bool
            self.counter = 0
        return(self)


def main():
    NUM = int(sys.argv[1])
    if NUM < 1:
        NUM = 1

    toggle = Toggle(1)
    for i in xrange(0,5):
        if toggle.activate().value():
            print "true"
        else:
            print "false"
    for i in xrange(0,NUM):
        toggle = Toggle(1)

    print ""

    ntoggle = NthToggle(1, 3)
    for i in xrange(0,8):
        if ntoggle.activate().value():
            print "true"
        else:
            print "false"
    for i in xrange(0,NUM):
        ntoggle = NthToggle(1, 3)

main()
