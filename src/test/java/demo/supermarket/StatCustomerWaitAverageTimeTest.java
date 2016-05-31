package demo.supermarket;

import junit.framework.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StatCustomerWaitAverageTimeTest {

    private Supermarket supermarket;

    @BeforeMethod
    public void before() {
        supermarket = new Supermarket();

        int orderId = 1;

        CustomerOrder order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(System.currentTimeMillis() + 5000);
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(System.currentTimeMillis() + 6000);
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(System.currentTimeMillis() + 9000);
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(System.currentTimeMillis() - 2000);
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(System.currentTimeMillis() - 7000);
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setCreated(System.currentTimeMillis());
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        order.setFinished(System.currentTimeMillis());
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        order.setStatus(CustomerOrder.ORDER_FINISHED);
        order.setGoods(supermarket.getRandomGoods());
        order.setOrderId(orderId++);
        supermarket.insertOrder(order);
    }

    @AfterMethod
    public void after() {
        this.supermarket = null;
    }

    @Test
    public void test() {
        long totalWait = 0;
        int totalCount = 0;
        for (CustomerOrder order : this.supermarket.getOrders()) {
            if (order.getCreated() > 0 && order.getFinished() > 0 && order.getFinished() > order.getCreated()) {
                totalWait += (order.getFinished() - order.getCreated());
                totalCount++;
            }
        }
        long averageTime = totalWait / (totalCount == 0 ? 1 : totalCount);

        Assert.assertTrue(averageTime == this.supermarket.statCustomerWaitAverageTime());
    }
}
