package doip.tester.utils;

import doip.tester.event.DoipEvent;

public interface DoipTcpConnectionWithEventCollectionListener {
	
	public void onEvent(DoipTcpConnectionWithEventCollection conn, DoipEvent event);

}
