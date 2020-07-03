import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortTest {

	@Test
	public  void testSort(){
		List<Integer> bsort = Arrays.asList(5, 1, 3, 4, 8);
		Collections.sort(bsort);
		System.out.println(bsort.toString());
	}
}
