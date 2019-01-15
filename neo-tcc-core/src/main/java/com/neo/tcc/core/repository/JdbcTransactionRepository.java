package com.neo.tcc.core.repository;

import com.neo.tcc.api.TransactionStatus;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.serializer.KryoPoolSerializer;
import com.neo.tcc.core.serializer.ObjectSerializer;
import com.neo.tcc.core.utils.ByteUtils;
import com.neo.tcc.core.utils.CollectionUtils;
import com.neo.tcc.core.utils.StringUtils;

import javax.sql.DataSource;
import javax.transaction.xa.Xid;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/15 14:07
 * @Description:
 */
public class JdbcTransactionRepository extends CachableTransactionRepository {

    private String domain;
    private String suffix;
    private DataSource dataSource;
    private ObjectSerializer serializer = new KryoPoolSerializer();

    @Override
    protected int doCreate(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO" + getTableName() + "(GLOBAL_TX_ID, BRANCH_QUALIFIER, TRANSACTION_TYPE, CONTENT, " +
                    "STATUS, RETRY_TIMES, CREATE_TIME, LAST_UPDATE_TIME, VERSION");
            builder.append(StringUtils.isNotEmpty(domain) ? ", DOMAIN ) VALUES (?,?,?,?,?,?,?,?,?,?" : ") VALUES (?,?,?," +
                    "?,?,?,?,?,?)");

            stmt = connection.prepareStatement(builder.toString());
            stmt.setBytes(1, transaction.getId().getGlobalTransactionId());
            stmt.setBytes(2, transaction.getId().getBranchQualifier());
            stmt.setInt(3, transaction.getTransactionType().getId());
            stmt.setBytes(4, serializer.serialize(transaction));
            stmt.setInt(5, transaction.getStatus().getId());
            stmt.setInt(6, transaction.getRetryTimes());
            stmt.setTimestamp(7, new Timestamp(transaction.getCreateTime().getTime()));
            stmt.setTimestamp(8, new Timestamp(transaction.getLastUpdateTime().getTime()));
            stmt.setLong(9, transaction.getVersion());
            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(10, domain);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            this.closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        java.util.Date lastUpdateTime = transaction.getLastUpdateTime();
        long currentVersion = transaction.getVersion();

        transaction.updateTime();
        transaction.updateVersion();

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE " + getTableName() + " SET " +
                    "CONTENT = ?,STATUS = ?,LAST_UPDATE_TIME = ?, RETRIED_COUNT = ?,VERSION = VERSION+1 WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ? AND VERSION = ?");
            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");

            stmt = connection.prepareStatement(builder.toString());
            stmt.setBytes(1, serializer.serialize(transaction));
            stmt.setInt(2, transaction.getStatus().getId());
            stmt.setTimestamp(3, new Timestamp(transaction.getLastUpdateTime().getTime()));
            stmt.setInt(4, transaction.getRetryTimes());
            stmt.setBytes(5, transaction.getId().getGlobalTransactionId());
            stmt.setBytes(6, transaction.getId().getBranchQualifier());
            stmt.setLong(7, currentVersion);
            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(8, domain);
            }

            int result = stmt.executeUpdate();

            return result;
        } catch (Throwable e) {
            transaction.setLastUpdateTime(lastUpdateTime);
            transaction.setVersion(currentVersion);
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM " + getTableName() +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?");
            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");

            stmt = connection.prepareStatement(builder.toString());
            stmt.setBytes(1, transaction.getId().getGlobalTransactionId());
            stmt.setBytes(2, transaction.getId().getBranchQualifier());
            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(3, domain);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected Transaction doGetTransaction(Xid xid) {
        List<Transaction> transactions = doGet(Arrays.asList(xid));

        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.get(0);
        }
        return null;
    }

    @Override
    protected List<Transaction> doGetTimeoutTransactions(Date date) {
        List<Transaction> transactions = new ArrayList<>();

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();

            builder.append("SELECT GLOBAL_TX_ID, BRANCH_QUALIFIER, CONTENT,STATUS,TRANSACTION_TYPE,CREATE_TIME,LAST_UPDATE_TIME,RETRIED_COUNT,VERSION");
            builder.append(StringUtils.isNotEmpty(domain) ? ",DOMAIN" : "");
            builder.append("  FROM " + getTableName() + " WHERE LAST_UPDATE_TIME < ?");
            builder.append(" AND IS_DELETE = 0 ");
            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setTimestamp(1, new Timestamp(date.getTime()));
            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(2, domain);
            }

            ResultSet resultSet = stmt.executeQuery();

            this.constructTransactions(resultSet, transactions);
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return transactions;
    }

    protected List<Transaction> doGet(List<Xid> ids) {
        List<Transaction> transactions = new ArrayList<Transaction>();

        if (CollectionUtils.isEmpty(ids)) {
            return transactions;
        }

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("SELECT GLOBAL_TX_ID, BRANCH_QUALIFIER, CONTENT,STATUS,TRANSACTION_TYPE,CREATE_TIME,LAST_UPDATE_TIME,RETRIED_COUNT,VERSION");
            builder.append(StringUtils.isNotEmpty(domain) ? ",DOMAIN" : "");
            builder.append("  FROM " + getTableName() + " WHERE");

            if (!CollectionUtils.isEmpty(ids)) {
                for (Xid xid : ids) {
                    builder.append(" ( GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ? ) OR");
                }

                builder.delete(builder.length() - 2, builder.length());
            }

            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");

            stmt = connection.prepareStatement(builder.toString());

            int i = 0;

            for (Xid xid : ids) {
                stmt.setBytes(++i, xid.getGlobalTransactionId());
                stmt.setBytes(++i, xid.getBranchQualifier());
            }

            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(++i, domain);
            }

            ResultSet resultSet = stmt.executeQuery();

            this.constructTransactions(resultSet, transactions);
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return transactions;
    }

    protected void constructTransactions(ResultSet resultSet, List<Transaction> transactions) throws SQLException {
        while (resultSet.next()) {
            byte[] transactionBytes = resultSet.getBytes(3);
            Transaction transaction = (Transaction) serializer.deserialize(transactionBytes);
            transaction.changeStatus(TransactionStatus.valueOf(resultSet.getInt(4)));
            transaction.setLastUpdateTime(resultSet.getDate(7));
            transaction.setVersion(resultSet.getLong(9));
            transaction.resetRetryTimes(resultSet.getInt(8));
            transactions.add(transaction);
        }
    }

    protected Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    protected void releaseConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    private void closeStatement(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    private String getTableName() {
        return StringUtils.isNotEmpty(suffix) ? "TCC_TRANSACTION" + suffix : "TCC_TRANSACTION";
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ObjectSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }
}
