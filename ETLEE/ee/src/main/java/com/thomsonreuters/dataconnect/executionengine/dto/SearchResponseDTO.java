package com.thomsonreuters.dataconnect.executionengine.dto;

import java.util.List;

public class SearchResponseDTO<T> {
    private List<T> items;
    private MetaData _meta;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public MetaData getMeta() {
        return _meta;
    }

    public void setMeta(MetaData newMeta) {
        this._meta = newMeta;
    }

    public static class MetaData {
        private int count;
        private int limit;
        private int offset;

        public MetaData(long totalElements, int size, int number) {
            this.count = (int) totalElements;
            this.limit = size;
            this.offset = number;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}