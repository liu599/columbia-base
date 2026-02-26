package base.ecs32.top.api.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
  @ConfigurationProperties(prefix = "spring.datasource.blog")
  public DataSource blogDataSource() {
    return new DriverManagerDataSource();
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
