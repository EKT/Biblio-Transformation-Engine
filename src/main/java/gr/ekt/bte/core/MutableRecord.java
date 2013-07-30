package gr.ekt.bte.core;

import java.util.List;

/**
 *  An interface providing the operations for changing the data of a
 *  record.
 *
 *  @author Panagiotis Koutsourakis
 *  @author Konstantinos Stamatis
 *  @author Nikos Houssos
 */
public interface MutableRecord extends Record {
    /**
     * Adds a new field to the record.
     *
     * @param field  The name of the field
     * @param values The values to be inserted
     * @return       <code>true</code> if the values have been
     * successfully inserted
     */
    public boolean addField(String field, List<Value> values);

    /**
     * Adds a new value to the specified field.
     *
     * @param field The name of the field
     * @param value The value to be inserted
     * @return      <code>true</code> if the value has been
     * successfully inserted
     */
    public boolean addValue(String field, Value value);

    /**
     * Removes a field from the record.
     *
     * @param field The name of the field to be removed
     * @return      <code>true</code> if the field has been
     * successfully removed.
     */
    public boolean removeField(String field);

    /**
     * Erases a value from the specified field.
     *
     * @param field The name of the field
     * @param value The value to be erased.
     * @return      <code>true</code> if the value is successfully erased.
     */
    public boolean removeValue(String field, Value value);

    /**
     *  Changes the values associated with a specified field.
     *
     * @param field  The name of the field to be changed
     * @param values The new set of values to be associated with the
     * field
     * @return       <code>true</code> if the field has been
     * successfully updated
     */
    public boolean updateField(String field, List<Value> values);

    /**
     * Changes a single value in a field.
     *
     * @param field     The name of the field to be changed
     * @param old_value The previous value
     * @param new_value The new value
     * @return          <code>true</code> if the value has been
     * changed successfully
     */
    public boolean updateValue(String field, Value old_value, Value new_value);
}
