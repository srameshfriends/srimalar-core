package dcapture.data.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.reflections.Reflections;

import javax.persistence.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Sql Enhancer
 *
 * @author Ramesh
 * @since Apr 12, 2017
 */
public class SqlEnhancer {
    private static final Logger logger = Logger.getLogger(SqlEnhancer.class.getName());

    public void write(File persistenceFile, String... packageArray) throws Exception {
        logger.info("Entity configuration writing..,");
        Map<Class<?>, SqlTable> classMap = new HashMap<>();
        if (packageArray == null || 1 > packageArray.length) {
            logger.info("Persistence entity not registered");
            return;
        }
        for (String pack : packageArray) {
            Reflections reflections = new Reflections(pack);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
            addTableClassMap(classMap, classes);
        }
        addTableColumns(classMap);
        JsonArray jsonArray = new JsonArray();
        for (SqlTable table : classMap.values()) {
            jsonArray.add(toJsonObject(table));
        }
        writeAsJson(persistenceFile, jsonArray);
        logger.info(" +++ persistence configuration has created +++ ");
    }

    private void addTableClassMap(Map<Class<?>, SqlTable> classMap, Set<Class<?>> entityList) {
        if (entityList != null && !entityList.isEmpty()) {
            for (Class<?> clazz : entityList) {
                classMap.put(clazz, null);
            }
        }
    }

    private void addTableColumns(Map<Class<?>, SqlTable> tableClassMap) {
        for (Class<?> entity : tableClassMap.keySet()) {
            String tableName = findTableName(entity);
            if (tableName == null) {
                continue;
            }
            List<Field> fieldList = new ArrayList<>();
            SqlTable sqlTable = new SqlTable(tableName, entity);
            sqlTable.setFieldList(fieldList);
            addFields(fieldList, entity);
            tableClassMap.put(sqlTable.getType(), sqlTable);
        }
        Collection<SqlTable> tableList = tableClassMap.values();
        for (SqlTable table : tableList) {
            addColumnsToTable(table);
        }
        for (SqlTable table : tableList) {
            createJoinColumn(table, tableList);
        }
        for (SqlTable table : tableList) {
            List<SqlColumn> sortedList = createSortedColumnList(table);
            table.setSqlColumnList(sortedList);
        }
        List<SqlReference> refList = new ArrayList<>();
        for (SqlTable table : tableList) {
            List<SqlColumn> sqlColumnList = table.getSqlColumnList();
            for (SqlColumn sqlColumn : sqlColumnList) {
                if (sqlColumn.getJoinTable() == null) {
                    continue;
                }
                SqlReference reference = new SqlReference();
                reference.setSqlTable(sqlColumn.getJoinTable());
                reference.setSqlColumn(sqlColumn.getJoinTable().getPrimaryColumn());
                reference.setReferenceTable(table);
                reference.setReferenceColumn(sqlColumn);
                refList.add(reference);
            }
        }
        for (SqlTable table : tableList) {
            List<SqlReference> list = refList.stream().filter(reference ->
                    table.equals(reference.getSqlTable())).collect(Collectors.toList());
            if (!list.isEmpty()) {
                table.setReferenceList(list);
            }
        }
    }

    private List<SqlColumn> createSortedColumnList(SqlTable sqlTable) {
        List<Class<?>> parentClassList = new ArrayList<>();
        parentClassList.add(sqlTable.getType());
        findSuperClass(sqlTable.getType(), parentClassList);
        Collections.reverse(parentClassList);
        List<String> columnList = new ArrayList<>();
        for (Class<?> cls : parentClassList) {
            ColumnIndex columnIndex = cls.getAnnotation(ColumnIndex.class);
            if (columnIndex != null && 0 < columnIndex.columns().length) {
                Collections.addAll(columnList, columnIndex.columns());
            }
        }
        List<SqlColumn> orderList = new ArrayList<>();
        List<SqlColumn> unOrderList = new ArrayList<>();
        for (String column : columnList) {
            for (SqlColumn sqlColumn : sqlTable.getSqlColumnList()) {
                if (column.equals(sqlColumn.getFieldName())) {
                    orderList.add(sqlColumn);
                    break;
                }
            }
        }
        for (SqlColumn sqlColumn : sqlTable.getSqlColumnList()) {
            if (!orderList.contains(sqlColumn)) {
                unOrderList.add(sqlColumn);
            }
        }
        orderList.addAll(unOrderList);
        return orderList;
    }

