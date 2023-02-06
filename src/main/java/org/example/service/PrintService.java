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
    final private double discountSizeByCard;
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
            discountSizeByCard = Double.parseDouble(property.getProperty("discountSizeByCard", "0"));
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
        StringBuilder sb = new StringBuilder();
        double totalSum = 0.0;
        double byCardDiscount = 0.0;
        sb.append(getHeader());
        for (Purchase p : purchaseList) {
            double discount;
            double price = p.getAmount() * p.getProduct().getPrice();
            if (p.getProduct().isPromo() && p.getAmount() > 5) {
                // расчитываем скидку, если товар акционный
                discount = discountSize;
                totalSum += price * (1 - discount);
                sb.append(getPurchaseStringWithDiscount(p));
            } else {
                totalSum += price;
                sb.append(getPurchaseStringWithoutDiscount(p));
                // если товар не акционный, то делаем скидку по карте покупателя (скидки не суммируются)
                if (discountCard != null) {
                    byCardDiscount += price * discountSizeByCard;
                }
            }
        }
        sb.append(getLineOfSymbols('=', fullLineLength));
        if (discountCard != null) {
            sb.append(formatLengthString("Карта покупателя номер " + discountCard.getNumber(),
                    fullLineLength, true));
            sb.append("\n");
            sb.append(formatLengthString("Промежуточный итог: " + String.format("%.2f", totalSum),
                    fullLineLength, true));
            sb.append("\n");
            sb.append(formatLengthString("Скидка по карте: " + String.format("%.2f", byCardDiscount),
                    fullLineLength, true));
            sb.append("\n");
            sb.append(formatLengthString("Итого: " + String.format("%.2f", totalSum - byCardDiscount),
                    fullLineLength, true));
            sb.append("\n");
        } else {
            sb.append(formatLengthString("Итого: " + String.format("%.2f", totalSum),
                    fullLineLength, true));
            sb.append("\n");
        }
        System.out.println(sb);
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
        sb.append(formatLengthString(totalHeader, totalLength, true)).append("\n");
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
        sb.append(formatLengthString("Цена с учётом скидки:",
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
            sb.append(" ".repeat(amountOfSymbols - input.length()));
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
        sb.append(String.valueOf(c).repeat(Math.max(0, count)));
        sb.append("\n");
        return sb.toString();
    }
}
