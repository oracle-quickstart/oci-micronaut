package mushop.carts.soda;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.core.annotation.Internal;
import oracle.soda.OracleDatabase;
import oracle.soda.rdbms.OracleRDBMSClient;

@EachBean(OracleRDBMSClient.class)
@TransactionalOracleSodaDatabaseAnn
@Internal
interface TransactionalOracleSodaDatabase extends OracleDatabase {
}
