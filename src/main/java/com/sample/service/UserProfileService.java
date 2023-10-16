package com.sample.service;

import com.sample.model.UserProfile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {


    /**
     * this is the connection for the database.
     */
    private final DataSource dataSource;
    /**
     * this helps to execute sql queries.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * this is the constructor.
     *
     * @param anDataSource
     * @param anJdbcTemplate
     */
    public UserProfileService(final DataSource anDataSource,
                              final JdbcTemplate anJdbcTemplate) {
        this.dataSource = anDataSource;
        this.jdbcTemplate = anJdbcTemplate;
    }

    /**
     * Maps the data from and to the database.
     *
     * @param rs
     * @param rowNum
     * @return p
     * @throws SQLException
     */
    private UserProfile rowMapper(final ResultSet rs,
                                  final Integer rowNum)
            throws SQLException {

        return new UserProfile(rs.getString(
                "user_handle"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getObject("dob", LocalDate.class)
        );
    }


    /**
     * @param userProfile
     * @return userProfile
     */
    public UserProfile create(final UserProfile userProfile) {

        final SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("user_profile")
                .usingColumns("user_handle",
                        "first_name",
                        "last_name", "dob");
        final Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("user_handle", userProfile.userHandle());
        valueMap.put("first_name", userProfile.firstName());
        valueMap.put("last_name", userProfile.lastName());
        valueMap.put("dob", userProfile.dob());

        insert.execute(valueMap);

        final Optional<UserProfile> createdUser =
                read(userProfile.userHandle());

        return createdUser.get();
    }

    /**
     * @param userHandle
     * @return UserProfile
     */
    public Optional<UserProfile> read(final String userHandle) {
        final String query = "SELECT user_handle,first_name,last_name,dob"
                + " FROM user_profile WHERE user_handle = ?";

        try {
            final UserProfile p = jdbcTemplate.queryForObject(query,
                    this::rowMapper, userHandle);
            return Optional.of(p);
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
