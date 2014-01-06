package a.packagename.with.multiple.levels

class Class {
	
	Closure c = { int s_p_1, d_p_1, d_p_2 ->
		int s_local_1, s_local_3, s_local_3
		def d_local_1, d_local_3, d_local_3, d_local_4
		
		 [].each { int s_p_2, d_p_3 ->
			int s_local_4
			def d_local_5
		 }
	}
}