class GroovyFilter {
	List<Integer> evenNumbers(List<Integer> list) {
		List<Integer> result = []
		for(int item : list) {
			if(item % 2 == 0) {
				result << item
			}
		}

		result
	}

	public static void main(String[] args) {
		List<Integer> list = [1, 2, 3, 5, 8]
		List<Integer> result = new GroovyFilter().evenNumbers(list)
		println result
	}
}
