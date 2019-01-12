package com.eklanku.otuChat.btprint;

import java.io.IOException;

public abstract class iConnection {

	public iConnection() {
	}

	abstract boolean Disconnect();

	abstract boolean isConnected();

	abstract boolean Connect() throws IOException;
}