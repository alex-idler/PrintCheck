package org.example.service;

import org.example.entity.Purchase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrintService {
    private int lineLength = 32;
    private String cashier = "Иванова А.Б.";
    private String logo = """
                |**********************************|
                |      Магазин "У тёти Сони"       |
                |	     Добро пожаловать!         |
                |  	                               |
                """;

    public void printCheck(List<Purchase> purchaseList) {
        System.out.println(makeHeader());
        for (Purchase p : purchaseList) {
            System.out.print(makePurchaseString(p));
        }
    }

    private String makeHeader() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dateStamp = dateTime.format(dateFormatter);
        String timeStamp = dateTime.format(timeFormatter);
        sb.append(logo);
        sb.append(makeString("Кассир: ", cashier));
        sb.append(makeString("Дата продажи: ", dateStamp));
        sb.append(makeString("Время продажи: ", timeStamp));
        sb.append("|----------------------------------|");
        return sb.toString();
    }

    private String makeString(String... input) {
        StringBuilder sb = new StringBuilder();
        int stringSize = 0;
        sb.append("| ");
        for (String s : input) {
            sb.append(s);
            stringSize += s.length();
        }
        for (int i = 0; i < lineLength - stringSize; i++) {
            sb.append(" ");
        }
        sb.append(" |\n");
        return sb.toString();
    }

    private String makePurchaseString(Purchase purchase) {
        StringBuilder sb = new StringBuilder();
        String amount = String.valueOf(purchase.getAmount());
        String title = purchase.getProduct().getTitle();
        String price = String.valueOf(purchase.getProduct().getPrice());
        sb.append("| ");
        sb.append(amount).append(" ");
        sb.append(title).append(" ");
        sb.append(price);
        sb.append(" |\n");
        return sb.toString();
    }
}
