package com.example.app;

/**
 * Simple Hello World Java Application for Docker Demo
 */
public class HelloWorld {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Docker Multistage Build Demo");
        System.out.println("===========================================");
        
        // Display system information
        displaySystemInfo();
        
        // Run demo calculations
        runDemoCalculations();
        
        // Display completion message
        System.out.println("\n===========================================");
        System.out.println("   Application completed successfully!");
        System.out.println("===========================================");
    }
    
    /**
     * Display system information
     */
    private static void displaySystemInfo() {
        System.out.println("\n[System Information]");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Architecture: " + System.getProperty("os.arch"));
        System.out.println("User: " + System.getProperty("user.name"));
    }
    
    /**
     * Run demo calculations
     */
    private static void runDemoCalculations() {
        System.out.println("\n[Demo Calculations]");
        
        int num1 = 42;
        int num2 = 58;
        
        System.out.println("Number 1: " + num1);
        System.out.println("Number 2: " + num2);
        System.out.println("Sum: " + add(num1, num2));
        System.out.println("Product: " + multiply(num1, num2));
        System.out.println("Fibonacci(10): " + fibonacci(10));
        System.out.println("Factorial(5): " + factorial(5));
    }
    
    /**
     * Add two numbers
     */
    public static int add(int a, int b) {
        return a + b;
    }
    
    /**
     * Multiply two numbers
     */
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    /**
     * Calculate Fibonacci number
     */
    public static int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    /**
     * Calculate factorial
     */
    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}
