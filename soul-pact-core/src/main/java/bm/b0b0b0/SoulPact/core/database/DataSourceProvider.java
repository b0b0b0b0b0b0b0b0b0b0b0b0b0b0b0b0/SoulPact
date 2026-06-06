package bm.b0b0b0.SoulPact.core.database;

import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;

public final class DataSourceProvider {

    private final AtomicBoolean ready = new AtomicBoolean(false);
    private HikariDataSource dataSource;

    public void assign(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.ready.set(true);
    }

    public boolean isReady() {
        return ready.get() && dataSource != null && !dataSource.isClosed();
    }

    public DataSource dataSource() {
        if (!isReady()) {
            throw new IllegalStateException("Database is not ready.");
        }
        return dataSource;
    }

    public void shutdown() {
        ready.set(false);
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
