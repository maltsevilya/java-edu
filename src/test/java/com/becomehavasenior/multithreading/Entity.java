package com.becomehavasenior.multithreading;

class Entity {
    private synchronized void method1(Entity entity) {
        entity.method2(this);
    }

    private synchronized void method2(Entity entity) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Entity en1 = new Entity();
            Entity en2 = new Entity();
            new Thread(() -> en1.method1(en2)).start();
            new Thread(() -> en2.method1(en1)).start();
        }
    }
}