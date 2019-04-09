/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.comm.osip.err;

import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/**
 * A ErrorMessageConfiguration is the JavaConfig for the {@link ErrorMessage} handling part.
 * 
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Configuration
public class ErrorMessageConfiguration {

    /**
     * Create a MessageChannel with the proper name dynamically.
     * 
     * @return An DirectChannel instance
     */
    @Bean(name = ErrorMessageServiceActivator.INPUT_CHANNEL_NAME)
    public MessageChannel getMessageChannel() {
        return new DirectChannel();
    }

    @Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
    @Bean("errExchange")
    DirectExchange directExchange(@Value("${owms.driver.osip.err.exchange-name}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
    @Bean("errQueue")
    Queue queue(@Value("${owms.driver.osip.err.queue-name}_${owms.driver.osip.err.routing-key}") String queueName) {
        return new Queue(queueName);
    }

    @Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
    @Bean("errBinding")
    Binding binding(
            @Value("${owms.driver.osip.err.exchange-name}") String exchangeName,
            @Value("${owms.driver.osip.err.queue-name}_${owms.driver.osip.res.routing-key}") String queueName,
            @Value("${owms.driver.osip.err.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(queue(queueName))
                .to(directExchange(exchangeName))
                .with(routingKey);
    }
}
