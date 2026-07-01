package com.wkr.store_appointment.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SchemaMigrationRunner implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public SchemaMigrationRunner(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ensureDeletedColumn("customer", "idx_customer_deleted");
        ensureDeletedColumn("appointment", "idx_appointment_deleted");
        ensureDeletedColumn("order_info", "idx_order_info_deleted");
    }

    private void ensureDeletedColumn(String tableName, String indexName) throws SQLException {

        if (!tableExists(tableName)) {
            return;
        }
        if (!columnExists(tableName, "deleted")) {
            jdbcTemplate.execute("alter table " + tableName + " add column deleted tinyint not null default 0");
        }
        if (!indexExists(tableName, indexName)) {
            jdbcTemplate.execute("create index " + indexName + " on " + tableName + " (deleted)");
        }
    }

    private boolean tableExists(String tableName) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            return exists(connection, tableName, null, MetadataLookup.TABLE)
                    || exists(connection, tableName.toUpperCase(), null, MetadataLookup.TABLE);
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            return exists(connection, tableName, columnName, MetadataLookup.COLUMN)
                    || exists(connection, tableName.toUpperCase(), columnName.toUpperCase(), MetadataLookup.COLUMN);
        }
    }

    private boolean indexExists(String tableName, String indexName) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return indexExists(metaData, connection.getCatalog(), connection.getSchema(), tableName, indexName)
                    || indexExists(metaData, connection.getCatalog(), null, tableName, indexName)
                    || indexExists(metaData, null, null, tableName.toUpperCase(), indexName);
        }
    }

    private boolean exists(Connection connection, String tableName, String columnName, MetadataLookup lookup) throws SQLException {

        DatabaseMetaData metaData = connection.getMetaData();
        return exists(metaData, connection.getCatalog(), connection.getSchema(), tableName, columnName, lookup)
                || exists(metaData, connection.getCatalog(), null, tableName, columnName, lookup)
                || exists(metaData, null, null, tableName, columnName, lookup);
    }

    private boolean exists(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName, MetadataLookup lookup) throws SQLException {

        try (ResultSet resultSet = lookup.find(metaData, catalog, schema, tableName, columnName)) {
            return resultSet.next();
        }
    }

    private boolean indexExists(DatabaseMetaData metaData, String catalog, String schema, String tableName, String indexName) throws SQLException {

        try (ResultSet resultSet = metaData.getIndexInfo(catalog, schema, tableName, false, false)) {
            while (resultSet.next()) {
                String currentName = resultSet.getString("INDEX_NAME");
                if (indexName.equalsIgnoreCase(currentName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private enum MetadataLookup {
        TABLE {
            @Override
            ResultSet find(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName) throws SQLException {
                return metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"});
            }
        },
        COLUMN {
            @Override
            ResultSet find(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName) throws SQLException {
                return metaData.getColumns(catalog, schema, tableName, columnName);
            }
        };

        abstract ResultSet find(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName) throws SQLException;
    }
}