    private JsonObject toJsonObject(SqlTable sqlTable) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", sqlTable.getName());
        obj.addProperty("type", sqlTable.getType().getName());
        //
        SqlColumn priCol = sqlTable.getPrimaryColumn();
        JsonObject jsonPriCol = new JsonObject();
        jsonPriCol.addProperty("name", priCol.getName());
        jsonPriCol.addProperty("type", priCol.getType().getName());
        obj.add("primaryColumn", jsonPriCol);
        //
        JsonArray jsonSqlColList = new JsonArray();
        for (SqlColumn sqlColumn : sqlTable.getSqlColumnList()) {
            jsonSqlColList.add(toJsonObject(sqlColumn));
        }
        obj.add("sqlColumnList", jsonSqlColList);
        if (sqlTable.getReferenceList() != null) {
            JsonArray jsonSqlRefList = new JsonArray();
            for (SqlReference sqlRef : sqlTable.getReferenceList()) {
                jsonSqlRefList.add(toJsonObject(sqlRef));
            }
            obj.add("referenceList", jsonSqlRefList);
        }
        return obj;
    }

    private JsonObject toJsonObject(SqlColumn sqlColumn) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fieldName", sqlColumn.getFieldName());
        obj.addProperty("name", sqlColumn.getName());
        obj.addProperty("type", sqlColumn.getType().getName());
        obj.addProperty("primaryKey", sqlColumn.isPrimaryKey());
        obj.addProperty("autoIncrement", sqlColumn.isAutoIncrement());
        obj.addProperty("nullable", sqlColumn.isNullable());
        obj.addProperty("length", sqlColumn.getLength());
        obj.addProperty("enumType", sqlColumn.isEnumType());
        //
        if (sqlColumn.getJoinTable() != null) {
            SqlTable joinTbl = sqlColumn.getJoinTable();
            JsonObject jsonJoinTbl = new JsonObject();
            jsonJoinTbl.addProperty("name", joinTbl.getName());
            jsonJoinTbl.addProperty("type", joinTbl.getType().getName());
            obj.add("joinTable", jsonJoinTbl);
        }
        if (sqlColumn.getTemporalType() != null) {
            obj.addProperty("temporalType", sqlColumn.getTemporalType().name());
        }
        obj.addProperty("colIndex", sqlColumn.getColIndex());
        if (sqlColumn.getSqlType() != null) {
            obj.addProperty("sqlType", sqlColumn.getSqlType().getName());
        }
        return obj;
    }

    private JsonObject toJsonObject(SqlReference ref) {
        JsonObject result = new JsonObject();
        //
        SqlTable sqlTbl = ref.getSqlTable();
        JsonObject jsonSqlTbl = new JsonObject();
        jsonSqlTbl.addProperty("name", sqlTbl.getName());
        jsonSqlTbl.addProperty("type", sqlTbl.getType().getName());
        result.add("sqlTable", jsonSqlTbl);
        //
        SqlTable refTbl = ref.getReferenceTable();
        JsonObject jsonRefTbl = new JsonObject();
        jsonRefTbl.addProperty("name", refTbl.getName());
        jsonRefTbl.addProperty("type", refTbl.getType().getName());
        result.add("referenceTable", jsonSqlTbl);
        //
        SqlColumn sqlColumn = ref.getSqlColumn();
        JsonObject jsonSqlCol = new JsonObject();
        jsonSqlCol.addProperty("name", sqlColumn.getName());
        jsonSqlCol.addProperty("fieldName", sqlColumn.getFieldName());
        jsonSqlCol.addProperty("type", sqlColumn.getType().getName());
        result.add("sqlColumn", jsonSqlCol);
        //
        SqlColumn refColumn = ref.getReferenceColumn();
        JsonObject jsonRefCol = new JsonObject();
        jsonRefCol.addProperty("name", refColumn.getName());
        jsonRefCol.addProperty("fieldName", refColumn.getFieldName());
        jsonRefCol.addProperty("type", refColumn.getType().getName());
        result.add("referenceColumn", jsonRefCol);
        return result;
    }

    private SqlTable findJoinTable(String tableName, Collection<SqlTable> tableList) {
        if (tableName == null) {
            return null;
        }
        for (SqlTable table : tableList) {
            if (tableName.equals(table.getName())) {
                return table;
            }
        }
        return null;
    }

    private void createJoinColumn(final SqlTable sqlTable, final Collection<SqlTable> tableList) {
        for (Field field : sqlTable.getFieldList()) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                SqlTable joinTable = findJoinTable(joinColumn.table(), tableList);
                if (joinTable == null) {
                    String err = sqlTable.getType() + " join column table name missing " + field.getName();
                    throw new NullPointerException(err);
                }
                SqlColumn sqlColumn = createJoinColumn(field, joinColumn);
                sqlColumn.setJoinTable(joinTable);
                sqlColumn.setLength(joinTable.getPrimaryColumn().getLength());
                sqlTable.getSqlColumnList().add(sqlColumn);
            }
        }
    }

    private SqlColumn createJoinColumn(Field field, JoinColumn joinColumn) {
        final String name = joinColumn.name().length() != 0 ? joinColumn.name() : field.getName();
        SqlColumn sqlColumn = new SqlColumn(name, field.getType());
        sqlColumn.setFieldName(field.getName());
        sqlColumn.setNullable(joinColumn.nullable());
        return sqlColumn;
    }

    private SqlColumn createColumn(Field field, Column column) {
        final String name = column.name().length() != 0 ? column.name() : field.getName();
        SqlColumn sqlColumn = new SqlColumn(name, field.getType());
        sqlColumn.setFieldName(field.getName());
        sqlColumn.setNullable(column.nullable());
        sqlColumn.setLength(column.length());
        if (Date.class.equals(sqlColumn.getType())) {
            Temporal temporal = field.getAnnotation(Temporal.class);
            sqlColumn.setTemporalType(temporal == null ? null : temporal.value());
        }
        return sqlColumn;
    }

    private void addColumnsToTable(SqlTable sqlTable) {
        List<SqlColumn> columnList = new ArrayList<>();
        sqlTable.setSqlColumnList(columnList);
        for (Field field : sqlTable.getFieldList()) {
            Column column = field.getAnnotation(Column.class);
            SqlColumn sqlColumn = null;
            if (column != null) {
                sqlColumn = createColumn(field, column);
            }
            Id primaryId = field.getAnnotation(Id.class);
            if (primaryId != null) {
                if (sqlColumn == null) {
                    sqlColumn = new SqlColumn(field.getName(), field.getType());
                }
                sqlColumn.setPrimaryKey(true);
                GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                if (generatedValue != null) {
                    sqlColumn.setAutoIncrement(true);
                }
            }
            if (sqlColumn == null) {
                continue;
            }
            sqlColumn.setEnumType(sqlColumn.getType().isEnum());
            columnList.add(sqlColumn);
        }
    }

    private void addFields(List<Field> fieldList, Class<?> classType) {
        Field[] fieldArray = classType.getDeclaredFields();
        if (fieldArray.length > 0) {
            Collections.addAll(fieldList, fieldArray);
        }
        if (classType.getSuperclass() != null) {
            if (!Object.class.equals(classType.getSuperclass())) {
                addFields(fieldList, classType.getSuperclass());
            }
        }
    }

    private String findTableName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Table table = clazz.getAnnotation(Table.class);
        return table == null ? null : table.name();
    }

    private void findSuperClass(Class<?> cls, List<Class<?>> classList) {
        Class<?> parentClass = cls.getSuperclass();
        if (parentClass != null) {
            classList.add(parentClass);
            findSuperClass(parentClass, classList);
        }
    }

    private void writeAsJson(File file, JsonElement element) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(element.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
