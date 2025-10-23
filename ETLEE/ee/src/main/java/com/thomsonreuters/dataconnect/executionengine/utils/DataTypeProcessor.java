package com.thomsonreuters.dataconnect.executionengine.utils;

        import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
        import org.postgresql.util.PGobject;

        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Timestamp;
        import java.time.LocalDateTime;
        import java.time.format.DateTimeFormatter;
        import java.util.EnumMap;
        import java.util.Map;
        import java.util.function.BiFunction;

        public class DataTypeProcessor {

            private static final Map<DataType, BiFunction<ResultSet, String, Object>> dataTypeHandlers = new EnumMap<>(DataType.class);

            static {
                dataTypeHandlers.put(DataType.STRING, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getString(columnName);


                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing STRING type", e);
                    }
                });
                dataTypeHandlers.put(DataType.CHAR, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getString(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing CHAR type", e);
                    }
                });
                dataTypeHandlers.put(DataType.INTEGER, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getInt(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing INTEGER type", e);
                    }
                });
                dataTypeHandlers.put(DataType.UUID, (resultSet, columnName) -> {
                    try {
                        return resultSet.getObject(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing INTEGER type", e);
                    }
                });
                dataTypeHandlers.put(DataType.BOOLEAN, (resultSet, columnName) -> {
                    try {
                        Object rawValue = resultSet.getObject(columnName);
                        boolean booleanValue = false;
                        if (rawValue instanceof Boolean) {
                            booleanValue= (Boolean) rawValue;
                        } else if (rawValue instanceof Integer) {
                            return ((Integer) rawValue) != 0;
                        } else if (rawValue instanceof String) {
                            return Boolean.parseBoolean((String) rawValue);
                        }
                        return booleanValue;
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing BOOLEAN type", e);
                    }
                });
                dataTypeHandlers.put(DataType.DATETIME, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getObject(columnName, LocalDateTime.class);

                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing DATETIME type", e);
                    }
                });
                dataTypeHandlers.put(DataType.DATE, (resultSet, columnName) -> {
                    try {
                        if(resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getDate(columnName).toLocalDate();
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing DATE type", e);
                    }
                });
                dataTypeHandlers.put(DataType.TIMESTAMP, (resultSet, columnName) -> {
                    try {
                        Object rawValue = resultSet.getObject(columnName);
                        if(rawValue == null) {
                            return null;
                        }else if (rawValue instanceof Timestamp) {
                            return ((Timestamp) rawValue).toLocalDateTime();
                        } else if (rawValue instanceof String) {
                            return LocalDateTime.parse((String) rawValue, DateTimeFormatter.ISO_DATE_TIME);
                        } else {
                            throw new IllegalArgumentException("Unsupported TIMESTAMP type for column: " + columnName);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing TIMESTAMP type", e);
                    }
                });
                dataTypeHandlers.put(DataType.TEXT, (resultSet, columnName) -> {
                    try {
                        return resultSet.getString(columnName) == null? "null" : resultSet.getString(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing TEXT type", e);
                    }
                });
                dataTypeHandlers.put(DataType.DOUBLEPRECISION, (resultSet, columnName) -> {
                    try {
                        Double value = resultSet.getDouble(columnName);
                        if (resultSet.wasNull()) {
                            return null;
                        }
                        return value;
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing DOUBLEPRECISION type", e);
                    }
                });
                dataTypeHandlers.put(DataType.ARRAY, (resultSet, columnName) -> {
                    try {
                        java.sql.Array sqlArray = resultSet.getArray(columnName);
                        if (sqlArray == null) {
                            return null;
                        }
                        return sqlArray.getArray(); // returns Object[]
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing ARRAY type", e);
                    }
                });
                dataTypeHandlers.put(DataType.JSONB, (resultSet, columnName) -> {
                    try {
                        Object rawValue = resultSet.getString(columnName);
                        if (rawValue == null) {
                            return null;
                        }
                        // If using PostgreSQL, JSONB is returned as String or PGobject
                        if (rawValue instanceof String) {
                            return rawValue;
                        } else if (rawValue instanceof PGobject) {
                            return ((PGobject) rawValue).getValue();
                        } else {
                            return rawValue.toString();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing JSONB type", e);
                    }
                });
                dataTypeHandlers.put(DataType.SMALLINT, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getShort(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing SMALLINT type", e);
                    }
                });
                dataTypeHandlers.put(DataType.BIGINT, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getLong(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing BIGINT type", e);
                    }
                });
                dataTypeHandlers.put(DataType.NUMERIC, (resultSet, columnName) -> {
                    try {
                        if (resultSet.getObject(columnName) == null) {
                            return null;
                        }
                        return resultSet.getBigDecimal(columnName);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing NUMERIC type", e);
                    }
                });
                dataTypeHandlers.put(DataType.TEXT_ARRAY, (resultSet, columnName) -> {
                    try {
                        java.sql.Array array = resultSet.getArray(columnName);
                        if (array == null) {
                            return null;
                        }
                        return (String[]) array.getArray();
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing TEXT_ARRAY type", e);
                    }
                });
                // Add other DataType handlers as needed
            }

            public static Object processDataType(DataType type, ResultSet resultSet, String columnName) throws SQLException {
                BiFunction<ResultSet, String, Object> handler = dataTypeHandlers.get(type);
                if (handler == null) {
                    throw new IllegalArgumentException("Unsupported DataType: " + type);
                }
                return handler.apply(resultSet, columnName);
            }
        }

