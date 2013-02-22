package gr.ekt.bte.core;

public class StringValue implements Value {
    private String value_;

    public StringValue(String value) {
        value_ = value;
    }

    @Override
    public String getAsString() {
        return value_;
    }

    @Override
    public boolean equals(Object other) {
        Value val = (Value)other;
        return value_.equals(val.getAsString());
    }

}
