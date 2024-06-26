package io.github.lunasaw.voglander.common.constant.mq;

/**
 * @author luna
 * @date 2024/6/26
 */
public interface MqConstant {

    interface DirectTopic {
        String VOGLANDER_INNER_QUEUE           = "voglander.inner.direct.queue";
        String VOGLANDER_INNER_EXCHANGE_DIRECT = "voglander.inner.exchange.direct";
        String VOGLANDER_INNER_ROUTING_KEY     = "voglander.inner.routing-key";
    }

    interface DefaultTopic {
        String VOGLANDER_INNER_QUEUE = "voglander.inner.queue";
    }
}
