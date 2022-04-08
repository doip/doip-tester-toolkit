package doip.tester.utils;

import java.util.List;

import doip.library.timer.NanoTimer;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.event.DoipEvent;

public class Wait {
	
	private static Logger logger = LogManager.getLogger(Wait.class);
	
	/**
	 * Waits that a specific number of DoIP events are stored in a event list.
	 * 
	 * @param events The list of events.
	 * 
	 * @param numberOfEvents The minimum number of events that shall be available
	 *                       in the event list.
	 *                        
	 * @param timeout Timeout in milliseconds
	 * 
	 * @return Returns true if the list contains at least the specific number of
	 *         of events. If the list does not contain the specific number of
	 *         event it returns false.
	 *         
	 * @throws InterruptedException Will be thrown if sleep function will
	 *                              get interrupted.
	 */
	public static boolean waitForEvents(List<DoipEvent> events, int numberOfEvents, long timeout) throws InterruptedException {
		logger.trace(">>> public boolean waitForEvents(List<DoipEvent> events, int numberOfEvents, long timeout) throws InterruptedException");
		logger.debug("Number of events at function entry: " + events.size());
		NanoTimer timer = new NanoTimer();
		long targetTime = timeout * 1000000;
		timer.reset();
		while (timer.getElapsedTime() < targetTime && events.size() < numberOfEvents) {
			try {
				Thread.sleep(0, 1000);
			} catch (InterruptedException e) {
				logger.error("Unexpected " + e.getClass().getName() + " in waitForEvents(...)");
				logger.error(Helper.getExceptionAsString(e));
				throw e;
			}
		}
		
		logger.debug("Number of events at function exit: " + events.size());
		logger.trace("<<< public boolean waitForEvents(List<DoipEvent> events, int numberOfEvents, long timeout) throws InterruptedException");
		return events.size() >= numberOfEvents;
	}
}
