package demo.buyer;

import demo.supermarket.CustomerOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

public class MockOrderCreatedHandler implements OrderCreatedListener {

    private Set<Integer> goodsIds = new HashSet<Integer>();

    public void created(CustomerOrder order) {
        System.out.println(ToStringBuilder.reflectionToString(order));

        order.getGoods().getInventory().decrementAndGet();

        goodsIds.add(order.getGoods().getGoodsId());
    }

    public Set<Integer> getGoodsIds() {
        return goodsIds;
    }
}
