package org.example.factory;

import org.example.entity.DiscountCard;
import org.example.entity.Purchase;

import java.util.List;

/**
 * Интерфейс формирования чека
 */
public interface CheckFactory {
    /**
     * Возвращает готовый чек в виде строки, готовой для печати, исользует методы CheckService
     * @param purchaseList список покупок
     * @param discountCard дисконтная карта клиента
     * @return чек
     */
    String getCheck(List<Purchase> purchaseList, DiscountCard discountCard);
}
