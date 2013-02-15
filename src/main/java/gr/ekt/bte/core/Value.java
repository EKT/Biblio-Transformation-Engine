package gr.ekt.bte.core;

public interface Value {
    public String getAsString();
    // We need to override this if we want other than the default comparison semantics.
    // As a matter of fact we do for the MapRecord.
    public boolean equals(Object other);
}
