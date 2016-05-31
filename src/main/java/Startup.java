import demo.buyer.Buyer;
import demo.cashier.Cashier;
import demo.cashier.CashierSelector;
import demo.cashier.OrderCreatedHandler;
import demo.supermarket.CustomerOrder;
import demo.supermarket.Stat;
import demo.supermarket.Supermarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Startup {

    static final Logger logger = LoggerFactory.getLogger(Startup.class);

    public static void main(String[] args) throws Exception {

        Startup startup = new Startup();

        Stat stat = startup.getStat();

        // 打印统计数据
        if (logger.isInfoEnabled()) {
            logger.info("顾客平均等待时间 {} ms", stat.getCustomerWaitAverageTime());
            logger.info("商品平均售出时间 {} ms", stat.getGoodsSoldAverageTime());
            logger.info("商品全部售出总时间 {} ms", stat.getSoldOutTotalTime());
            for (Integer cashierId : stat.getCashierProcessOrderNum().keySet()) {
                logger.info("收银台 {} 共处理 {} 个客户订单", cashierId, stat.getCashierProcessOrderNum().get(cashierId));
            }
        }
    }

    private Stat getStat() throws InterruptedException {

        if (logger.isInfoEnabled()) {
            logger.info("初始化数据及任务");
        }

        // 初始化超市
        Supermarket supermarket = new Supermarket();

        // 初始化收银台任务
        List<Cashier> cashiers = new ArrayList<Cashier>();
        for (int i = 1; i <= Cashier.CASHIER_NUM; i++) {
            cashiers.add(new Cashier(i, supermarket));
        }

        // 初始化买任务
        Buyer buyer = new Buyer(supermarket, new OrderCreatedHandler(cashiers, new CashierSelector() {
            public Cashier select(CustomerOrder order, List<Cashier> cashiers) {
                // 缺省按求模的方式把订单均匀分配到各个收银台队列
                return cashiers.get(order.getOrderId() % cashiers.size());
            }
        }));


        if (logger.isInfoEnabled()) {
            logger.info("启动收银台及购买任务");
        }

        // 开始售买时间
        long startSell = System.currentTimeMillis();

        // 启动收银台任务
        for (Cashier cashier : cashiers) {
            new Thread(cashier, String.format("cashier-%d", cashier.getCashierId())).start();
        }

        // 启动购买任务
        new Thread(buyer, "buy-1").start();


        if (logger.isInfoEnabled()) {
            logger.info("等持任务执行完毕");
        }

        // 等待购买任务结束
        synchronized (Buyer.buyerOverLock) {
            while (!Buyer.buyerOver) {
                Buyer.buyerOverLock.wait();
            }
        }

        // 等待收银台任务结束
        for (Cashier cashier : cashiers) {
            synchronized (cashier.getCashierOverLock()) {
                while (!cashier.isCashierOver()) {
                    cashier.getCashierOverLock().wait();
                }
            }
        }

        // 卖完时间
        long soldOut = System.currentTimeMillis();


        if (logger.isInfoEnabled()) {
            logger.info("汇总结果数据");
        }

        Stat stat = new Stat();
        stat.setCustomerWaitAverageTime(supermarket.statCustomerWaitAverageTime());
        stat.setGoodsSoldAverageTime(supermarket.statGoodsSoldAverageTime(startSell));
        stat.setSoldOutTotalTime(soldOut - startSell);
        for (Cashier cashier : cashiers) {
            stat.getCashierProcessOrderNum().put(cashier.getCashierId(), cashier.getProcessOrdersNum());
        }
        return stat;
    }
}
