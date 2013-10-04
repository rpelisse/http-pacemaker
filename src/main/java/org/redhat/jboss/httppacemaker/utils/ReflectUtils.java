package org.redhat.jboss.httppacemaker.utils;

public final class ReflectUtils {

	private ReflectUtils() {}
	
	public static <T> Object invokeValueOfOnString(String value, Class<T> type) {
		if (value == null)
			return null;
		Object valueAsAnObject;
		if (type == String.class) {
			valueAsAnObject = value;
		} else {
			try {
				//noinspection unchecked
				valueAsAnObject = type.getMethod("valueOf",String.class).invoke(type,value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return valueAsAnObject;
	}
}
