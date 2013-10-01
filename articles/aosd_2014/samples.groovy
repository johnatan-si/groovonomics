class GroovyCalculator {
	int sum(List<Integer> list) {
		int result = 0

		list.each { item ->
			result += item
		}

		result
	}

	public static void main(String args[]) {
		List<Integer> list = [1, 3, 5, 10]
		int result = new GroovyCalculator().sum(list)
		println result
	}
}
