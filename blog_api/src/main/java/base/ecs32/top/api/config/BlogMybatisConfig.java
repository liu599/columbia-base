package base.ecs32.top.api.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@MapperScan(
    basePackages = "base.ecs32.top.blog.dao",
    sqlSessionTemplateRef = "blogSqlSessionTemplate"
)
public class BlogMybatisConfig {

    @Bean(name = "blogDataSource")
    public DataSource blogDataSource(
            @Value("${spring.datasource.blog.url}") String url,
            @Value("${spring.datasource.blog.username}") String username,
            @Value("${spring.datasource.blog.password}") String password,
            @Value("${spring.datasource.blog.driver-class-name}") String driverClassName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "blogSqlSessionFactory")
    public SqlSessionFactory blogSqlSessionFactory(@Qualifier("blogDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        // 设置 MyBatis-Plus 配置
        com.baomidou.mybatisplus.core.MybatisConfiguration configuration = new com.baomidou.mybatisplus.core.MybatisConfiguration();
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);

        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    @Bean(name = "blogSqlSessionTemplate")
    public SqlSessionTemplate blogSqlSessionTemplate(@Qualifier("blogSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "blogTransactionManager")
    public DataSourceTransactionManager blogTransactionManager(@Qualifier("blogDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
