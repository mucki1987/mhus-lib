/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.adb.query;

import java.util.LinkedList;
import java.util.Map.Entry;

import javax.transaction.NotSupportedException;

import de.mhus.lib.basics.consts.Identifier;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.parser.AttributeMap;
import de.mhus.lib.core.pojo.MPojo;

/**
 * <p>Db class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class Db {

	/**
	 * <p>query.</p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param <T> a T object.
	 * @return a {@link de.mhus.lib.adb.query.AQuery} object.
	 */
	public static <T> AQuery<T> query(Class<T> type) {
		return new AQuery<T>(type);
	}

	/**
	 * 
	 * @param mask Masquerading Base Type
	 * @param type Real Type to query for
	 * @return The Query
	 */
	public static <T> AQuery<T> query(Class<T> mask, Class<? extends T> type) {
		return new AQuery<T>(mask, type);
	}
	
	/**
	 * <p>eq.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart eq(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.EQ,left,right);
	}

	/**
	 * <p>eq.</p>
	 *
	 * @param attr a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart eq(String attr, Object value) {
		return new ACompare(ACompare.TYPE.EQ,attr(attr),value(null, attr, value));
	}

	/**
	 * <p>eq.</p>
	 *
	 * @param attr a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart eq(Identifier attr, Object value) {
		String name = MPojo.toAttributeName(attr);
		return new ACompare(ACompare.TYPE.EQ,attr(name),value(attr.getClazz(), name, value));
	}

	/**
	 * <p>like</p>
	 *
	 * @param attr a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return like
	 * @since 3.3.0
	 */
	public static APart like(String attr, Object value) {
		return new ACompare(ACompare.TYPE.LIKE,attr(attr),value(null, attr, value));
	}

	public static APart like(Identifier left, Object value) {
		String attr = MPojo.toAttributeName(left);
		return new ACompare(ACompare.TYPE.LIKE,attr(attr),value(left.getClazz(), attr, value));
	}

	/**
	 * <p>ne.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart ne(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.NE,left,right);
	}

	/**
	 * <p>lt.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart lt(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.LT,left,right);
	}

	public static APart lt(String attr, Object value) {
		return new ACompare(ACompare.TYPE.LT,attr(attr),value(null, attr, value));
	}
	
	/**
	 * <p>le.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart le(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.LE,left,right);
	}
	
	/**
	 * <p>gt.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart gt(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.GT,left,right);
	}

	/**
	 * <p>ge.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart ge(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.GE,left,right);
	}
	
	/**
	 * <p>el.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart el(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.EL,left,right);
	}

	/**
	 * <p>eg.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart eg(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.EG,left,right);
	}

	/**
	 * <p>like.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart like(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.LIKE,left,right);
	}

	/**
	 * <p>contains.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart contains(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.LIKE,left, new AContainsWrap( right ) );
	}

	public static APart in(Identifier left, Object ... right) {
		String attr = MPojo.toAttributeName(left);
		AAttribute[] list = new AAttribute[right.length];
		for (int i = 0; i < list.length ; i++)
			list[i] = new ADynValue(left.getClazz(), attr, null, right[i]);
		return new ACompare(ACompare.TYPE.IN,attr(attr),new AList(list));
	}
	
	/**
	 * <p>in.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param right a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart in(AAttribute left, AAttribute right) {
		return new ACompare(ACompare.TYPE.IN,left,right);
	}

	/**
	 * <p>in.</p>
	 *
	 * @param left a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param projection a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param subQuery a {@link de.mhus.lib.adb.query.AQuery} object.
	 * @return a {@link de.mhus.lib.adb.query.AOperation} object.
	 */
	public static APart in(AAttribute left, AAttribute projection,
			AQuery<?> subQuery) {
		return new ASubQuery(left, projection, subQuery);
	}

	/**
	 * <p>in.</p>
	 *
	 * @param left a {@link java.lang.String} object.
	 * @param projection a {@link java.lang.String} object.
	 * @param subQuery a {@link de.mhus.lib.adb.query.AQuery} object.
	 * @return a {@link de.mhus.lib.adb.query.AQuery} object.
	 */
	public <T> APart in(Identifier left, Identifier projection, AQuery<?> subQuery) {
		return new ASubQuery(attr(MPojo.toAttributeName(left)), attr(MPojo.toAttributeName(projection)), subQuery);
	}
	
	/**
	 * <p>in.</p>
	 *
	 * @param left a {@link java.lang.String} object.
	 * @param projection a {@link java.lang.String} object.
	 * @param subQuery a {@link de.mhus.lib.adb.query.AQuery} object.
	 * @return a {@link de.mhus.lib.adb.query.AQuery} object.
	 */
	public APart in(String left, String projection, AQuery<?> subQuery) {
		return new ASubQuery(Db.attr(left), Db.attr(projection), subQuery);
	}
	
	/**
	 * <p>and.</p>
	 *
	 * @param operations a {@link de.mhus.lib.adb.query.APart} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart and(APart ... operations) {
		return new AAnd(operations);
	}

	/**
	 * <p>or.</p>
	 *
	 * @param operations a {@link de.mhus.lib.adb.query.APart} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart or(APart ... operations) {
		return new AOr(operations);
	}

	/**
	 * <p>not.</p>
	 *
	 * @param operation a {@link de.mhus.lib.adb.query.APart} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart not(APart operation) {
		return new ANot(operation);
	}

	/**
	 * <p>concat.</p>
	 *
	 * @param parts a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute concat(AAttribute ... parts) {
		return new AConcat(parts);
	}

	/**
	 * A dynamic value.
	 * @param type 
	 * @param attr 
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute value(Class<?> type, String attr, Object value) {
		return new ADynValue(type, attr, null,value);
	}

	/**
	 * A dynamic value
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
//	public static AAttribute value(Object value) {
//		return new ADynValue(value);
//	}

	/**
	 * A database field.
	 *
	 * @param attribute a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute attr(String attribute) {
		return new ADbAttribute(null, attribute);
	}

	/**
	 * A database field.
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 * @param attribute a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute attr(Class<?> clazz, String attribute) {
		return new ADbAttribute(clazz, attribute);
	}

	/**
	 * A fixed value
	 *
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute fix(String value) {
		return new AFix(value);
	}

	/**
	 * <p>fix.</p>
	 *
	 * @param value a {@link java.lang.Enum} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute fix(Enum<?> value) {
		return new AEnumFix(value);
	}

	/**
	 * <p>literal.</p>
	 *
	 * @param literal a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart literal(String literal) {
		return new ALiteral(literal);
	}

	/**
	 * <p>literal.</p>
	 *
	 * @param list a {@link de.mhus.lib.adb.query.APart} object.
	 * @return a {@link de.mhus.lib.adb.query.APart} object.
	 */
	public static APart literal(APart ... list) {
		return new ALiteralList(list);
	}

	/**
	 * <p>list.</p>
	 *
	 * @param list a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.AAttribute} object.
	 */
	public static AAttribute list(AAttribute ... list) {
		return new AList(list);
	}

	/**
	 * <p>limit.</p>
	 *
	 * @param limit a int.
	 * @return a {@link de.mhus.lib.adb.query.AOperation} object.
	 */
	public static AOperation limit(int limit) {
		return new ALimit(limit);
	}
	
	/**
	 * <p>limit.</p>
	 *
	 * @param offset a int.
	 * @param limit a int.
	 * @return a {@link de.mhus.lib.adb.query.AOperation} object.
	 * @since 3.3.0
	 */
	public static AOperation limit(int offset, int limit) {
		return new ALimit(offset, limit);
	}
	
	private static class AContainsWrap extends AAttribute {

		private AAttribute attr;

		public AContainsWrap(AAttribute attr) {
			this.attr = attr;
		}

		@Override
		public void getAttributes(AQuery<?> query, AttributeMap map) {
			AttributeMap map2 = new AttributeMap();
			attr.getAttributes(query, map2);
			
			for (Entry<String, Object> entry : map2.entrySet())
				map.put(entry.getKey(), "%" + entry.getValue() + "%" );
			
		}
		
	}

	/**
	 * <p>isNull.</p>
	 *
	 * @param attr a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.AOperation} object.
	 */
	public static AOperation isNull(AAttribute attr) {
		return new ANull(attr, true);
	}

	/**
	 * <p>isNotNull.</p>
	 *
	 * @param attr a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @return a {@link de.mhus.lib.adb.query.AOperation} object.
	 */
	public static AOperation isNotNull(AAttribute attr) {
		return new ANull(attr, false);
	}


	private static final SearchHelper DEFAULT_HELPER = new SearchHelper();

	/**
	 * Extend the query by operators from the search string. THe method analyze the search string and
	 * add all search parameters to the query concatenating with 'and'.
	 * The syntax of the search are <key>:<value>,<key>:<value>... It's also possible to set only a value.
	 * In this case the helper is asked for the correct key.
	 *
	 * The helper plays a central role in analyzing the query. The helper can also deny the usage of keys or
	 * change the name.
	 *
	 * The value can have place holders at the beginning and end to generate a like statement. The placeholder
	 * is a asterisk.
	 *
	 * Use _search=name[ asc|desc] to set sorting.
	 *
	 * @param query The query to extend
	 * @param search The search string
	 * @param helper The helper or null for the default helper
	 * @return The query
	 * @param <T> a T object.
	 */
	public static <T> AQuery<T> extendObjectQueryFromSearch(AQuery<T> query, String search, SearchHelper helper) {
		if (MString.isEmpty(search)) return query;
		if (helper == null) helper = DEFAULT_HELPER;
		String[] parts = search.split(",");
		QueryCreate<T> cont = new QueryCreate<>(query, helper);
		for (String part : parts) {
			if (MString.isSet(part))
				extendObjectQueryFromParameter(cont, part);
		}
		return query;
	}

	// helper container to extend queries using fifo
	private static class QueryCreate<T> {

		private AQuery<T> query;
		private SearchHelper helper;
		private LinkedList<APart> queue = null;
		private APart current = null;

		public QueryCreate(AQuery<T> query, SearchHelper helper) {
			this.query = query;
			this.helper = helper;
		}

		public void add(APart pa) {
			if (current == null)
				query.and(pa);
			else
				try {
					current.append(pa);
				} catch (NotSupportedException e) {
				}
		}
		
		public void addOr() {
			APart next = Db.or();
			current = next;
			if (queue == null) {
				queue = new LinkedList<>();
				query.append(next);
			} else {
				try {
					queue.getLast().append(next);
				} catch (NotSupportedException e) {
				}
			}
			queue.add(next);
		}
		
		public void addAnd() {
			APart next = Db.and();
			current = next;
			if (queue == null) {
				queue = new LinkedList<>();
				query.append(next);
			} else {
				try {
					queue.getLast().append(next);
				} catch (NotSupportedException e) {
				}
			}
			queue.add(next);
		}
		
		public void reduce() {
			if (queue == null || queue.size() == 0) {
				MLogUtil.log().d(Db.class,"closing bracked without open bracked");
				current = null;
				return;
			}
			queue.removeLast();
			if (queue.size() == 0)
				current = null;
			else
				current = queue.getLast();
		}
		
	}
	
	/**
	 * <p>extendObjectQueryFromParameter.</p>
	 *
	 * @param query a {@link de.mhus.lib.adb.query.AQuery} object.
	 * @param part a {@link java.lang.String} object.
	 */
	private static void extendObjectQueryFromParameter(QueryCreate<?> cont, String part) {
		if (part.equals("_sort")) {
			// implemented sort option
			int p = part.indexOf(' ');
			String order = null;
			if (p > 0) {
				order = part.substring(p+1).trim().toLowerCase();
				part = part.substring(0,p);
			}
			cont.query.order(new AOrder(cont.query.getType(), part, "asc".equals(order)) );
			return;
		}
		if (part.equals("|(") || part.equals("or(")) {
			cont.addOr();
			return;
		}
		if (part.equals("&(") || part.equals("and(")) {
			cont.addAnd();
			return;
		}
		if (part.equals(")")) {
			cont.reduce();
			return;
		}
		
		int p = part.indexOf(':');
		String key = null;
		String value = null;
		if (p < 0) {
			key = cont.helper.findKeyForValue(cont.query, part);
			value = part;
		} else {
			key = part.substring(0,p);
			value = part.substring(p+1);
		}
		key = cont.helper.transformKey(cont.query, key);
		if (key == null) return; // ignore
		value = cont.helper.transformValue(cont.query, key, value);
		
		boolean like = false;
		if (cont.helper.isLikeAllowed(cont.query,key)) {
			if (value.startsWith("*") || value.endsWith("*")) {
				value = transformToLikeLike(value);
				like = true;
			}
		}
		
		APart pa = null;
		if (like)
			pa = cont.helper.createLike(key, value);
		else
			pa = cont.helper.createEq(key, value);
		cont.add(pa);
	}

	/**
	 * <p>transformToLikeLike.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String transformToLikeLike(String value) {
		if (value.indexOf('\\') > -1) value = value.replace("\\", "\\\\");
		if (value.indexOf('%') > -1) value = value.replace("%", "\\%");
		if (value.startsWith("*")) value = "%" + value.substring(1);
		if (value.endsWith("*")) value = value.substring(0, value.length()-1) + "%";
		return value;
	}
	
}
