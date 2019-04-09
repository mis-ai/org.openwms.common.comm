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
package org.openwms.common.comm.osip.locu.tcp;

import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMapper;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.app.Driver;
import org.openwms.common.comm.osip.locu.LocationUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;

/**
 * A LocationUpdateTelegramMapper.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Component
class LocationUpdateTelegramMapper implements MessageMapper<LocationUpdateMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUpdateTelegramMapper.class);
    private final Driver driver;

    LocationUpdateTelegramMapper(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<LocationUpdateMessage> mapTo(String telegram, Map<String, Object> headers) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Telegram to transform: [{}]", telegram);
        }
        int startLocationGroup = LENGTH_HEADER + forType().length();
        int startLocation = startLocationGroup + 20;
        int startErrorCode = startLocation + 20;
        int startCreateDate = startErrorCode + Payload.ERROR_CODE_LENGTH;
        try {
            return new GenericMessage<>(
                    new LocationUpdateMessage.Builder()
                            .withType(LocationUpdateMessage.IDENTIFIER)
                            .withLocationGroupName(telegram.substring(startLocationGroup, startLocation))
                            .withLocation(telegram.substring(startLocation, startErrorCode))
                            .withErrorCode(telegram.substring(startErrorCode, startCreateDate))
                            .withCreated(
                                    telegram.substring(startCreateDate, startCreateDate + Payload.DATE_LENGTH),
                                    driver.getOsip().getDatePattern()
                            ).build()
                    , CommonMessageFactory.createHeaders(telegram, headers)
            );
        } catch (ParseException e) {
            throw new MessageMismatchException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String forType() {
        return LocationUpdateMessage.IDENTIFIER;
    }
}
