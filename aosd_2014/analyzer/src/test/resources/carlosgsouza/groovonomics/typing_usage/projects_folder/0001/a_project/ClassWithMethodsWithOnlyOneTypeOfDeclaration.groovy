import org.gradle.*

class ClassWithMethodsWithOnlyOneTypeOfDeclaration {

	public int public_s_method_with_no_parameters() {
	}

	public int public_s_metod_with_s_parameter(int param_1, float param_2) {
		1
	}

	public int public_s_method_with_s_and_d_parameters(param_1, int param_2) {
		[1, 2].each { it }
	}



	private int private_s_method_with_no_parameters() {
	}

	private int private_s_metod_with_s_parameter(int param_1, float param_2) {
	}

	private int private_s_method_with_s_and_d_parameters(param_1, int param_2) {
	}


	protected int protected_s_method_with_no_parameters() {
	}

	protected int protected_s_metod_with_s_parameter(int param_1, float param_2) {
	}

	protected int protected_s_method_with_s_and_d_parameters(param_1, int param_2) {
	}

	public public_d_method_with_no_parameters() {
	}

	public public_d_metod_with_d_parameter(param_1, param_2) {
	}

	public public_d_method_with_d_and_s_parameters(param_1, int param_2) {
	}



	private private_d_method_with_no_parameters() {
	}

	private private_d_metod_with_d_parameter(param_1, param_2) {
	}

	private private_d_method_with_d_and_s_parameters(param_1, int param_2) {
	}


	protected protected_d_method_with_no_parameters() {
	}

	protected protected_d_metod_with_d_parameter(param_1, param_2) {
	}

	protected protected_d_method_with_d_and_s_parameters(param_1, int param_2) {
	}
}
