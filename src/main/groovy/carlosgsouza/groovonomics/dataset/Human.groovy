package carlosgsouza.groovonomics.dataset

import carlosgsouza.groovonomics.common.FileHandler;

/**
 * Simulates human behavior. 
 * 
 * (Actually this class exists so I can mock wait operations. I just wanted to give it a fancy name) 
 * 
 * @author carlosgsouza
 */
class Human {
	
	def MAX_WAIT_TIME = 60
	def MIN_WAIT_TIME = 10
	
	FileHandler fileHandler
	
	public Human() {
		
	}
	
	public Human(FileHandler fileHandler) {
		this.fileHandler = fileHandler
	}
	
	def think() {
		thinkFor(MIN_WAIT_TIME + new Random().nextInt(MAX_WAIT_TIME - MIN_WAIT_TIME))
	}
	
	def thinkFor(seconds) {
		fileHandler.log("Thinking for $seconds seconds")
		Thread.sleep(seconds * 1000)
	}
}
