package demo.cashier;

import demo.supermarket.CustomerOrder;

import java.util.List;

/**
 * 收银台选择器
 */
public interface CashierSelector {

    Cashier select(CustomerOrder order, List<Cashier> cashiers);
}
