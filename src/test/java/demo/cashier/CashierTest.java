package demo.cashier;

import demo.buyer.Buyer;
import demo.supermarket.CustomerOrder;
import demo.supermarket.Goods;
import demo.supermarket.Supermarket;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CashierTest {

    private Supermarket supermarket = new Supermarket();

    private int orderId = 0;

    private Cashier cashier = null;

    @BeforeMethod
    public void before() {
        cashier = new Cashier(1, supermarket);
    }

    @Test
    public void test() throws Exception {

        int totalGoods = supermarket.getSaleableGoodsTotal();

        // 启动收银台任务
        new Thread(cashier, "cashier-1").start();

        // 添加订单到收银台
        this.mockBuy();

        // 等待收银台任务结束
        synchronized (cashier.getCashierOverLock()) {
            while (!cashier.isCashierOver()) {
                cashier.getCashierOverLock().wait();
            }
        }

        // 对比结果
        Assert.assertTrue(cashier.getProcessOrdersNum() == totalGoods);
    }

    private void mockBuy() throws Exception {
        CustomerOrder order = null;
        while ((order = this.buyOne()) != null) {
            Thread.sleep(10000);
            cashier.addOrder(order);
        }
        Buyer.buyerOver = true;

    }

    private CustomerOrder buyOne() {
        CustomerOrder order = null;

        Goods goods = supermarket.getRandomGoods();

        if (goods == null) {
            return order;
        }

        order = new CustomerOrder();
        order.setOrderId(++orderId);
        order.setGoods(goods);
        order.setStatus(CustomerOrder.ORDER_CREATED);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(0l);
        return order;
    }
}
