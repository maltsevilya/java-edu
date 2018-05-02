package com.becomehavasenior.oop;

import java.util.Random;

public class A {

    {
        init();
    }

    private A() throws Exception {

    }

    private void init() throws Exception {
        if (new Random().nextBoolean()) {
            throw new Exception();
        }
    }
}
