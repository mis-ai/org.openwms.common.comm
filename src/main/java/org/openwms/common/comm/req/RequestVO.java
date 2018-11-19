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
package org.openwms.common.comm.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * A RequestVO.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestVO implements Serializable {

    @JsonProperty
    String actualLocation, targetLocation, targetLocationGroup, barcode;
    @JsonProperty
    RequestHeaderVO header;
    @JsonProperty
    String errorCode;
    @JsonProperty
    Date created;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestHeaderVO {
        @JsonProperty
        String sender, receiver, sequenceNo;
    }
}
