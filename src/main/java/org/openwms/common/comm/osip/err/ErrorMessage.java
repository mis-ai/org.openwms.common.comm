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

import org.openwms.common.comm.osip.Payload;
import org.openwms.common.comm.osip.ResponseHeader;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * An ErrorMessage signals any error or failure situation from or to an external system.
 * 
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class ErrorMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "ERR_";
    private String locationGroupName;
    private Date createDate;

    /*~------------ Constructors ------------*/
    public ErrorMessage() {
        super();
    }

    private ErrorMessage(Builder builder) {
        setHeader(builder.header);
        setErrorCode(builder.errorCode);
        setCreated(builder.created);
        locationGroupName = builder.locationGroupName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /*~------------ Accessors ------------*/
    public ResponseHeader getHeader() {
        return super.getHeader();
    }

    public void setHeader(ResponseHeader header) {
        super.setHeader(header);
    }

    public String getLocationGroupName() {
        return locationGroupName;
    }

    public void setLocationGroupName(String locationGroupName) {
        this.locationGroupName = locationGroupName;
    }

    public Date getCreateDate() {
        return super.getCreated();
    }

    public void setCreateDate(Date createDate) {
        super.setCreated(createDate);
    }

    /*~------------ Builders ------------*/
    public static final class Builder {
        private ResponseHeader header;
        private String errorCode;
        private Date created;
        private String locationGroupName;

        private Builder() {
        }

        public Builder header(ResponseHeader val) {
            header = val;
            return this;
        }

        public Builder errorCode(String val) {
            errorCode = val;
            return this;
        }

        public Builder created(Date val) {
            created = val;
            return this;
        }

        public Builder locationGroupName(String val) {
            locationGroupName = val;
            return this;
        }

        public ErrorMessage build() {
            return new ErrorMessage(this);
        }
    }

    /*~------------ Overrides ------------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithoutReply() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", ErrorMessage.class.getSimpleName() + "[", "]").toString();
    }
}
