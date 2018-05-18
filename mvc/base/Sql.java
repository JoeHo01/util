package com.jo.analysis.web.mvc.base;

import org.apache.commons.lang3.StringUtils;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/5/6
 */
public class Sql {

	private String table;

	private String where;

	private String groupBy;

	private String orderBy;

	private String join;

	private String page;

	private String[] column;

	private String[] values;

	private String[] setting;

	private static final short TYPE_SELECT = 1;

	private static final short TYPE_INSERT = 2;

	private static final short TYPE_UPDATE = 3;

	private static final short TYPE_DELETE = 4;

	private short type;

	public static Sql  select(String table) {
		return new Sql(TYPE_SELECT, table);
	}

	public static Sql insert(String table) {
		return new Sql(TYPE_INSERT,table);
	}

	public static Sql update(String table) {
		return new Sql(TYPE_UPDATE,table);
	}

	public static Sql delete(String table) {
		return new Sql(TYPE_DELETE,table);
	}

	public Sql where(String condition) {
		this.where = " WHERE " + condition;
		return this;
	}

	public Sql page(int currentNum, int pageSize) {
		this.page = " WHERE " + table + ".id IN (SELECT id FROM " + table + " %s LIMIT " + currentNum + "," + pageSize + ")";
		return this;
	}

	public Sql join(String table, String condition) {
		if (this.join == null) this.join = "";
		this.join = this.join + " LEFT JOIN " + table + " ON " + condition;
		return this;
	}

	public Sql groupBy(String groupBy) {
		this.groupBy = " GROUP BY " + groupBy;
		return this;
	}

	public Sql orderBy(String orderBy) {
		this.orderBy = " ORDER BY " + orderBy;
		return this;
	}

	public Sql column(String... column) {
		this.column = column;
		return this;
	}

	public Sql values(String... values) {
		this.values = values;
		return this;
	}

	public Sql setting(String... setting) {
		this.setting = setting;
		return this;
	}

	public String build(){
		StringBuilder sql = new StringBuilder();
		switch (type) {
			case TYPE_SELECT:
				sql.append(String.format("SELECT %s FROM %s", format(column), table));
				if (join != null) sql.append(join);

				if (where == null) where = "";
				if (groupBy == null) groupBy = "";
				if (orderBy == null) orderBy = "";

				if (page != null)
					sql.append(String.format(page,where + groupBy + orderBy));
				else
					sql.append(where).append(groupBy).append(orderBy);
				break;
			case TYPE_INSERT:
				sql.append(String.format("INSERT INTO %s %s VALUES %s", table, "(" + format(column) + ")", format(values)));
				break;
			case TYPE_UPDATE:
				sql.append(String.format("UPDATE %s SET %s", table, format(setting)));
				if (where != null) sql.append(where);
				break;
			case TYPE_DELETE:
				sql.append(String.format("DELETE FROM %s", table));
				if (where != null) sql.append(where);
				break;
			default:
				break;
		}
		return sql.toString();
	}

	private Sql(short sql, String table) {
		switch (sql) {
			case TYPE_SELECT:
				this.type = sql;
				this.table = table;
				break;
			case TYPE_INSERT:
				this.type = sql;
				this.table = table;
				break;
			case TYPE_UPDATE:
				this.type = sql;
				this.table = table;
				break;
			case TYPE_DELETE:
				this.type = sql;
				this.table = table;
				break;
			default:
				break;
		}
	}

	private String format(String[] array) {
		StringBuilder builder = new StringBuilder();
		if (array != null) for (int i = 0; i < array.length; i++) {
			if (i > 0) builder.append(",");
			builder.append(array[i]);
		}
		return builder.toString();
	}
}
