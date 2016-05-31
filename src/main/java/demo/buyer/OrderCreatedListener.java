package demo.buyer;

import demo.supermarket.CustomerOrder;

/**
 * 订单创建监听器
 */
public interface OrderCreatedListener {

    void created(CustomerOrder order);

}
