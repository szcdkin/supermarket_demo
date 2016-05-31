package demo.supermarket;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SupermarketInitTest {

    @Test
    public void test() {
        Supermarket supermarket = new Supermarket();

        Assert.assertTrue(supermarket.getOrders() != null);
        Assert.assertTrue(supermarket.getOrders().size() == 0);
        Assert.assertTrue(supermarket.getListGoods() != null);
        Assert.assertTrue(supermarket.getListGoods().size() == 3);

        for (Goods goods : supermarket.getListGoods()) {
            Assert.assertTrue(goods.getInventory().get() == 15);
        }

        Assert.assertTrue(supermarket.getSaleableGoodsTotal() == 45);

        int apple = 0;
        int macbook = 0;
        int cookie = 0;
        for (Goods goods : supermarket.getListGoods()) {
            if ("Apple".equals(goods.getName())) {
                apple++;
            }
            if ("Macbook".equals(goods.getName())) {
                macbook++;
            }
            if ("Cookie".equals(goods.getName())) {
                cookie++;
            }
        }
        Assert.assertTrue(apple == 1);
        Assert.assertTrue(macbook == 1);
        Assert.assertTrue(cookie == 1);
    }
}
