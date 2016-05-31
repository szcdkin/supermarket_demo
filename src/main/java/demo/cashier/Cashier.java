package demo.cashier;

import demo.buyer.Buyer;
import demo.supermarket.CustomerOrder;
import demo.supermarket.Supermarket;
import demo.supermarket.exception.InventoryShortageException;
import demo.supermarket.exception.OrderHasFinishedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 收银台任务
 */
public class Cashier implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Cashier.class);

    /**
     * 当前收银台任务是否已结束
     */
    private volatile boolean cashierOver = false;

    /**
     * 当前收银台任务结束同步锁
     */
    private final byte[] cashierOverLock = new byte[0];

    /**
     * 收银台个数
     */
    public static final int CASHIER_NUM = 3;

    /**
     * 当前收银台待处理订单列表, 非阻塞队列
     */
    private List<CustomerOrder> createdOrders = new ArrayList<CustomerOrder>();

    /**
     * 当前收银台处理订单数
     */
    private int processOrdersNum;

    /**
     * 当前收银台id
     */
    private int cashierId;

    /**
     * 超市数据存取接口
     */
    private Supermarket supermarket;


    public Cashier(int cashierId, Supermarket supermarket) {
        this.cashierId = cashierId;
        this.supermarket = supermarket;
    }

    public void run() {
        try {

            // 处理下一订单推迟时间
            int nextProcessInterval = 0;

            while (createdOrders.size() > 0 || !Buyer.buyerOver) {
                if (nextProcessInterval > 0) {
                    Thread.sleep(nextProcessInterval * 1000);
                }

                synchronized (createdOrders) {
                    while (createdOrders.size() == 0) {
                        createdOrders.wait();
                    }

                    CustomerOrder order = createdOrders.remove(0);

                    if (order != null) {
                        if (logger.isInfoEnabled()) {
                            logger.info("收银台处理订单支付, cashierId={}, orderId={}", this.cashierId, order.getOrderId());
                        }

                        try {
                            supermarket.payOrder(order);
                            processOrdersNum++;
                        } catch (InventoryShortageException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("收银台处理订单支付失败, 库存不足，cashierId={}, orderId={}", this.cashierId, order.getOrderId());
                            }
                        } catch (OrderHasFinishedException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("收银台处理订单支付失败, 订单已完成，cashierId={}, orderId={}", this.cashierId, order.getOrderId());
                            }
                        }
                    }
                }

                nextProcessInterval = this.getRandom();
            }

            // 当前收银台任务结束
            synchronized (cashierOverLock) {
                cashierOver = true;
                cashierOverLock.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 添加待支付订单到收银台订单列表
     * @param order
     */
    public void addOrder(CustomerOrder order) {
        if (logger.isInfoEnabled()) {
            logger.info("添加待支付订单到收银台订单列表, cashierId={}, orderId={}", cashierId, order.getOrderId());
        }

        synchronized (createdOrders) {
            createdOrders.add(order);
            createdOrders.notifyAll();
        }
    }


    private int getRandom() {
        Random random = new Random();
        return random.nextInt(6) + 5;
    }

    public int getCashierId() {
        return cashierId;
    }

    public int getProcessOrdersNum() {
        return processOrdersNum;
    }

    public boolean isCashierOver() {
        return cashierOver;
    }

    public byte[] getCashierOverLock() {
        return cashierOverLock;
    }
}
