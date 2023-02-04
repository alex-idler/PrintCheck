package org.example;

import org.example.entity.Product;
import org.example.entity.Purchase;
import org.example.service.PrintService;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    private static List<Purchase> purchaseList;

    public static void main( String[] args ) {
        initialize();
        PrintService printService = new PrintService();
        printService.printCheck(purchaseList);
    }

    private static void initialize() {
        purchaseList = new ArrayList<>();
        purchaseList.add(new Purchase(new Product("Молоко 1л", 82.30), 3));
        purchaseList.add(new Purchase(new Product("Продукт с очень-очень длинным названием", 82.30), 4));
        purchaseList.add(new Purchase(new Product("Кофе растворимый 300г", 452.10), 6));
        purchaseList.add(new Purchase(new Product("Coca-Cola 2л", 99.90), 1));
        purchaseList.add(new Purchase(new Product("Йогурт со вкусом клубники", 29.50), 12));
        purchaseList.add(new Purchase(new Product("Сахарный песок 1кг", 70.20), 5));
        purchaseList.add(new Purchase(new Product("Пакет", 5.00), 1));
    }
}
