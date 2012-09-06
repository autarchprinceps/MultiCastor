package zisko.multicastor.program.model;
import java.util.Queue;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import zisko.multicastor.program.controller.ViewController;

public class MessageCheck extends TimerTask{
	private Queue<String> messageQueue;
	private String message;
	private ViewController viewController;
	private Logger logger;
	
	public MessageCheck(Queue<String> messageQueue, ViewController viewController, Logger logger){
		super();
		this.messageQueue = messageQueue;
		this.viewController = viewController;
		this.logger = logger;
	}
	
	@Override
	public void run() {
		message = messageQueue.poll();
		while(message != null){
			if(message.substring(0, 8).equals("[Fehler]")){
			//	viewController.showMessage(ViewController.MessageTyp.ERROR, message.substring(8));
				logger.log(Level.WARNING, message.substring(8));
			} else {
				logger.log(Level.INFO, message.substring(8));
			}
			message = messageQueue.poll();
		}
	}

}
