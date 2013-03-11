package gr.ekt.bte.core;

public class StringValue implements Value {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String getAsString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        Value val = (Value)other;
        return value.equals(val.getAsString());
    }

}
