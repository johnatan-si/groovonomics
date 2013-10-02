import java.util.ArrayList;
import java.util.List;

public class JavaFilter {
	List<Integer> evenNumbers(List<Integer> list) {
		List<Integer> result = new ArrayList<Integer>();
		for(int item : list) {
			if(item % 2 == 0) {
				result.add(item);
			}
		}

		return result;
	}

	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(5);
		list.add(8);

		List<Integer> result = new JavaFilter().evenNumbers(list);
		System.out.println(result);
	}
}
