package aic.bigdata.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;

import aic.bigdata.database.model.AicUser;
import aic.bigdata.server.ServerConfig;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class SqlDatabase {

	JdbcDataSource ds = null;
	Connection conn = null;
	private ConnectionSource connectionSource;
	private Dao<AicUser, String> userDao = null;

	public SqlDatabase(ServerConfig config) throws SQLException {
		String url = "jdbc:h2:file:./" + config.getSqlDatabaseName();
		connectionSource = new JdbcConnectionSource(url, "sa", "sa");

		TableUtils.createTableIfNotExists(connectionSource, AicUser.class);
		if (config.getSqlCleanOnStart()) {
			TableUtils.clearTable(connectionSource, AicUser.class);
		}
		userDao = DaoManager.createDao(connectionSource, AicUser.class);
	}

	public void recreateTables() throws SQLException {
		TableUtils.dropTable(connectionSource, AicUser.class, true);
		TableUtils.createTableIfNotExists(connectionSource, AicUser.class);
	}

	public void createUser(AicUser user) throws SQLException {
		if (userDao.queryForSameId(user) == null) {
			userDao.create(user);
		}
	}

	public AicUser getUserById(long id) throws SQLException {
		return userDao.queryForId(String.valueOf(id));
	}

	public long getUserCount() throws SQLException {
		return userDao.countOf();
	}

	public List<AicUser> getAllUsers() throws SQLException {
		return userDao.queryForAll();
	}

	public List<AicUser> getUsers(long page, long pageSize) throws SQLException {
		long startRow = page * pageSize;
		PreparedQuery<AicUser> q = userDao.queryBuilder().offset(startRow).limit(pageSize).orderBy("name", true)
				.prepare();
		return userDao.query(q);
	}

	public List<Long> getUserIds(int userLimit) throws SQLException {
		List<Long> list = new ArrayList<Long>();
		PreparedQuery<AicUser> q = userDao.queryBuilder().prepare();
		for (AicUser usr : userDao.query(q)) {
			list.add(usr.getId());
		}
		return list;
	}

}
