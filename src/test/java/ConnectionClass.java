import com.healthy.skincare.data.JdbcUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConnectionClass {
    private JdbcTemplate jdbcTemplate;
    private JdbcUserRepository userRepository;


    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*public User getCountOfEmployees() {
        userRepository.findByUsername()
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM EMPLOYEE", Integer.class);
    }*/
}
