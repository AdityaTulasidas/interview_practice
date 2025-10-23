package com.thomsonreuters.metadataregistry.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Schema(description = "Paginated response for DataSource")
@Getter
public class ApiCollectionResponse<DataSource, T> {

        @Schema(description = "List of items")
        private List<T> items;

        @Schema(description = "Metadata information")
        private Meta _meta;


        public Meta _meta() {
            return _meta;
        }

        // Inner class for metadata
        @Schema(description = "Metadata information")
        public static class Meta {
            @Schema(description = "Total count of items")
            private long count;

            @Schema(description = "Limit of items per page")
            private int limit;

            @Schema(description = "Offset for pagination")
            private int offset;

            // Getters and setters
            public long getCount() {
                return count;
            }

            public void setCount(long count) {
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