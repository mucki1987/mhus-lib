package de.mhus.lib.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.mhus.lib.core.MJson;
import de.mhus.lib.core.json.JacksonTransformer;
import de.mhus.lib.core.json.SerializerTransformer;
import de.mhus.lib.core.json.SimpleObjectTransformer;
import de.mhus.lib.core.json.TransformHelper;
import junit.framework.TestCase;

public class MJsonTest extends TestCase {

	public void testSerializerTransformations() {
		TransformHelper helper = new TransformHelper() {
			{
				strategy = new SerializerTransformer();
			}
		};

		{
			JsonNode j = MJson.pojoToJson( true, helper );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertEquals(true, (boolean)v);
		}

		{
			JsonNode j = MJson.pojoToJson( 1, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertEquals(1, (int)v);
		}

		{
			JsonNode j = MJson.pojoToJson( "aloa", helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertEquals("aloa", (String)v);
		}

		{
			JsonNode j = MJson.pojoToJson( null, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertNull(v);
		}
		
		{
			JsonNode j = MJson.pojoToJson( (byte)1, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertEquals(1, (byte)v);
		}
		
		{
			JsonNode j = MJson.pojoToJson( (double)1, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(j);
			assertEquals(1.0, (double)v);
		}
		
		{
			Map<String,Object> m = new HashMap<>();
			m.put("a", "b");
			JsonNode j = MJson.pojoToJson( m, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			assertEquals(m.getClass(), v.getClass());
			System.out.println(j);
			assertEquals(m.toString(), v.toString());
			
		}

		{
			Map<String,Object> m = new TreeMap<>();
			m.put("a", "b");
			m.put("b", 1);
			m.put("c", true);
			m.put("d", new MJsonObject("a"));
			JsonNode j = MJson.pojoToJson( m, helper  );
			Object v = MJson.jsonToPojo( j, helper  );
			assertEquals(m.getClass(), v.getClass());
			System.out.println(m);
			System.out.println(v);
			System.out.println(j);
			assertEquals(m.toString(), v.toString());
			
		}
		
		{
			Object u = new byte[] {0,1,2};
			JsonNode j = MJson.pojoToJson( u, helper  );
			System.out.println(j);
			Object v = MJson.jsonToPojo( j, helper  );
			System.out.println(Arrays.toString((byte[])u));
			System.out.println(Arrays.toString((byte[])v));
			assertEquals(Arrays.toString((byte[])u), Arrays.toString((byte[])v));
		}
		
	}

	public void testJacksonTransformations() {
		TransformHelper helper = new TransformHelper() {
			{
				strategy = new JacksonTransformer();
			}
		};

		
		{
			Object u = new MJsonObject("aloa");
			JsonNode j = MJson.pojoToJson( u, helper  );
			System.out.println(j);
			Object v = MJson.jsonToPojo( j, MJsonObject.class, helper  );
			assertEquals(u.toString(), v.toString());
		}

		{
			Object u = new HashMap<>();
			((HashMap)u).put("a", "b");
			JsonNode j = MJson.pojoToJson( u, helper  );
			System.out.println(j);
			Object v = MJson.jsonToPojo( j, HashMap.class, helper  );
			assertEquals(u.toString(), v.toString());
		}

		{
			Object u = new LinkedList<>();
			((LinkedList)u).add("a");
			JsonNode j = MJson.pojoToJson( u, helper  );
			System.out.println(j);
			Object v = MJson.jsonToPojo( j, LinkedList.class, helper  );
			assertEquals(u.toString(), v.toString());
		}
		
	}
	
	public void testSimpleTransformations() {
		TransformHelper helper = new TransformHelper() {
			{
				strategy = new SimpleObjectTransformer();
			}
		};

		{
			Object u = new MJsonObject("aloa");
			JsonNode j = MJson.pojoToJson( u, helper  );
			System.out.println(j);
			Object v = MJson.jsonToPojo( j, MJsonObject.class, helper  );
			assertEquals(u.toString(), v.toString());
		}
	}
		
		
}