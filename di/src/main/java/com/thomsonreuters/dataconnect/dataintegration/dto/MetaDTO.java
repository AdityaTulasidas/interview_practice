package com.thomsonreuters.dataconnect.dataintegration.dto;

public class MetaDTO {
    private int count;
    private int limit;
    private int offset;

    public MetaDTO() {}

    public MetaDTO(int count, int limit, int offset) {
        this.count = count;
        this.limit = limit;
        this.offset = offset;
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
