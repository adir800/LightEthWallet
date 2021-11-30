package entity;

import java.util.ArrayList;

public class Channels {

	public ArrayList<Channel> channels;

	public Channels() {
		this.channels = new ArrayList<Channel>();
	}

	public float getChannelsSum() {
		float sum = 0;
		for (Channel ch : channels) {
			sum = sum + ch.balance;
		}
		return sum;
	}

	public float getPositiveBalance() {
		float positiveSum = 0;
		for (Channel ch : channels) {
			if(ch.balance > 0) {
				positiveSum = positiveSum + ch.balance;
			}
		}
		return positiveSum;
	}
	
	public float getNegativeBalance() {
		float negativeSum = 0;
		for (Channel ch : channels) {
			if(ch.balance < 0) {
				negativeSum = negativeSum + ch.balance;
			}
		}
		return negativeSum;
	}

}
