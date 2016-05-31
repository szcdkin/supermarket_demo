package demo.buyer;

import demo.supermarket.CustomerOrder;
import demo.supermarket.Goods;
import demo.supermarket.Supermarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 购买任务
 */
public class Buyer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Buyer.class);

    /**
     * 购买任务是否已结束
     */
    public static volatile boolean buyerOver = false;

    /**
     * 购买任务结束同步锁
     */
    public static final byte[] buyerOverLock = new byte[0];

    /**
     * 超市接口
     */
    private Supermarket supermarket = null;

    /**
     * 订单创建监听器,
     * 用于发布订单创建事件, 处理后续的订单状态流转
     */
    private OrderCreatedListener createdListener = null;

    /**
     * 订单id产生器
     */
    private AtomicInteger orderIdGenerator = new AtomicInteger(0);

    public Buyer(Supermarket supermarket, OrderCreatedListener createdListener) {
        this.supermarket = supermarket;
        this.createdListener = createdListener;
    }

    public void run() {

        try {
            // 下一次购买推迟时间
            int nextBuyInterval = 0;

            while (this.supermarket.getSaleableGoodsTotal() > 0) {

                if (nextBuyInterval > 0) {
                    Thread.sleep(nextBuyInterval * 1000);
                }

                /*
                  购买任务挑选商品生成订单不代表购买成功,
                  该例只会启动一个购买任务线程,但收银台任务线程处理订单支付的进度仍然可能落后于购买任务线程购买的进度,
                  即存在某个商品被多次重复购买,后续支付的时候扣减库存会抛库存不足异常
                 */

                CustomerOrder order = this.buyOne();

                if (order != null && order.getGoods() != null) {
                    if (logger.isInfoEnabled()) {
                        logger.info("购买任务购买商品,生成订单, orderId={}, goodsId={}", order.getOrderId(), order.getGoods().getGoodsId());
                    }

                    // 保存订单
                    supermarket.insertOrder(order);

                    /*
                       created回调方法由购买任务线程执行,
                       方法里不能能有长时间阻塞或耗时操作
                     */
                    createdListener.created(order);
                }

                nextBuyInterval = getRandom();
            }

            // 购买任务结束
            synchronized (Buyer.buyerOverLock) {
                Buyer.buyerOver = true;
                Buyer.buyerOverLock.notifyAll();
            }

        } catch (InterruptedException err) {
            Thread.currentThread().interrupt();
            err.printStackTrace();
        }
    }

    private CustomerOrder buyOne() {
        CustomerOrder order = null;

        // 挑选商品
        Goods goods = supermarket.getRandomGoods();

        if (goods == null) {
            return order;
        }

        // 生成订单
        order = new CustomerOrder();
        order.setOrderId(orderIdGenerator.incrementAndGet());
        order.setGoods(goods);
        order.setStatus(CustomerOrder.ORDER_CREATED);
        order.setCreated(System.currentTimeMillis());
        order.setFinished(0l);
        return order;
    }

    private int getRandom() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }
}
