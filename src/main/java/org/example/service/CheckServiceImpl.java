package org.example.service;

import org.example.entity.DiscountCard;
import org.example.entity.Purchase;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс для формирования чеков
 */
public class CheckServiceImpl implements CheckService {
    private int amountLength;
    private int titleLength;
    private int priceLength;
    private int totalLength;
    private int fullLineLength;
    private double discountSize;
    private double discountSizeByCard;
    private String amountHeader;
    private String titleHeader;
    private String priceHeader;
    private String totalHeader;
    private String cashierName;
    private String logo;

    public CheckServiceImpl() {
        init();
    }

    /**
     * Возвращает заголовок чека в виде строки. Заголовок состоит из логотипа, информации о кассире,
     * даты и времени продажи
     * @return возвращает заголовок чека в виде строки
     */
    @Override
    public String getHeaderInfoSection() {
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
        return sb.toString();
    }

    /**
     * Возвращает строку с заголовками
     * @return строка с заголовками
     */
    @Override
    public String getPurchaseHeaderSection() {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLengthString(amountHeader, amountLength)).append(" ");
        sb.append(formatLengthString(titleHeader, titleLength)).append(" ");
        sb.append(formatLengthString(priceHeader, priceLength, true)).append(" ");
        sb.append(formatLengthString(totalHeader, totalLength, true));
        return sb.toString();
    }

    /**
     * Возвращает содержимое секци с покупками и итогом
     * @param purchaseList список покупок
     * @param discountCard скидочная карта, при отсутствии карты null
     * @return содержимое секци с покупками и итогом
     */
    @Override
    public List<String> getPurchasesCheckSection(List<Purchase> purchaseList, DiscountCard discountCard) {
        List<String> list = new ArrayList<>();
        double totalSum = 0.0;
        double byCardDiscount = 0.0;
        for (Purchase p : purchaseList) {
            double discount;
            double price = p.getAmount() * p.getProduct().getPrice();
            if (p.getProduct().isPromo() && p.getAmount() > 5) {
                // расчитываем скидку, если товар акционный
                discount = discountSize;
                totalSum += price * (1 - discount);
                list.add(getPurchaseStringWithDiscount(p));
            } else {
                totalSum += price;
                list.add(getPurchaseStringWithoutDiscount(p));
                // если товар не акционный, то делаем скидку по карте покупателя (скидки не суммируются)
                if (discountCard != null) {
                    byCardDiscount += price * discountSizeByCard;
                }
            }
        }
        list.add(getLineOfSymbols('=', fullLineLength));
        if (discountCard == null) {
            list.add(formatLengthString("Итого: " + String.format("%.2f", totalSum),
                    fullLineLength, true));
        } else {
            list.add(formatLengthString("Карта покупателя номер " + discountCard.getNumber(),
                    fullLineLength, true));
            list.add(formatLengthString("Промежуточный итог: " + String.format("%.2f", totalSum),
                    fullLineLength, true));
            list.add(formatLengthString("Скидка по карте: " + String.format("%.2f", byCardDiscount),
                    fullLineLength, true));
            list.add(formatLengthString("Итого: " + String.format("%.2f", totalSum - byCardDiscount),
                    fullLineLength, true));
        }
        return list;
    }

    /**
     * Возвращает строку с информацией о товаре без акции. Строка содержит количество,
     * наименование товара, цену, итог
     * @param purchase покупка
     * @return строка с информацией о покупке
     */
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
        return sb.toString();
    }

    /**
     * Возвращает две строки информации о товаре по акции, первая строка содержит количество, наименование
     * товара и цену без скидки, вторая строка - фразу "Цена с учетом скидки:", цену, итог
     * @param purchase покупка
     * @return строка с информацией о покупке
     */
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
        return sb.toString();
    }

    /**
     * Преобразует строку к определённой длине, обрезая её или дополняя пробелами до нужной длины,
     * выравнивает текст по левому краю, для этого передаёт исходные параматры в метод
     * formatLengthString(String input, int amountOfSymbols, boolean alightToRight), устанавливая
     * параметр alightToRight в false
     * @param input исходная строка
     * @param amountOfSymbols длина итоговой строки
     * @return итоговая строка
     */
    private String formatLengthString(String input, int amountOfSymbols) {
        return formatLengthString(input, amountOfSymbols, false);
    }

    /**
     * Преобразует строку к определённой длине, обрезая её или дополняя пробелами до нужной длины
     * Также позволяет добавить выравнивание пробелами по левому или правому краю
     * @param input исходная строка
     * @param amountOfSymbols длина итоговой строки
     * @param alightToRight true, если требуется выравнивание текста по правому краю
     * @return итоговая строка
     */
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
            sb.append(input.substring(0, amountOfSymbols)); // обрезаем строку в размер
        }
        return sb.toString();
    }

    /**
     * Возврщает строку символов
     * @param c символ, которым нужно заполнить строку
     * @param count необходимая длина строки
     * @return строка заданной длины, состоящая из заданных символов
     */
    private String getLineOfSymbols(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }

    /**
     * Читает настройки из файла config.properties инициализирует ими соответствующие параметры
     */
    private void init() {
        try (FileInputStream propertiesFile = new FileInputStream("src/main/resources/config.properties")) {
            Properties property = new Properties();
            property.load(propertiesFile);
            // считываем параметры, при их отсутствии заполняем значениями по умолчанию
            discountSize = Double.parseDouble(property.getProperty("discountSize", "0"));
            discountSizeByCard = Double.parseDouble(property.getProperty("discountSizeByCard", "0"));
            // считываем размеры каждой колонки и вычисояем общую ширину чека
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
}
