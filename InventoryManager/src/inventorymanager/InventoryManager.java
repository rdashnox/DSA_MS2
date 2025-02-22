/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package inventorymanager;

/**
 *
 * @author dashcodes
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class InventoryManager {
    private final List<InventoryItem> inventory;
    private final List<InventoryItem> originalInventory;
    private final String csvFilePath;
    private static final String CSV_HEADER = "DateEntered,StockLabel,Brand,EngineNumber,Status";
    private static final Logger LOGGER = Logger.getLogger(InventoryManager.class.getName());

    /**
     * Constructor with absolute path handling
     */
    
    public InventoryManager(String csvFilePath) {
        // Convert to absolute path if relative
        File file = new File(csvFilePath);
        this.csvFilePath = file.getAbsolutePath();
        this.inventory = new ArrayList<>();
        this.originalInventory = new ArrayList<>();
        setupLogger();
        LOGGER.info("Initialized with CSV path: " + this.csvFilePath);
    }
    
     /**
     * Sets up the logging system
     * Creates a file handler for persistent logging
     */
    private void setupLogger() {
        try {
            FileHandler fh = new FileHandler("inventory_manager.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Could not setup logger: " + e.getMessage());
        }
    }

    /**
     * Loads inventory with proper error handling and validation
     */
    public void loadInventory() {
        try {
            File file = new File(csvFilePath);
            if (!file.exists()) {
                // Create directory if it doesn't exist
                file.getParentFile().mkdirs();
                Files.write(file.toPath(), Collections.singletonList(CSV_HEADER));
                LOGGER.info("Created new inventory file: " + csvFilePath);
                return;
            }
            
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                Files.write(file.toPath(), Collections.singletonList(CSV_HEADER));
                LOGGER.info("Reinitialized empty inventory file with header");
                return;
            }

            if (!lines.get(0).equals(CSV_HEADER)) {
                LOGGER.warning("Invalid CSV header found. Reinitializing file.");
                lines.add(0, CSV_HEADER); // Add correct header
                Files.write(file.toPath(), lines);
            }
            
            // Clear existing lists before loading
            inventory.clear();
            originalInventory.clear();
            
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",");
                if (values.length == 5) {
                    InventoryItem item = new InventoryItem(
                        values[0].trim(), // dateEntered
                        values[1].trim(), // stockLabel
                        values[2].trim(), // brand
                        values[3].trim(), // engineNumber
                        values[4].trim()  // status
                    );
                    inventory.add(item);
                    originalInventory.add(new InventoryItem( // Create new instance for original
                        values[0].trim(),
                        values[1].trim(),
                        values[2].trim(),
                        values[3].trim(),
                        values[4].trim()
                    ));
                }
            }
            LOGGER.info("Loaded " + inventory.size() + " items from inventory");
        } catch (IOException e) {
            LOGGER.severe("Error loading inventory: " + e.getMessage());
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }
    
     /**
     * Checks if an engine number already exists in inventory
     * Case-insensitive comparison
     * @param engineNumber Engine number to check
     * @return true if engine number exists, false otherwise
     */
    public boolean isEngineNumberExists(String engineNumber) {
        return inventory.stream()
            .anyMatch(item -> item.getEngineNumber().equalsIgnoreCase(engineNumber));
    }

    /**
     * Adds a new item to inventory
     * Validates engine number uniqueness
     * Updates both current and original inventory
     * Updates CSV file
     * @param engineNumber Unique engine identifier
     * @param brand Manufacturer brand
     */
    
    public void addItem(String engineNumber, String brand) {
        try {
            // Reload inventory before adding to ensure we have latest data
            loadInventory();
            
            if (!isEngineNumberExists(engineNumber)) {
                InventoryItem newItem = new InventoryItem(brand, engineNumber);
                inventory.add(newItem);
                originalInventory.add(new InventoryItem(brand, engineNumber)); // Add to original list
                updateCsvFile();
                LOGGER.info("Added new item: " + engineNumber);
                System.out.println("Item added successfully.");
            } else {
                LOGGER.warning("Attempted to add duplicate engine number: " + engineNumber);
                System.out.println("Error: Engine number already exists!");
            }
        } catch (Exception e) {
            LOGGER.severe("Error adding item: " + e.getMessage());
            System.err.println("Error adding item: " + e.getMessage());
        }
    }
    
    /**
     * Deletes an item from inventory
     * Validates:
     * - Item exists
     * - Item is marked as 'Old'
     * - Item status is 'Sold'
     * Updates both lists and CSV file after deletion
     * @param engineNumber Engine number of item to delete
     */
    public void deleteItem(String engineNumber) {
        try {
            Optional<InventoryItem> itemToDelete = inventory.stream()
                .filter(item -> item.getEngineNumber().equalsIgnoreCase(engineNumber))
                .findFirst();

            if (itemToDelete.isPresent()) {
                InventoryItem item = itemToDelete.get();
                if (!"Old".equals(item.getStockLabel()) || !"Sold".equals(item.getStatus())) {
                    LOGGER.warning("Attempted to delete item that is not old and sold: " + engineNumber);
                    System.out.println("Error: Cannot delete item. Item must have StockLabel 'Old' and Status 'Sold'");
                    return;
                }
                
                inventory.remove(item);
                originalInventory.remove(item);
                updateCsvFile();
                LOGGER.info("Deleted item: " + engineNumber);
                System.out.println("Item deleted successfully.");
            } else {
                LOGGER.warning("Attempted to delete non-existent item: " + engineNumber);
                System.out.println("Error: Engine number not found.");
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting item: " + e.getMessage());
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }    

    /**
     * Updates CSV with proper error handling and backup
     */
    private void updateCsvFile() {
        try {
            // Create backup of existing file
            File originalFile = new File(csvFilePath);
            if (originalFile.exists()) {
                File backupFile = new File(csvFilePath + ".bak");
                Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Write new content
            List<String> lines = new ArrayList<>();
            lines.add(CSV_HEADER);
            for (InventoryItem item : inventory) {
                lines.add(item.toString());
            }

            // Use atomic write operation
            Path tempFile = Files.createTempFile("inventory", ".tmp");
            Files.write(tempFile, lines);
            Files.move(tempFile, Paths.get(csvFilePath), StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("Updated CSV file successfully");
        } catch (IOException e) {
            LOGGER.severe("Error updating CSV file: " + e.getMessage());
            System.err.println("Error updating CSV file: " + e.getMessage());
            
            // Attempt to restore from backup
            try {
                File backupFile = new File(csvFilePath + ".bak");
                if (backupFile.exists()) {
                    Files.copy(backupFile.toPath(), Paths.get(csvFilePath), StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("Restored from backup file");
                }
            } catch (IOException restoreError) {
                LOGGER.severe("Failed to restore from backup: " + restoreError.getMessage());
            }
        }
    }

    public void sortItemsByBrand() {
        inventory.sort(Comparator.comparing(InventoryItem::getBrand));
        LOGGER.info("Sorted inventory by brand");
    }
    
    public void resetToOriginalOrder() {
        inventory.clear();
        inventory.addAll(originalInventory);
        LOGGER.info("Reset inventory to original order");
    }

    public InventoryItem searchItem(String engineNumber) {
        return inventory.stream()
            .filter(item -> item.getEngineNumber().equalsIgnoreCase(engineNumber))
            .findFirst()
            .orElse(null);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        System.out.println(CSV_HEADER);
        inventory.forEach(System.out::println);
    }
}