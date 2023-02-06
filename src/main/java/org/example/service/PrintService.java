package org.example.service;

import org.example.entity.DiscountCard;
import org.example.entity.Purchase;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class PrintService {
    final private int amountLength;
    final private int titleLength;
    final private int priceLength;
    final private int totalLength;
    final private int fullLineLength;
    final private double discountSize;
    final private String amountHeader;
    final private String titleHeader;
    final private String priceHeader;
    final private String totalHeader;
    final private String cashierName;
    final private String logo;

    public PrintService() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            Properties property = new Properties();
            property.load(fis);
            // считываем параметры, при их отсутствии заполняем значениями по умолчанию
            discountSize = Double.parseDouble(property.getProperty("discountSize", "0"));
            // считываем размеры каждой колонки и общую ширину чека
            amountLength = Integer.parseInt(property.getProperty("print.amountLength", "4"));
            titleLength = Integer.parseInt(property.getProperty("print.titleLength", "40"));
            priceLength = Integer.parseInt(property.getProperty("print.priceLength", "9"));
            totalLength = Integer.parseInt(property.getProperty("print.totalLength", "9"));
            fullLineLength = amountLength + titleLength + priceLength + totalLength + 3; // 3 = пробелы между колонками
            // считываем заголовки для столбцов
            amountHeader = property.getProperty("print.amountHeader", "QTY");
            titleHeader = property.getProperty("print.titleHeader", "Product");
            priceHeader = property.getProperty("print.priceHeader", "Price");
            totalHeader = property.getProperty("print.totalHeader", "Total");
            cashierName = property.getProperty("print.cashierName", "!!! N/A !!!");
            logo = property.getProperty("print.logo", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCheck(List<Purchase> purchaseList) {
        printCheck(purchaseList, null);
    }

    public void printCheck(List<Purchase> purchaseList, DiscountCard discountCard) {
        double totalSum = 0.0;
        double totalDiscount = 0.0;
        System.out.println(getHeader());
        for (Purchase p : purchaseList) {
            double discount = 0;
            double price = p.getAmount() * p.getProduct().getPrice();
            // расчитываем скидку, если продукт акционный
            if (p.getProduct().isPromo() && p.getAmount() > 5) {
                discount = discountSize;
                totalSum += price * (1 - discount);
                totalDiscount += price * discount;
                System.out.print(getPurchaseStringWithDiscount(p));
            } else {
                totalSum += price;
                System.out.print(getPurchaseStringWithoutDiscount(p));
            }
        }
    }

    private String getHeader() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dateStamp = dateTime.format(dateFormatter);
        String timeStamp = dateTime.format(timeFormatter);
        sb.append(logo);
        sb.append("Кассир: ").append(cashierName).append("\n");
        sb.append("Дата продажи: ").append(dateStamp).append("\n");
        sb.append("Время продажи: ").append(timeStamp).append("\n");
        sb.append(getLineOfSymbols('-', fullLineLength));
        sb.append(getPurchaseHeader());
        return sb.toString();
    }

    private String getPurchaseHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLengthString(amountHeader, amountLength)).append(" ");
        sb.append(formatLengthString(titleHeader, titleLength)).append(" ");
        sb.append(formatLengthString(priceHeader, priceLength, true)).append(" ");
        sb.append(formatLengthString(totalHeader, totalLength, true));
        return sb.toString();
    }

    private String getPurchaseStringWithoutDiscount(Purchase purchase) {
        StringBuilder sb = new StringBuilder();
        int amount = purchase.getAmount();
        String amountAsString = String.valueOf(amount);
        String title = purchase.getProduct().getTitle();
        double price = purchase.getProduct().getPrice();
        String priceAsString = String.valueOf(String.format("%.2f", price));
        double total = amount * price;
        String totalAsString = String.valueOf(String.format("%.2f", total));
        sb.append(formatLengthString(amountAsString, amountLength)).append(" ");
        sb.append(formatLengthString(title, titleLength)).append(" ");
        sb.append(formatLengthString(priceAsString, priceLength, true)).append(" ");
        sb.append(formatLengthString(totalAsString, totalLength, true));
        sb.append("\n");
        return sb.toString();
    }

    private String getPurchaseStringWithDiscount(Purchase purchase) {
        StringBuilder sb = new StringBuilder();
        int amount = purchase.getAmount();
        String amountAsString = String.valueOf(amount);
        String title = purchase.getProduct().getTitle();
        double price = purchase.getProduct().getPrice();
        String priceAsString = String.format("%.2f", price);
        double priceWithAmount = price * (1 - discountSize);
        String priceWithAmountAsString = String.format("%.2f", priceWithAmount);
        double total = amount * priceWithAmount;
        String totalAsString = String.format("%.2f", total);
        sb.append(formatLengthString(amountAsString, amountLength)).append(" ");
        sb.append(formatLengthString(title, titleLength)).append(" ");
        sb.append(formatLengthString(priceAsString, priceLength, true)).append("\n");
        sb.append(formatLengthString("Цена с учётом скидки: ",
                        amountLength + titleLength + 1, true)).append(" ");
        sb.append(formatLengthString(priceWithAmountAsString, priceLength, true)).append(" ");
        sb.append(formatLengthString(totalAsString, totalLength, true));
        sb.append("\n");
        return sb.toString();
    }

    private String formatLengthString(String input, int amountOfSymbols) {
        return formatLengthString(input, amountOfSymbols, false);
    }

    private String formatLengthString(String input, int amountOfSymbols, boolean alightToRight) {
        StringBuilder sb = new StringBuilder();
        if (input.length() < amountOfSymbols) {
            if (!alightToRight) {
                sb.append(input);
            }
            for (int i = 0; i < amountOfSymbols - input.length(); i++) {
                sb.append(" ");
            }
            if (alightToRight) {
                sb.append(input);
            }
        } else {
            sb.append(input.substring(0, amountOfSymbols));
        }
        return sb.toString();
    }

    private String getLineOfSymbols(char c, int count) {
        StringBuilder sb = new StringBuilder(count + 1);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        sb.append("\n");
        return sb.toString();
    }
}
