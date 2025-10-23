package com.aditya.dataconnect.executionengine.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MetaDataRegistryException extends RuntimeException {
        private final String code;

        public MetaDataRegistryException(String message, String code) {
            super(message);
            this.code = code;
        }

}