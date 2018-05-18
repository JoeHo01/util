package com.jo.analysis.web.mvc.base;

import com.jo.analysis.web.mvc.base.annotation.Table;
import com.jo.analysis.web.mvc.base.pojo.DO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/4/21
 *
 */
public class BaseDAO {

	protected final JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

	public BaseDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public <D extends DO> int save(D d) {
		if (d == null) return 0;
		// get class
		Class<? extends DO> clazz = d.getClass();
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// build columns
		String[] column = buildColumn(clazz, fields);
		// build values
		String values = buildInsertValues(clazz, fields, d);
		// sql of insert
		Sql sql = Sql.insert(table).column(column).values(values);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> connection.prepareStatement(sql.build(), PreparedStatement.RETURN_GENERATED_KEYS), keyHolder);
		return keyHolder.getKey().intValue();
	}

	public <D extends DO> void save(List<D> dos) {
		if (CollectionUtils.isEmpty(dos)) return;
		// get class
		Class<? extends DO> clazz = dos.get(0).getClass();
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// build column
		String[] column = buildColumn(clazz, fields);
		// build values
		String[] values = new String[dos.size()];
		for (int i = 0; i < dos.size(); i++)
			values[i] = buildInsertValues(clazz, fields, dos.get(i));

		// sql of insert
		Sql sql = Sql.insert(table).column(column).values(values);
		jdbcTemplate.execute(sql.build());
	}

	public <D extends DO> void remove(String id, Class<D> clazz) {
		if (StringUtils.isEmpty(id)) return;
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// sql of delete
		Sql sql = Sql.delete(table).where("id IN (" + id + ")");
		jdbcTemplate.execute(sql.build());
	}

	public <D extends DO> void update(D d) {
		// get id
		Long id = d == null ? null : d.getId();
		if (id == null) return;
		// get class
		Class<? extends DO> clazz = d.getClass();
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// get updated value
		String[] setting = buildSetting(clazz, fields, d);
		// sql of update
		Sql sql = Sql.update(table).setting(setting).where("id = " + String.valueOf(id));
		jdbcTemplate.execute(sql.build());
	}


	public <D extends DO> D get(Long id, Class<D> clazz) {
		if (id == null) return null;
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// build columns
		String[] column = buildColumn(clazz, fields);
		// sql of select
		Sql sql = Sql.select(table).column(column).where("id = ?");
		return jdbcTemplate.queryForObject(sql.build(), new Object[]{id}, new BeanPropertyRowMapper<>(clazz));
	}

	public <D extends DO> List<D> list(D d) {
		if (d == null) return null;
		// get class
		Class<D> clazz = (Class<D>) d.getClass();
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// get columns
		String[] column = buildColumn(clazz, fields);
		// build query conditions
		String condition = buildCondition(clazz, fields, d);
		// sql of select
		Sql sql = Sql.select(table).column(column).where(condition);

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.build());
		return POJOHelper.convert(result, clazz);
	}

	public List<Map<String, Object>> list(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

	public <D extends DO> Long count(D d) {
		if (d == null) return null;
		// get class
		Class<? extends DO> clazz = d.getClass();
		// get table name in DB
		String table = clazz.getAnnotation(Table.class).value();
		// get fields in class
		ArrayList<Field> fields = POJOHelper.getField(clazz);
		// build query conditions
		String condition = buildCondition(clazz, fields, d);
		// sql of select
		Sql sql = Sql.select(table).column("count(0)").where(condition);
		return jdbcTemplate.queryForObject(sql.build(), Long.class);
	}

	private String buildCondition(Class clazz, List<Field> fields, DO d) {
		StringBuilder condition = new StringBuilder();
		for (Field field : fields) {
			String column = POJOHelper.getColumn(clazz, field.getName());
			String value = getValue(clazz, field.getName(), d);
			if (value != null) {
				if (condition.length() > 0) condition.append(" AND ");
				condition.append(column).append(" LIKE '%").append(value).append("%'");
			}
		}
		return condition.toString();
	}

	private String buildInsertValues(Class clazz, List<Field> fields, DO d){
		StringBuilder values = new StringBuilder();
		values.append("(");
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) values.append(",");
			String value = getValue(clazz, fields.get(i).getName(), d);
			if (value != null)
				values.append('\'').append(value).append('\'');
			else
				values.append("null");
		}
		values.append(")");
		return values.toString();
	}

	private String[] buildSetting(Class clazz, List<Field> fields, DO d) {
		List<String> setting = new ArrayList<>();
		for (Field field : fields) {
			String column = POJOHelper.getColumn(clazz, field.getName());
			String value = getValue(clazz, field.getName(), d);
			if (value != null) setting.add(column + "='" + value + "'");
		}
		return setting.toArray(new String[setting.size()]);
	}

	private String[] buildColumn(Class clazz, List<Field> fields) {
		String[] column = new String[fields.size()];
		for (int i = 0; i < fields.size() ; i++)
			column[i] = POJOHelper.getColumn(clazz, fields.get(i).getName());
		return column;
	}

	private String getValue(Class clazz, String fieldName, DO d) {
		String value = null;
		Method get = POJOHelper.methodGet(clazz, fieldName);
		if (get != null) try  {
			Object o = get.invoke(d);
			if (!ObjectUtils.isEmpty(o)) value = o instanceof Date ? dateFormat((Date) o) : String.valueOf(o);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return value;
	}

	private String dateFormat(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

}
