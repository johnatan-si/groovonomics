List<Integer> result = []
[1, 2, 3, 5, 8].each { item ->
	if(item % 2 == 0) {
		result << item
	}
}
println result