package io.realm.typed;

import java.util.Date;

import io.realm.ColumnType;
import io.realm.Group;
import io.realm.Mixed;
import io.realm.Table;
import io.realm.TableSpec;

/**
 * Super-type of the generated XyzTable classes for the Xyz entity, having
 * common table operations for all entities.
 */
public abstract class AbstractTable<Cursor, View, Query> extends AbstractTableOrView<Cursor, View, Query> {

    static {
        TightDB.loadLibrary();
    }

    protected final Table table;

    public AbstractTable(EntityTypes<?, View, Cursor, Query> types) {
        this(types, new Table());
    }

    /**
     * Can be used to specify a different name than the class. Allows for more of same type in typed interface
     * @param types
     * @param group
     * @param tableName
     */
    public AbstractTable(EntityTypes<?, View, Cursor, Query> types, Group group, String tableName) {
        this(types, group.getTable(tableName));
    }

    public AbstractTable(EntityTypes<?, View, Cursor, Query> types, Group group) {
        this(types, group.getTable(types.getTableClass().getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    protected AbstractTable(EntityTypes<?, View, Cursor, Query> types, Table table) {
        super(types, table);
        this.table = table;
        if (table != null && table.getTableSpec().getColumnCount() <= 0) {
            // Build table schema
            final TableSpec spec = new TableSpec();
            specifyTableStructure((Class<? extends AbstractTable<?, ?, ?>>) types.getTableClass(), spec);
            table.updateFromSpec(spec);
        }
    }

    public boolean isValid() {
        return table.isValid();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AbstractTable)
            return table.equals(((AbstractTable<?, ?, ?>) other).table);
        return false;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    protected static void addLongColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.INTEGER, name);
    }

    protected static void addFloatColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.FLOAT, name);
    }

    protected static void addDoubleColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.DOUBLE, name);
    }

    protected static void addStringColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.STRING, name);
    }

    protected static void addBooleanColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.BOOLEAN, name);
    }

    protected static void addBinaryColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.BINARY, name);
    }

    protected static void addDateColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.DATE, name);
    }

    protected static void addMixedColumn(TableSpec spec, String name) {
        spec.addColumn(ColumnType.MIXED, name);
    }

    @SuppressWarnings("unchecked")
    protected static void addTableColumn(TableSpec spec, String name, AbstractTable<?, ?, ?> subtable) {
        TableSpec subspec = spec.addSubtableColumn(name);
        specifyTableStructure((Class<? extends AbstractTable<?, ?, ?>>) subtable.getClass(), subspec);
    }

    protected static void specifyTableStructure(Class<? extends AbstractTable<?, ?, ?>> tableClass, TableSpec spec) {
        try {
            tableClass.getMethod("specifyStructure", TableSpec.class).invoke(tableClass, spec);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't specify the table structure!", e);
        }
    }

    protected void insertLong(long columnIndex, long rowIndex, long value) {
        table.getInternalMethods().insertLong(columnIndex, rowIndex, value);
    }

    protected void insertFloat(long columnIndex, long rowIndex, float value) {
        table.getInternalMethods().insertFloat(columnIndex, rowIndex, value);
    }

    protected void insertDouble(long columnIndex, long rowIndex, double value) {
        table.getInternalMethods().insertDouble(columnIndex, rowIndex, value);
    }

    protected void insertString(long columnIndex, long rowIndex, String value) {
        table.getInternalMethods().insertString(columnIndex, rowIndex, value);
    }

    protected void insertBoolean(long columnIndex, long rowIndex, boolean value) {
        table.getInternalMethods().insertBoolean(columnIndex, rowIndex, value);
    }

    protected void insertBinary(long columnIndex, long rowIndex, byte[] value) {
        table.getInternalMethods().insertBinary(columnIndex, rowIndex, value);
    }

    /*
    protected void insertBinary(long columnIndex, long rowIndex, ByteBuffer value) {
        table.getInternalMethods().insertBinary(columnIndex, rowIndex, value);
    }
    */

    protected void insertDate(long columnIndex, long rowIndex, Date value) {
        table.getInternalMethods().insertDate(columnIndex, rowIndex, value);
    }

    protected void insertMixed(long columnIndex, long rowIndex, Object value) {
        Mixed mixed = Mixed.mixedValue(value);
        table.getInternalMethods().insertMixed(columnIndex, rowIndex, mixed);
    }

    protected void insertTable(long columnIndex, long rowIndex, Object[][] values) {
        table.getInternalMethods().insertSubtable(columnIndex, rowIndex, values);
    }

    protected void insertDone() {
        table.getInternalMethods().insertDone();
    }

    public void optimize() {
        table.optimize();
    }

    public Query where() {
        return AbstractQuery.createQuery(types.getQueryClass(), table, table.where());
    }

   /* // Experimental
    public long lookup(String value) {
        return table.lookup(value);
    }*/

    public void moveLastOver(long rowIndex) {
        table.moveLastOver(rowIndex);
    }

    public long lowerBound(long columnIndex, long value) {
        return table.lowerBoundLong(columnIndex, value);
    }
    public long upperBound(long columnIndex, long value) {
        return table.upperBoundLong(columnIndex, value);
    }

    public Cursor addEmptyRow() {
        long rowPos = table.addEmptyRow();
        return cursor(rowPos);
    }
}