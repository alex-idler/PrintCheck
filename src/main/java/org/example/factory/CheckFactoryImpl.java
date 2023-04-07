package org.example.factory;

import org.example.entity.DiscountCard;
import org.example.entity.Purchase;
import org.example.service.CheckService;
import org.example.service.CheckServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс формирования чека
 */
public class CheckFactoryImpl implements CheckFactory {
    private final CheckService checkService;

    public CheckFactoryImpl() {
        checkService = new CheckServiceImpl();
    }

    /**
     * Возвращает готовый чек в виде строки, готовой для печати, исользует методы CheckService
     * @param purchaseList список покупок
     * @param discountCard дисконтная карта клиента
     * @return чек
     */
    @Override
    public String getCheck(List<Purchase> purchaseList, DiscountCard discountCard) {
        List<String> check = new ArrayList<>();
        check.add(checkService.getHeaderInfoSection());
        check.add(checkService.getPurchaseHeaderSection());
        check.addAll(checkService.getPurchasesCheckSection(purchaseList, discountCard));
        StringBuilder sb = new StringBuilder();
        for (String str : check) {
            sb.append(str);
            sb.append("\n");
        }
        return sb.toString();
    }
}
