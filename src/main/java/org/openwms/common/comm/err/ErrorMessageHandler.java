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
package org.openwms.common.comm.err;

import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * A ErrorMessageHandler is the default implementation to handle {@link ErrorMessage}s but does not do anything, it's just to satisfy the
 * dependency. The error handling functionality must be implemented in the actual project, because the OSIP specification does not make
 * any requirements nor assumptions to error handling.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class ErrorMessageHandler implements Function<GenericMessage<ErrorMessage>, Void> {

    /**
     * Does not do anything.
     */
    @Override
    public Void apply(GenericMessage<ErrorMessage> errorMessage) {

        // Currently no error handling happens in the base.
        return null;
    }
}
