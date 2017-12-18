import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
public class TokenBucket {
private int tokenrate;
private int capactity;
private long token;
long lastTime;
public TokenBucket(int cap, int trate) {
	tokenrate = trate;
	capactity = cap;
	token = 0;
	lastTime = System.currentTimeMillis();
}

synchronized public void updateToken() {
	long now = System.currentTimeMillis(); 
	token += (now - lastTime)*tokenrate/1000;
	if(token > capactity) {
		token = capactity;
	}
	lastTime = now;
}

public void send() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	System.out.println("send at time " + sdf.format(new Date()));
}
public void sendWithWait() {
	while(true) {
		updateToken();
		synchronized (this) {
			if(token > 1) {
				token--;
				break;
			}
			else {
				double waitTime = 1.0/tokenrate*1000;
				try {
					Thread.sleep((long) waitTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	send();
}

static class RequestGenerator extends TimerTask {
	public RequestGenerator(TokenBucket a) {
		b = a;
	}
	TokenBucket b;
	public void run() {
		b.sendWithWait();
	}
}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		TokenBucket bucket =new TokenBucket(5,5);
		TimerTask task1 = new RequestGenerator(bucket);
		TimerTask task2 = new RequestGenerator(bucket);
		timer.scheduleAtFixedRate(task1 ,600L,1L);
		timer.scheduleAtFixedRate(task2 ,500L,1L);
	}

}
