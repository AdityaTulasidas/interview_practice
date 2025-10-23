package com.aditya.ETLExecutionEngine.context;

public class AdapterContext extends ExecutionContextBase {
    public static final String ADOPTER_ID = "adopter_id";
    public static final String META_OBJECT = "meta_obj";
    public static final String PARSER = "parser";

    public AdapterContext() {
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AdapterContext)) {
            return false;
        } else {
            AdapterContext other = (AdapterContext)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AdapterContext;
    }

    public int hashCode() {
        int result = 1;
        return 1;
    }

    public String toString() {
        return "AdapterContext()";
    }
}
