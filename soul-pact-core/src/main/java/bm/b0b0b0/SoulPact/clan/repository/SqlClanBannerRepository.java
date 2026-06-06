package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class SqlClanBannerRepository implements ClanBannerRepository {

    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SqlClanBannerRepository(DataSourceProvider dataSourceProvider, AsyncDatabaseExecutor asyncDatabaseExecutor) {
        this.dataSourceProvider = dataSourceProvider;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public CompletableFuture<Optional<String>> findDataByClanId(long clanId) {
        return asyncDatabaseExecutor.supply(() -> queryData(clanId));
    }

    @Override
    public CompletableFuture<Boolean> updateData(long clanId, String bannerData) {
        return asyncDatabaseExecutor.supply(() -> updateDataSync(clanId, bannerData));
    }

    @Override
    public CompletableFuture<Boolean> markStandardIssued(long clanId) {
        return asyncDatabaseExecutor.supply(() -> markStandardIssuedSync(clanId));
    }

    @Override
    public CompletableFuture<Boolean> isStandardIssued(long clanId) {
        return asyncDatabaseExecutor.supply(() -> isStandardIssuedSync(clanId));
    }

    private boolean markStandardIssuedSync(long clanId) {
        String sql = "UPDATE clans SET standard_issued = 1 WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to mark clan standard issued", exception);
        }
    }

    private boolean isStandardIssuedSync(long clanId) {
        String sql = "SELECT standard_issued FROM clans WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
                return resultSet.getInt("standard_issued") == 1;
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan standard issued flag", exception);
        }
    }

    private Optional<String> queryData(long clanId) {
        String sql = "SELECT banner_data FROM clans WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, clanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.ofNullable(resultSet.getString("banner_data"));
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query clan banner", exception);
        }
    }

    private boolean updateDataSync(long clanId, String bannerData) {
        String sql = "UPDATE clans SET banner_data = ? WHERE id = ?";
        try (Connection connection = dataSourceProvider.dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bannerData);
            statement.setLong(2, clanId);
            return statement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to update clan banner", exception);
        }
    }
}
