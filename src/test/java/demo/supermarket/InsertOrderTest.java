package demo.supermarket;

import junit.framework.Assert;
import org.testng.annotations.Test;

public class InsertOrderTest {

    @Test
    public void test() {
        Supermarket supermarket = new Supermarket();

        CustomerOrder order = new CustomerOrder();
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        supermarket.insertOrder(order);

        order = new CustomerOrder();
        supermarket.insertOrder(order);

        Assert.assertTrue(supermarket.getOrders().size() == 5);
    }
}
