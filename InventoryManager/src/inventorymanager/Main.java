/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package inventorymanager;

/**
 *
 * @author dashcodes
 */

import java.util.Scanner;

public class Main {
        
    /**
     /**
     * Main entry point of the application
     * Handles:
     * - Menu display and navigation
     * - User input processing
     * - Interaction with InventoryManager
     * - Input validation
     * @param args Command line arguments (not used)
     */
    
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager("inventory_data.csv");
        manager.loadInventory();
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nMotorPH Inventory Management System");
                System.out.println("1. Display Inventory");
                System.out.println("2. Add Item");
                System.out.println("3. Delete Item");
                System.out.println("4. Sort Items by Brand");
                System.out.println("5. Search Item");
                System.out.println("6. Reset to Original Order");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                    continue;
                }
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1 -> manager.displayInventory();
                    case 2 -> {
                        System.out.print("Enter engine number: ");
                        String engineNumber = scanner.nextLine().trim();
                        
                        if (engineNumber.isEmpty()) {
                            System.out.println("Error: Engine number cannot be empty!");
                            continue;
                        }
                        
                        if (!manager.isEngineNumberExists(engineNumber)) {
                            System.out.print("Enter brand: ");
                            String brand = scanner.nextLine().trim();
                            
                            if (brand.isEmpty()) {
                                System.out.println("Error: Brand cannot be empty!");
                                continue;
                            }
                            
                            manager.addItem(engineNumber, brand);
                        } else {
                            System.out.println("Error: Engine number already exists!");
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter engine number to delete: ");
                        String engineToDelete = scanner.nextLine().trim();
                        
                        if (engineToDelete.isEmpty()) {
                            System.out.println("Error: Engine number cannot be empty!");
                            continue;
                        }
                        
                        System.out.print("Are you sure you want to delete this item? (y/n): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();
                        
                        if (confirm.equals("y")) {
                            manager.deleteItem(engineToDelete);
                        } else {
                            System.out.println("Delete operation cancelled.");
                        }
                    }
                    case 4 -> {
                        manager.sortItemsByBrand();
                        System.out.println("Items sorted by brand.");
                        manager.displayInventory();
                    }
                    case 5 -> {
                        System.out.print("Enter engine number to search: ");
                        String engineToSearch = scanner.nextLine().trim();
                        
                        if (engineToSearch.isEmpty()) {
                            System.out.println("Error: Engine number cannot be empty!");
                            continue;
                        }
                        
                        InventoryItem foundItem = manager.searchItem(engineToSearch);
                        if (foundItem != null) {
                            System.out.println("\nItem Found:");
                            System.out.println(foundItem.getDetailedString());
                        } else {
                            System.out.println("Item not found.");
                        }
                    }
                    case 6 -> {
                        manager.resetToOriginalOrder();
                        System.out.println("Inventory reset to original order.");
                        manager.displayInventory();
                    }
                    case 0 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}