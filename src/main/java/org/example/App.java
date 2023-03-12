package org.example;

import org.example.entity.DiscountCard;
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
    private static List<DiscountCard> discountCardList;

    public static void main( String[] args ) {
        initialize();
        PrintService printService = new PrintService();
        printService.printCheck(purchaseList);
        printService.printCheck(purchaseList, discountCardList.get(0));
        // asdsad
    }

    private static void initialize() {
        purchaseList = new ArrayList<>();
        purchaseList.add(new Purchase(new Product("Молоко 1л", 82.30, false), 3));
        purchaseList.add(new Purchase(new Product("Продукт с очень-очень длинным названием", 82.30, false), 4));
        purchaseList.add(new Purchase(new Product("Кофе растворимый 300г", 452.10, true), 6));
        purchaseList.add(new Purchase(new Product("Coca-Cola 2л", 99.90, true), 1));
        purchaseList.add(new Purchase(new Product("Йогурт со вкусом клубники", 29.50, true), 12));
        purchaseList.add(new Purchase(new Product("Сахарный песок 1кг", 70.20, false), 5));
        purchaseList.add(new Purchase(new Product("Пакет", 5.00, false), 1));

        discountCardList = new ArrayList<>();
        discountCardList.add(new DiscountCard(1234556));
        discountCardList.add(new DiscountCard(76543));
    }
}
