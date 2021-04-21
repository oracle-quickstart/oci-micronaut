package mushop.carts.soda;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.transaction.exceptions.NoTransactionException;
import io.micronaut.transaction.jdbc.DataSourceUtils;
import io.micronaut.transaction.jdbc.DelegatingDataSource;
import io.micronaut.transaction.jdbc.exceptions.CannotGetJdbcConnectionException;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleException;
import oracle.soda.rdbms.OracleRDBMSClient;

import javax.sql.DataSource;
import java.sql.Connection;

@InterceptorBean(TransactionalOracleSodaDatabaseAnn.class)
@Prototype
@Internal
final class OracleSodaDatabaseInterceptor implements MethodInterceptor<OracleDatabase, Object> {
    private final OracleRDBMSClient client;
    private final DataSource dataSource;

    @SuppressWarnings({"rawtypes", "unchecked"})
    OracleSodaDatabaseInterceptor(BeanContext beanContext, Qualifier qualifier) {
        this.dataSource = DelegatingDataSource.unwrapDataSource(beanContext.getBean(DataSource.class, qualifier));
        this.client = beanContext.getBean(OracleRDBMSClient.class, qualifier);
    }

    @Override
    public Object intercept(MethodInvocationContext<OracleDatabase, Object> context) {
        Connection connection;
        try {
            connection = DataSourceUtils.getConnection(dataSource, false);
        } catch (CannotGetJdbcConnectionException e) {
            throw new NoTransactionException("No current transaction present. Consider declaring @Transactional on the surrounding method", e);
        }
        final ExecutableMethod<OracleDatabase, Object> method = context.getExecutableMethod();

        try {
            final OracleDatabase database = this.client.getDatabase(connection);
            return method.invoke(database, context.getParameterValues());
        } catch (OracleException e) {
            throw new CannotGetJdbcConnectionException(e.getMessage(), e);
        }
    }
}
