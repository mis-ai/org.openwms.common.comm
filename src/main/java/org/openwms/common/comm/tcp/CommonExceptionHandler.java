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
package org.openwms.common.comm.tcp;

import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.err.ErrorCodes;
import org.openwms.common.comm.err.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * A CommonExceptionHandler.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("errorServiceActivator")
public class CommonExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExceptionHandler.class);

    /**
     * Reply to incoming OSIP telegrams on the inputChannel with an {@link ErrorMessage} o the outputChannel.
     * 
     * @param telegram
     *            The incoming OSIP telegram
     * @return An {@link ErrorMessage}
     */
    @ServiceActivator(inputChannel = "commonExceptionChannel", outputChannel = "outboundChannel")
    public ErrorMessage handle(String telegram) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Common error: " + telegram);
        }
        CommHeader header = CommonMessageFactory.createHeader(telegram);
        String sender = header.getSender();
        header.setSender(header.getReceiver());
        header.setReceiver(sender);
        return new ErrorMessage.Builder().withErrorCode(ErrorCodes.UNKNOWN_MESSAGE_TYPE).withCreateDate().build();
    }
}
