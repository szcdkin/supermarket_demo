package demo.cashier;

import demo.buyer.OrderCreatedListener;
import demo.supermarket.CustomerOrder;

import java.util.List;

public class OrderCreatedHandler implements OrderCreatedListener {

    /**
     * 收银台列表
     */
    private List<Cashier> cashiers;

    /**
     * 收银台选择器
     */
    private CashierSelector cashierSelector;

    public OrderCreatedHandler(List<Cashier> cashiers, CashierSelector cashierSelector) {
        this.cashiers = cashiers;
        this.cashierSelector = cashierSelector;
    }

    public void created(CustomerOrder order) {
        Cashier cashier = this.cashierSelector.select(order, cashiers);
        cashier.addOrder(order);
    }
}
