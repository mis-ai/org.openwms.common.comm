/*
 * Copyright 2019 Heiko Scherrer
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
package org.openwms.common.comm.tcp;

import org.openwms.common.comm.MessageChannelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionFailedEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.openwms.common.comm.CommConstants.PREFIX_CONNECTION_FACTORY;
import static org.openwms.common.comm.CommConstants.SUFFIX_INBOUND;
import static org.openwms.common.comm.CommConstants.SUFFIX_OUTBOUND;

/**
 * A ConnectionHolder keeps track of established connections per ConnectionFactory. It
 * listens on events whenever a new Connection is opened and stores the ConnectionId
 * that can be used as header argument in outbound messages.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
public class ConnectionHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHolder.class);
    private Map<String, String> connectionIds = new ConcurrentHashMap<>();
    private final Map<String, AbstractConnectionFactory> factories;

    public ConnectionHolder(@Autowired(required = false) List<AbstractConnectionFactory> factories) {
        this.factories = factories == null ?
                Collections.emptyMap() :
                factories
                        .stream()
                        .collect(Collectors.toMap(AbstractConnectionFactory::getComponentName, m -> m));
    }

    /**
     * On an {@link TcpConnectionOpenEvent} we store the connectionId along with the name
     * of the ConnectionFactory.
     *
     * @param event The event
     */
    @EventListener
    public void onOpenMessage(TcpConnectionOpenEvent event) {
        connectionIds.putIfAbsent(event.getConnectionFactoryName(), event.getConnectionId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("New connection for factory [{}] established with id [{}]", event.getConnectionFactoryName(), event.getConnectionId());
        }
    }


    /**
     * On an {@link TcpConnectionCloseEvent} we try to remove the existing assignment.
     *
     * @param event The event
     */
    @EventListener
    public void onClosedMessage(TcpConnectionCloseEvent event) {
        String connectionId = connectionIds.remove(event.getConnectionFactoryName());
        if (connectionId != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deregister connection for factory [{}] with id [{}]", event.getConnectionFactoryName(), event.getConnectionId());
        }
    }

    /**
     * On an {@link TcpConnectionCloseEvent} we try to remove the existing assignment.
     *
     * @param event The event
     */
    @EventListener
    public void onFailedMessage(TcpConnectionFailedEvent event) {
        LOGGER.debug("Connection Lost for : {}", ((AbstractConnectionFactory)event.getSource()).getComponentName());
        String connectionId = connectionIds.remove(((AbstractConnectionFactory)event.getSource()).getComponentName());
        if (connectionId != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deregister connection for factory [{}]", ((AbstractConnectionFactory)event.getSource()).getComponentName());
        }
    }

    /**
     * On an {@link TcpConnectionCloseEvent} we try to remove the existing assignment.
     *
     * @param event The event
     */
    @EventListener
    public void onFailedConnectionMessage(TcpConnectionExceptionEvent event) {
        LOGGER.debug("Connection Lost for : [{}]", event.getConnectionFactoryName());
        String connectionId = connectionIds.remove(event.getConnectionFactoryName());
        if (connectionId != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deregister connection for factory [{}]", event.getConnectionFactoryName());
        }
    }

    /**
     * Return the current active connectionId for the ConnectionFactory given by the
     * {@code connectionFactoryName}.
     *
     * @param connectionFactoryName Name of the ConnectionFactory
     * @return The connectionId of the active Connection
     */
    public String getConnectionId(String connectionFactoryName) {
        String result = connectionIds.get(PREFIX_CONNECTION_FACTORY + connectionFactoryName + SUFFIX_OUTBOUND);
        if (result == null) {
            result = connectionIds.get(PREFIX_CONNECTION_FACTORY + connectionFactoryName + SUFFIX_INBOUND);
            if (result == null) {
                throw new MessageChannelNotFoundException(format("No Connection found for ConnectionFactory [%s]", connectionFactoryName));
            }
        }
        return result;
    }
}
