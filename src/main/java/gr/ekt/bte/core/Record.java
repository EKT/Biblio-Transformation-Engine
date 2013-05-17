package gr.ekt.bte.core;

import java.util.List;

/**
   The basic immutable record. The most useful operation is the
   getValues that returns a list of values for a given field.

   @author Panagiotis Koutsourakis
   @author Konstantinos Stamatis
   @author Nikos Houssos
*/
public interface Record {
    /**
       Given the name of a field returns the list of values.

       @param field The name of the field
       @return      The list of values of the specified field
    */
    public List<Value> getValues(String field);

    /**
       Converts this record into a {@link MutableRecord}.

       @return a {@link MutableRecord} containing the exact same
       values
     */
    public MutableRecord makeMutable();

    /**
       Tests whether the record is mutable or not.

       @return <code>true</code> if the record is mutable
     */
    public boolean isMutable();
}
