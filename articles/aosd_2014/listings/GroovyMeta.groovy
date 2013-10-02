List.metaClass.evenNumbers = {
	delegate.findAll { it%2 == 0}
}
println([1, 2, 3, 4].evenNumbers())