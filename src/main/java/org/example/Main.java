package org.example;

public class Main {

    public static void main(String[] args) {
        new Main().test();
    }



    public void test() {
        Branch.begin(this::test);

        System.out.println("Every");

        if (Branch.diverge()) {
            System.out.println("First");
        } else {
            System.out.println("Second");
        }
    }
}