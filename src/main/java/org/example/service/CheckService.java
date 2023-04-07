package org.example.service;

import org.example.entity.DiscountCard;
import org.example.entity.Purchase;

import java.util.List;

/**
 * Интерфейс для формирования чеков
 */
public interface CheckService {
    /**
     * Возвращает заголовок чека в виде строки. Заголовок состоит из логотипа, информации о кассире,
     * даты и времени продажи
     * @return возвращает заголовок чека в виде строки
     */
    String getHeaderInfoSection();

    /**
     * Возвращает строку с заголовками
     * @return строка с заголовками
     */
    String getPurchaseHeaderSection();

    /**
     * Возвращает содержимое секци с покупками и итогом
     * @param purchaseList список покупок
     * @param discountCard скидочная карта, при отсутствии карты null
     * @return содержимое секци с покупками и итогом
     */
    List<String> getPurchasesCheckSection(List<Purchase> purchaseList, DiscountCard discountCard);
}
