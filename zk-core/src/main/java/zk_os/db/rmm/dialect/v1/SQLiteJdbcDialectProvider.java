//package zkbae.db.dialect.v1;
//
//import java.util.Optional;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
//import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
//import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
//import org.springframework.data.relational.core.mapping.NamingStrategy;
//
//@Configuration
//public class SQLiteJdbcDialectProvider extends AbstractJdbcConfiguration {
//
//	@Bean
//	@Override
//	public JdbcMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy, JdbcCustomConversions customConversions) {
//		JdbcMappingContext mappingContext = super.jdbcMappingContext(namingStrategy, customConversions);
//		mappingContext.setForceQuote(false);
//		return mappingContext;
//	}
//
//}