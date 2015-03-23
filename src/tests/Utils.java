package tests;

import java.util.*;

public final class Utils {

	public final static <T> String join(T delimiter, List<T> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i <= list.size() - 1)
				sb.append(delimiter);
		}
		return sb.toString();
	}
}
