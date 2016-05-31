package demo.buyer;

import demo.supermarket.Goods;
import demo.supermarket.Supermarket;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class BuyerTest {

    private Supermarket supermarket = new Supermarket();

    private Set<Integer> srcGoodsIds = new HashSet<Integer>();

    @BeforeMethod
    public void before() {
        for (Goods goods : supermarket.getListGoods()) {
            srcGoodsIds.add(goods.getGoodsId());
        }
    }

    @AfterMethod
    public void after() {
    }

    @Test
    public void test() throws Exception {
        MockOrderCreatedHandler createdHandler = new MockOrderCreatedHandler();

        // 启动buyer任务
        Buyer buyer = new Buyer(supermarket, createdHandler);
        new Thread(buyer, "buyer-1").start();

        // 等待buyer任务结束
        synchronized (Buyer.buyerOverLock) {
            while (!Buyer.buyerOver) {
                Buyer.buyerOverLock.wait();
            }
        }

        Set<Integer> buyGoodsIds = createdHandler.getGoodsIds();

        // 比对结果
        Assert.assertEquals(buyGoodsIds, srcGoodsIds);
        Assert.assertTrue(supermarket.getSaleableGoodsTotal() == 0);
    }
}
