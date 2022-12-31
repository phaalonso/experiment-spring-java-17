package com.pedroalonso.productcrud;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;

@Service
public class ProductService {
    private final JdbcTemplate jdbcTemplate;

    private final String findByIdSql = """
            SELECT * FROM products \
            WHERE id = ?
            """;

    private final String insertSql = """
            INSERT INTO products(name, status) \
            VALUES (?, ?)
            """;

    private final RowMapper<ProductRecord> productRecordRowMapper = (rs, rowNum) -> new ProductRecord(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("status"));

    public ProductService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ProductRecord findById(Integer id) {
        return this.jdbcTemplate.queryForObject(findByIdSql, productRecordRowMapper, id);
    }

    public ProductRecord create(String name, ProductStatus status) {
        var statusCode = switch (status) {
            case ACTIVE -> 1;
            case INACTIVE -> 0;
        };

        List<SqlParameter> declaredParameters = List.of(
                new SqlParameter(Types.VARCHAR, "name"),
                new SqlParameter(Types.INTEGER, "status"));

        var pscf = new PreparedStatementCreatorFactory(insertSql, declaredParameters);
        pscf.setReturnGeneratedKeys(true);
        pscf.setGeneratedKeysColumnNames("id");

        var psc = pscf.newPreparedStatementCreator(List.of(name, statusCode));
        var generatedKey = new GeneratedKeyHolder();

        this.jdbcTemplate.update(psc, generatedKey);

        if (generatedKey.getKey() instanceof Integer id) {
            return findById(id);
        }
        throw new IllegalArgumentException("Não foi possivel criar produto " + name + ".");
    }
}
