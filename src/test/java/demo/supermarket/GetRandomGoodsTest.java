package demo.supermarket;

import junit.framework.Assert;
import org.testng.annotations.Test;

public class GetRandomGoodsTest {

    @Test
    public void test() {
        Supermarket supermarket = new Supermarket();

        int beforeTotal = supermarket.getSaleableGoodsTotal();

        int selectTotal = 0;

        Goods goods = null;
        while ((goods = supermarket.getRandomGoods()) != null) {
            selectTotal++;
            goods.getInventory().decrementAndGet();
        }

        Assert.assertTrue(supermarket.getSaleableGoodsTotal() == 0);
        Assert.assertTrue(beforeTotal == selectTotal);
    }
}
