package org.example.service;

import org.example.entity.Purchase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrintService {
    final private int lineLength = 32;
    final private int amountLength = 3;     //todo вынести в enum
    final private int titleLength = 32;
    final private int priceLength = 7;
    final private int totalLength = 10;
    final private int fullLineLength = amountLength + titleLength + priceLength + totalLength + 3;
    final private String amountHeader = "Количество";
    final private String titleHeader = "Наименование";
    final private String priceHeader = "Цена";
    final private String totalHeader = "Итого";
    final private String cashier = "Иванова А.Б.";
    final private String logo = """
                ***************************
                *  Магазин "У тёти Сони"  *
                *  Добро пожаловать!      *
                ***************************
                """;

    public void printCheck(List<Purchase> purchaseList) {
        System.out.println(getHeader());
        for (Purchase p : purchaseList) {
            System.out.print(getPurchaseString(p));
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
        sb.append(getString("Кассир: ", cashier));
        sb.append(getString("Дата продажи: ", dateStamp));
        sb.append(getString("Время продажи: ", timeStamp));
        sb.append(getLineOfSymbols('-', fullLineLength));
        sb.append(getPurchaseHeader());
        return sb.toString();
    }

    private String getString(String... input) {
        StringBuilder sb = new StringBuilder();
        int stringSize = 0;
        for (String s : input) {
            sb.append(s);
            stringSize += s.length();
        }
        for (int i = 0; i < lineLength - stringSize; i++) {
            sb.append(" ");
        }
        sb.append("\n");
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

    private String getPurchaseString(Purchase purchase) {
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
