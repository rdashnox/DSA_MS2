/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package inventorymanager;

/**
 *
 * @author dashcodes
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single item in the inventory
 * Handles all item-specific data and operations
 */

public class InventoryItem {
     private final String dateEntered;
    private String stockLabel;
    private final String brand;
    private final String engineNumber;
    private String status;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Constructor for new inventory items
     * Automatically sets:
     * - Current date as dateEntered
     * - "New" as stockLabel
     * - "On-hand" as status
     * - Converts brand and engineNumber to uppercase
     *
     * @param brand The manufacturer brand
     * @param engineNumber Unique engine identifier
     */
    public InventoryItem(String brand, String engineNumber) {
        this.dateEntered = LocalDate.now().format(DATE_FORMATTER);
        this.stockLabel = "New";
        this.brand = brand.toUpperCase();
        this.engineNumber = engineNumber.toUpperCase();
        this.status = "On-hand";
    }
    
    /**
     * Constructor for existing inventory items (loaded from CSV)
     * Used when recreating items from stored data
     * 
     * @param dateEntered Original entry date
     * @param stockLabel Current stock label
     * @param brand Manufacturer brand
     * @param engineNumber Unique engine identifier
     * @param status Current status
     */
    public InventoryItem(String dateEntered, String stockLabel, String brand, 
                        String engineNumber, String status) {
        this.dateEntered = dateEntered;
        this.stockLabel = stockLabel;
        this.brand = brand.toUpperCase();
        this.engineNumber = engineNumber.toUpperCase();
        this.status = status;
    }

    // Getters
    public String getDateEntered() { return dateEntered; }
    public String getStockLabel() { return stockLabel; }
    public String getBrand() { return brand; }
    public String getEngineNumber() { return engineNumber; }
    public String getStatus() { return status; }

    // Setters
    public void setStockLabel(String stockLabel) { this.stockLabel = stockLabel; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Creates a CSV-formatted string representation of the item
     * @return Comma-separated values string
     */
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", 
            dateEntered, stockLabel, brand, engineNumber, status);
    }
    
    /**
     * Creates a detailed, human-readable string representation of the item
     * @return Formatted string with labels and values
     */
    public String getDetailedString() {
        return String.format("""
            Date Entered: %s
            Stock Label: %s
            Brand: %s
            Engine Number: %s
            Status: %s""", 
            dateEntered, stockLabel, brand, engineNumber, status);
    }
}