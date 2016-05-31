package demo.supermarket;

import demo.supermarket.exception.InventoryShortageException;
import demo.supermarket.exception.OrderHasFinishedException;
import junit.framework.Assert;
import org.testng.annotations.Test;

public class PayOrderTest {

    @Test(expectedExceptions = InventoryShortageException.class)
    public void test_InventoryShortage() throws Exception {
        Supermarket supermarket = new Supermarket();

        Goods goods = supermarket.getRandomGoods();
        goods.getInventory().set(0);

        CustomerOrder order = new CustomerOrder();
        order.setGoods(goods);
        order.setStatus(CustomerOrder.ORDER_CREATED);

        supermarket.payOrder(order);
    }

    @Test(expectedExceptions = OrderHasFinishedException.class)
    public void test_OrderHasFinished() throws Exception {
        Supermarket supermarket = new Supermarket();

        Goods goods = supermarket.getRandomGoods();

        CustomerOrder order = new CustomerOrder();
        order.setGoods(goods);
        order.setStatus(CustomerOrder.ORDER_FINISHED);

        supermarket.payOrder(order);
    }

    @Test
    public void test_normal() throws Exception {
        Supermarket supermarket = new Supermarket();

        Goods goods = supermarket.getRandomGoods();

        CustomerOrder order = new CustomerOrder();
        order.setGoods(goods);
        order.setCreated(System.currentTimeMillis());
        order.setStatus(CustomerOrder.ORDER_CREATED);

        supermarket.payOrder(order);

        Assert.assertTrue(order.getStatus() == CustomerOrder.ORDER_FINISHED);
    }
}
